package com.mii.sscintegration.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mii.sscintegration.domain.ConfigurationValue;


/**
 * ConfigFile class is used to read configuration file properties
 * @see srtconfig.properties
 * @author MII-ferry.hendrayana
 * @version 1.0
 * @since 2018-04-29
 */


@Configuration
@ComponentScan(basePackages = { "com.mii.sscintegration.*" })
@PropertySource("classpath:sscconfig.properties")
public class ConfigFile {
	@Autowired
	private Environment env;
	
	/**
	 * This method is used to set the configuration value based on configuration file properties
	 * 
	 */
	@Bean
	public ConfigurationValue getConfigurationValue() {
		return new ConfigurationValue(
				env.getProperty("remedy.server"),
				env.getProperty("remedy.username"),
				env.getProperty("remedy.password"),
				env.getProperty("remedy.port"),
				env.getProperty("filenet.url"),
				env.getProperty("filenet.password"),
				env.getProperty("filenet.user"), 
				env.getProperty("filenet.objectStore"),
				env.getProperty("filenet.middleform"),
				env.getProperty("remedy.travel.settlement.form"),
				env.getProperty("remedy.travle.advance.form"),
				env.getProperty("traveloka.payment"),
				env.getProperty("sap.wstrip"),
				env.getProperty("sap.credential"),
				env.getProperty("remedy.pulldata.permenit"),
				env.getProperty("pertamina.proxy")
				);
	}
}
