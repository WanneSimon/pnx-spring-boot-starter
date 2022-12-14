package cc.wanforme.nukkit.spring.plugins.lang;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.wanforme.nukkit.spring.util.ResourceSaver;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;

/** 多语言容器<br>
 * 规则：所有语言文件放在一个 basePackage 下，该文件夹只能放语言文件<br>
 * 每个中语言类型对应一个文件。如： 'en' -> 'en.yml' (如果语言文件类型是yml)
 * 创建实例后，每个语言文件对应一个Config。<br>
 * 用法：<br>
 * 首先新建一个此对象，然后在 {@link cn.nukkit.plugin.Plugin#onLoad()} 方法中，调用初始化方法 {@link #init(boolean)}
 * @author wanne
 * 2020年7月22日
 */
public abstract class PluginLangHolder {
	private static final Logger log = LoggerFactory.getLogger(PluginLangHolder.class);
	// 语言容器所属 插件
	private Plugin plugin;
	
	// 读取进来的语言，可能是所有的语言，也可能只有正在使用中的语言
	private Map<String, Config> langs = new HashMap<>();
	// 文件类型
	private final ConfigFileType type;
	// 当前使用的语言
	private final String current;
	// 是否加载所有语言
//	private final boolean loadAllLangs;
	// 语言文件所在基础路径
	private final String basePackage;
	
	
	/** 只读取传入的语言。其它语言不读取
	 * @param type 语言类型 {@link cn.nukkit.utils.Config}
	 * @param basePackage 语言文件所在基础路径
	 * @param lang 使用的语言
	 * @param defaultLang 默认语言
	 * @param loadAll 是否将所有语言文件读入进来
	 */
	public PluginLangHolder(Plugin plugin, ConfigFileType type, String basePackage, String lang) {
		this.plugin = plugin;
		this.type = type;
//		this.loadAllLangs = loadAll;
		this.current = lang;
		this.basePackage = basePackage;
		
//		this.checkSaveAndLoad(loadAll);
	}
	
	/** 初始化语言容器，检查保存内部语言文件，并加载语言*,
	 * @param loadAll 是否预先加载所有的语言
	 * @throws IOException 
	 */
	public void init(boolean loadAll) {
		// 检查并保存所有的语言文件
		// 拿到插件jar文件所在路径/G:/Minecraft/Nukkit/ns_test/nsplugins/CMLogin-1.0-SNAPSHOT.jar
//		String file = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		// 保存jar内部的语言文件到数据文件夹内 (pluginDataFolder)
		try {
			ResourceSaver.savePluginResources(plugin, basePackage);
		} catch (RuntimeException e) {
			log.info("cound not save nsplugin ["+plugin.getName()+"] languages folder!" , e);
			throw new RuntimeException();
		}
		
		// 检查语言文件
		File parent = new File(plugin.getDataFolder(), basePackage);
		if(!parent.exists() || !parent.isDirectory()) {
			throw new RuntimeException("There's no language files, please check base package '"+parent.getAbsolutePath()+"'");
		}
//		
		if(loadAll) {
			File[] fs = parent.listFiles();
			if(fs == null || fs.length == 0) {
				throw new RuntimeException("There's no language files, please check base package '"+parent.getAbsolutePath()+"'");
			}
			
			for (File f : fs) {
				String name = f.getName();
				String lang_f = name;
				int index = name.lastIndexOf('.');
				if(index != -1) {
					lang_f = lang_f.substring(0, index);
				}
				this.loadLang(lang_f, f, type);
			}
		} else {
			File f = new File(parent, current+type.getType());
			if(!f.exists()) {
				throw new RuntimeException("There's no language file, please check it out '"+f.getAbsolutePath()+"'");
			}
			
			this.loadLang(current, f, type);
		}
	}
	
	/**
	 * @param lang 语言类型
	 * @param f 语言文件
	 * @param type 文件类型
	 * @return
	 */
	private Config loadLang(String lang, File f, ConfigFileType type) {
		try {
			Config c = new Config(f, type.getConfigCode());
			this.langs.put(lang, c);
			return c;
		} catch (Exception e) {
			log.error("load lang file ["+f.getAbsolutePath()+"] failed !", e);
		}
		return null;
	}
	
	/** 设置语言类型, 加载并设置成功时，返回语言文件，错误时返回null*/
	public Config setLang(String name) {
		Config c = langs.get(name);
		if(c == null) {
			c = this.loadLang(name, new File(plugin.getDataFolder().getName()+"/"+basePackage, name+type.getType()), type);
		}
		return c;
	}

	/** 刷新，并返回当前的语言配置对象*/
	public Config refresh() {
		langs.keySet().forEach( e -> {
			Config lang = this.loadLang(e, new File(plugin.getDataFolder().getName()+"/"+basePackage,  e+type.getType()), type);
			if(lang == null) {
				this.langs.remove(e);
			} else {
				this.langs.put(e, lang);
			}
		});
		
		return this.getCurrentConfig();
	}
	
	/** 获取语言文件的文件类型*/
	public ConfigFileType getType() {
		return type;
	}
	
	/** 获取当前语言明*/
	public String getCurrentLang() {
		return current;
	}

	/** 获取已加载的所有语言*/
	public Set<String> getLoadedLangs(){
		return langs.keySet();
	}
	
	/** 获取某个语言文件*/
	public Config getLangConfig(String lang) {
		return langs.get(lang);
	}
	
	/** 获取正在使用中的语言文件*/
	public Config getCurrentConfig() {
		return langs.get(current);
	}
	
	/** 获取文件中配置的语言*/
	public String get(String key) {
		return this.get(key, "{"+key+"}");
	}
	
	/** 获取文件中配置的语言*/
	public String get(String key, String defaultValue) {
		return getCurrentConfig().getString(key, defaultValue);
	}

}
