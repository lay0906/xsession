package io.github.ray.xsession.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SessionProperties {
	
	private static final Logger log = LoggerFactory.getLogger(SessionProperties.class);
	
	private static Properties sessionProperties = null;

	private static final class SessionPropertiesHolder {
		private static final SessionProperties INSTANCE = new SessionProperties();
	}

	public static SessionProperties getInstance() {
		return SessionPropertiesHolder.INSTANCE;
	}

	private SessionProperties() {
		init();
	}

	private void init() {
		sessionProperties = new Properties();
		InputStream is = null;
		is = SessionProperties.class.getClassLoader().getResourceAsStream(
				"session.properties");
		try {
			sessionProperties.load(is);
		} catch (Exception e) {
			log.error("读取{}失败！", "session.properties");
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	
	public String getProperty(String key, String defaultValue) {
		return sessionProperties.getProperty(key, defaultValue);
	}
	
	public String getProperty(String key) {
		return getProperty(key, null);
	}

	public static Integer getSessionTimeout(){
		String v = getInstance().getProperty("session.timeout");
		if(v != null){
			try{
				return Integer.parseInt(v);
			}catch(Exception e){}
		}
		return null;
	}
	
	public static boolean getSessionEnable(){
		String v = getInstance().getProperty("session.enable");
		if (v != null && v.toLowerCase().equals("false")) {
			return false;
		}
		return true;
	}
	
	public static boolean getSupportExpireEvent(){
		String v = getInstance().getProperty("session.supportExpireEvent");
		if (v != null && v.toLowerCase().equals("true")) {
			return true;
		}
		return false;
	}

}
