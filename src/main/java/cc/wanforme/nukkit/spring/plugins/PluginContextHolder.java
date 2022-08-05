package cc.wanforme.nukkit.spring.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.spring.plugins.loader.ExtBeanClassLoader;
import cc.wanforme.nukkit.spring.plugins.loader.ExtPluginLoader;
import cc.wanforme.nukkit.spring.plugins.loader.ExtResourceLoader;
import cc.wanforme.nukkit.spring.starter.PnxProperties;
import cc.wanforme.nukkit.spring.starter.PnxStartHandler;
import cc.wanforme.nukkit.spring.util.NukkitServerUtil;
import cc.wanforme.nukkit.spring.util.PathResource;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLoader;
import cn.nukkit.plugin.PluginManager;

/** 上下文辅助类<br>
 * 如果启用 nukkit，那么还将 创建一个新的 context 加载插件
 * @author wanne
 * 2020年7月7日
 */
@Component
public class PluginContextHolder implements ApplicationContextAware{
	private static final Logger log = LoggerFactory.getLogger(PluginContextHolder.class);
	public static final String EXT_PLUGIN_LOADER = "extPluginLoader";
	
	
	@Autowired
	private PnxProperties properties;
	@Autowired
	private PnxStartHandler nukkitStartHandler;
	@Autowired
	private ExtPluginLoader pluginLoader;
	
	// 当前SpringBoot的 context 
	private ApplicationContext context;
	// 插件的 context
	private AnnotationConfigApplicationContext pluginContext;
	// 插件加载器（默认，设置SpringBoot#applicationContext时直接加入）
	private Map<String, PluginLoader> pluginLoaders = new HashMap<>(1);

	// 初始化pluginContext时（前）读取到的 PluginDescription (key是对应的插件文件)
	private Map<File, PluginDescription> pluginDescriptions = new HashMap<>();
	
	// 插件类加载器 (pluginContext初始化前)
	private ExtBeanClassLoader classLoader;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		pluginLoaders.put(EXT_PLUGIN_LOADER, pluginLoader);
	}
	
	
	/** 读取插件nsplugins, 开放给外部直接调用
	 */
	public void loadPlugins() {
		// 检查nukkit是否启用，并且启动了nukkit
		// 注：不检查 nukkit 是否启动
		if(!properties.isEnable()) {
			return ;
		}
		
		// 加载插件的所有类
		classLoader = this.loadClasses();
		
		if(classLoader != null && nukkitStartHandler.isNukkitStarted()) {
			List<File> fs = this.urlToFile(classLoader.getURLs());
			
			// 初始化 PluginContext, 并预先读取所有插件的 PluginDescription
			this.initPluginContext(fs);
			
			// 加载插件
			List<Plugin> plugins = this.loadPlugins(fs);
			
			// 启用插件
			this.enablePlugins(plugins);
		} else {
			log.warn("There's no plugin, or error occured while loading plugins!");
		}
		
	}
	
	/** 获取当前SpringBoot的 context，*/
	public ApplicationContext getApplicationContext() {
		return context;
	}
	
	/** 获取当前加载插件的 context，*/
	public ApplicationContext getPluginApplicationContext() {
		return pluginContext;
	}
	
	/** 获取某个插件文件的 PluginDescription*/
	public PluginDescription getPluginDescription(File file){
		return pluginDescriptions.get(file);
	}
	
	/** 获取外部插件的class*/
	public Class<?> getPluginClass(String name){
		if(classLoader!=null && name != null) {
			try {
				return classLoader.loadClass(name);
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	/** 加载插件的所有类,并返回类加载器*/
	private ExtBeanClassLoader loadClasses() {
		// 获取项目路径，然后获取到插件路径
//		File projectLocation = PathResource.getProjectLocation();
		File projectLocation = new File(System.getProperty("user.dir"));
		String[] dirs = properties.getNukkitSpringPluginLocation().split("\\,");
		log.info("[nsplugins] location: " + Arrays.toString(dirs));
		
//		URL[] uris = new URL[0];
		List<URL> uriList = new ArrayList<>();
		for (String dir : dirs) {
			File pluginDir = new File(projectLocation, dir);
			if (!pluginDir.exists() || !pluginDir.isDirectory()) {
				pluginDir.mkdir();
			}
			
			// 获取所有的文件，并过滤文件类型
			// ExtPluginLoader 会再过滤一次文件类型
			log.info("[nsplugins] " + pluginDir.getAbsolutePath());
			
			File[] files = pluginDir.listFiles();
			List<File> fs = new ArrayList<>(Arrays.asList(files));

			// 获取到插件目录下的所有插件
			List<URL> urisTemp = fs.stream().filter(f -> PathResource.isJarOrDirectory(f)).map(File::toURI).map(t -> {
				try {
					return t.toURL();
				} catch (MalformedURLException e) {
					log.error("NSPlugin load error! ["+t.getPath()+"]", e);
				}
				return null;
			}).filter(e -> e != null).collect(Collectors.toList());
			
			uriList.addAll(urisTemp);
		}
		
		URL[] uris = uriList.toArray(new URL[0]);
		if (uris.length > 0) {
			// 实例化类加载器，加载所有的类
			ExtBeanClassLoader classLoader = new ExtBeanClassLoader(uris,
					Thread.currentThread().getContextClassLoader());
			return classLoader;
		}
		return null;	
	}
	
	/** 初始化插件加载器的 context*/
	private void initPluginContext(final List<File> fs){
		// 预先读取 pluginDescription， 并从中找出主类的所在位置。
		// 主类的包即为 spring 自动扫描的目录
		//（也就是说，主类必须在整个包的最外层目录下）
		List<String> basePackage = new ArrayList<>(fs.size());
		for (File file : fs) {
			PluginDescription des = ExtPluginLoader.readPluginDescription(file);
			if(des != null) {
				pluginDescriptions.put(file, des);
				
				String main = des.getMain();
				int lastDot = main.lastIndexOf('.');
				
				// 使用默认包的情况 
				// TODO 仍然需要重新处理
				if(lastDot == -1) {
					log.warn(" Plugin - [{}] use default package, that's deprecated!", des.getName());
					main = "";
				} else {
					main = main.substring(0, lastDot);
				}
				
				basePackage.add(main);
			}
		}
		
		// 创建插件的 context 
		pluginContext = new AnnotationConfigApplicationContext();
		pluginContext.setClassLoader(classLoader);
		// 修改 context 资源加载器
		pluginContext.setResourceLoader(new ExtResourceLoader(classLoader));
		// 设置 spring 的 context 为 parent-context
		pluginContext.setParent(context);
		
		if(!basePackage.isEmpty()) {
			pluginContext.scan(basePackage.toArray(new String[0]));
		}
		pluginContext.refresh();
	}
	
	/** 真正加载插件的地方*/ 
	private List<Plugin> loadPlugins(List<File> fs) {
		// 获取 PluginManager
		PluginManager pluginManager = NukkitServerUtil.getPluginManager();
		List<Plugin> list= new ArrayList<>(fs.size());
		if (pluginManager != null) {
			for (int i = 0; i < fs.size(); i++) {
				File f = fs.get(i);
				
				Plugin plugin = pluginManager.loadPlugin(f, pluginLoaders);
				if (plugin != null) {
					list.add(plugin);
				} 
			}
		} else {
			log.warn("It seems the server is not started, during loading plugins!");
		}
		return list;
	}
	
	/** 启用插件*/
	private void enablePlugins(List<Plugin> plugins) {
		PluginManager pluginManager = NukkitServerUtil.getPluginManager();
		for (Plugin plugin : plugins) {
			pluginManager.enablePlugin(plugin);
		}
	}
	
	private List<File> urlToFile(URL[] urls) {
		List<File> fs = new ArrayList<>(urls.length);

		for (int i = 0; i < urls.length; i++) {
			try {
				fs.add(new File(urls[i].toURI()));
			} catch (URISyntaxException e) {
				log.error("Error occured while loading plugin [" + urls[i] + "]", e);
			}
		}
		return fs;
	}
	
}
