package io.github.ray.xsession.event;

import io.github.ray.xcache.redis.RedisClient;
import io.github.ray.xcache.redis.RedisClientFactory;
import io.github.ray.xsession.SessionConfig;
import io.github.ray.xsession.constant.XSessionConstant;
import io.github.ray.xsession.http.XSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Set;

public class XSessionEventProcess {
	
	private static final Logger log = LoggerFactory.getLogger(XSessionEventProcess.class);
	
	/**
	 * 监听器列表
	 */
	private Set<HttpSessionListener> listeners = null; 
	/**
	 * create-destory事件处理器
	 */
	private SessionEventHandler eventHandler = null;
	
	private RedisClient redis = RedisClientFactory.getRedisClient();
	
	
	private Thread master;
	private Thread slave;
	
	private String key;
	
	public XSessionEventProcess(){
		key = "session-" + SessionConfig.getInstance().getCtx() + "*";
	}
	
	class MasterThread extends Thread{
		public void run(){
			while(true){
				try{
					redis.psubscribe(eventHandler, key);
				}catch(Exception je){
					log.error(je.getMessage(), je);
				}
			}
		}
	}
	
	class SlaveThread extends Thread{
		public void run(){
			while(true){
				try{
					Thread.sleep(600000);
					eventHandler.punsubscribe(key);
				}catch(Exception je){
					log.error(je.getMessage(), je);
				}
			}
		}
	}
	

	public void initEvent() {
		listeners = SessionConfig.getInstance().getListeners();
		if(listeners != null && listeners.size() > 0){
			eventHandler = new SessionEventHandler();
			master = new MasterThread();
			slave = new SlaveThread();
			slave.start();
			master.start();
		}
	}
	

	class SessionEventHandler extends JedisPubSub {
		public void onPSubscribe(String pattern, int subscribedChannels) {
			log.debug("XSession 事件监听器[{}-{}]启动成功!", pattern, subscribedChannels);
		}
		
		public void onPMessage(String pattern, String channel, String message) {
			log.debug("XSession[{}] 事件触发[{}-{}]", message, pattern, channel);
			try{
				HttpSessionEvent event = new HttpSessionEvent(XSession.buildSessionOnlyId(message));
				if(XSessionConstant.SESSION_CREATE_KEY().equals(channel)){
					for(HttpSessionListener l : listeners){
						l.sessionCreated(event);
					}
				}else if(XSessionConstant.SESSION_DEL_KEY().equals(channel)){
					for(HttpSessionListener l : listeners){
						l.sessionDestroyed(event);
					}
				}else{
					log.warn("Hiksession[{}] 事件类型[{}]不支持", message, channel);
				}
			}catch(Exception e){
				log.error("XSession[{}] 事件处理发生错误，事件[{}]", message, channel);
			}
		}
	}
}
