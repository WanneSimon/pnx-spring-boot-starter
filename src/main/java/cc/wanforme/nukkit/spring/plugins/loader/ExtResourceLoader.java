package cc.wanforme.nukkit.spring.plugins.loader;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/** 插件的 Spring context 的资源加载器<br>
 * 未做任何实现，仅仅用于后续扩展
 * @author wanne
 * 2020年7月11日
 */
public class ExtResourceLoader extends DefaultResourceLoader{

	public ExtResourceLoader(ExtBeanClassLoader classLoader) {
		super.setClassLoader(classLoader);
	}
	
	@Override
	public Resource getResource(String location) {
		Resource resource =  super.getResource(location);
		return resource;
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return super.getClassLoader();
	}
	
}
