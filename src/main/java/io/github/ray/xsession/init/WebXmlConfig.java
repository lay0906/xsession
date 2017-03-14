package io.github.ray.xsession.init;

import io.github.ray.xsession.constant.XSessionConstant;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionListener;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebXmlConfig {
	
	private static Logger log = LoggerFactory.getLogger(WebXmlConfig.class);
	
	private Integer sessionTimeOut = XSessionConstant.DEFAULT_SESSION_TIMEOUT;
	
	/**
	 * session创建事件监听器
	 */
	private Set<HttpSessionListener> sessionListeners = new HashSet<HttpSessionListener>();
	
	
	private  WebXmlConfig(){
		WebXmlConfigParser parser = new WebXmlConfigParser();
		parser.parse();
	}
	
	private static volatile WebXmlConfig INS = null;
	
	private static WebXmlConfig getWebXmlConfig(){
		if(INS == null){
			synchronized (WebXmlConfig.class) {
				if(INS == null){
					INS = new WebXmlConfig();
				}
            }
		}
		return INS;
	}
	
	public static Integer getSessionTimeOut() {
		return getWebXmlConfig().sessionTimeOut;
	}
	
	public static Set<HttpSessionListener> getSessionListeners() {
		return getWebXmlConfig().sessionListeners;
	}
	
	class WebXmlConfigParser{
		
		String getWebXmlPath(){
			String path = WebXmlConfigParser.class.getResource("/").getPath();
			int ids = path.indexOf("/classes/");
			if(ids != -1){
				return path.substring(0, ids) + "/web.xml";
			}
			return null;
		}
		
        void parse(){
			FileInputStream in = null;
			Document doc = null;
			Element root = null;
			try {
	            in = new FileInputStream(getWebXmlPath());
	            SAXBuilder sb = new SAXBuilder();
	            doc = sb.build(in);
	            root = doc.getRootElement();
	            initSessionTimeout(root);
	            initHttpSessionListener(root);
            } catch (Exception e) {
            	log.warn("web.xml解析失败，使用默认配置", e);
	            defaultConfig();
            }finally{
            	if(in != null){
            		try{
            			in.close();
            		}catch(Exception e){
            			log.warn(e.getMessage(), e);
            		}
            	}
            }
		}
		
		void defaultConfig(){
			sessionTimeOut = XSessionConstant.DEFAULT_SESSION_TIMEOUT;
			sessionListeners = null;
		}
		
		Class<?> getHttpSessionListenerClass(String clazz){
			Class<?> c = null;
			try {
	            c = Class.forName(clazz);
	            if(HttpSessionListener.class.isAssignableFrom(c))
	            	return c;
            } catch (ClassNotFoundException e) {
//            	log.error(e.getMessage(), e);
            }
			return null;
		}
		
		void addHttpSessionListener(Class<?> c){
			Object o = null;
			try {
	            o = c.newInstance();
	            if(o instanceof HttpSessionListener){
	            	sessionListeners.add((HttpSessionListener)o);
	            }
            } catch (Exception e) {
            	throw new RuntimeException("监听器初始化失败!", e);
            }
		}
		
		void initSessionTimeout(Element root){
			try{
				
				XPath xpath = XPath.newInstance("//ns:session-timeout");
		        xpath.addNamespace("ns", "http://java.sun.com/xml/ns/javaee");
		        Element sessionTimeOutEl = (Element)xpath.selectSingleNode(root);
				if(sessionTimeOutEl != null){
					sessionTimeOut = Integer.parseInt(sessionTimeOutEl.getValue());
				}
			}catch(Exception e){
				log.debug("session过期时间解析失败，使用默认时间[{}]", sessionTimeOut);
			}
		}
		
		@SuppressWarnings("unchecked")
		void initHttpSessionListener(Element root){
			try{
				XPath xpath = XPath.newInstance("//ns:listener-class");
		        xpath.addNamespace("ns", "http://java.sun.com/xml/ns/javaee");
                List<Element> listenerList = (List<Element>)xpath.selectNodes(root);
				if(listenerList != null && listenerList.size() > 0){
					for(Element e : listenerList){
						String v = e.getValue();
						if(v == null || v.length() == 0)
							continue;
						Class<?> c = getHttpSessionListenerClass(v);
						if(c == null)
							continue;
						addHttpSessionListener(c);
					}
				}
			}catch(Exception e){
				throw new RuntimeException("监听器初始化失败!", e);
			}
		}
	}
}
