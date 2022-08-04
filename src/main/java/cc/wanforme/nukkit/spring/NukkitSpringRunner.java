package cc.wanforme.nukkit.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.spring.starter.PnxProperties;
import cc.wanforme.nukkit.spring.starter.PnxStartHandler;

/**
 * @author wanne
 * 2022-08-03
 */
@Component
@EnableConfigurationProperties(PnxProperties.class)
public class NukkitSpringRunner implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(NukkitSpringRunner.class);
	
	@Autowired
	private PnxProperties properties;
	@Autowired
	private PnxStartHandler nukkitStartHandler;
	
	@Override
	public void run(String... args) throws Exception {
		log.info(properties.toString());
		
		if(properties.isEnable()) {
			if(properties.isStartNukkit()) {
				nukkitStartHandler.runNukkit(args);
			}
		}
	}

	
}
