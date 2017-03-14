package io.github.ray.xsession.event;

import io.github.ray.xcache.redis.RedisClient;
import io.github.ray.xcache.redis.RedisClientFactory;
import io.github.ray.xsession.Session;
import io.github.ray.xsession.SessionConfig;
import io.github.ray.xsession.constant.XSessionConstant;
import io.github.ray.xsession.http.XSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Set;

public class XSessionExpireEventProcess {
	
	private static final Logger log = LoggerFactory.getLogger(XSessionExpireEventProcess.class);
	
	/**
	 * 监听器列表
	 */
	private Set<HttpSessionListener> listeners = null; 
	/**
	 * 事件处理器
	 */
	private SessionExpireHandler eventHandler = null;
	
	private RedisClient redis = RedisClientFactory.getRedisClient();
	
	
	private Thread master;
	private Thread slave;
	
	class MasterThread extends Thread{
		public void run(){
			while(true){
				try{
					redis.psubscribe(eventHandler, XSessionConstant.SESSION_EXPRIED);
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
					Thread.sleep(10000);
					eventHandler.punsubscribe(XSessionConstant.SESSION_EXPRIED);
				}catch(Exception je){
					log.error(je.getMessage(), je);
				}
			}
		}
	}

	public void initEvent() {
		listeners = SessionConfig.getInstance().getListeners();
		if(SessionConfig.getInstance().isSupportExpireEvent() && listeners != null && listeners.size() > 0){
			eventHandler = new SessionExpireHandler();
			master = new MasterThread();
			slave = new SlaveThread();
			slave.start();
			master.start();
		}
	}
	

	class SessionExpireHandler extends JedisPubSub {
		public void onPSubscribe(String pattern, int subscribedChannels) {
			log.debug("XSession 过期事件监听器[{}-{}]启动成功!", pattern, subscribedChannels);
		}
		
		public void onPMessage(String pattern, String channel, String message) {
			log.debug("XSession[{}] 过期事件触发[{}-{}]", message, pattern, channel);
			try{
				
				if(XSessionConstant.SESSION_EXPRIED.equals(channel)){
					if(message.startsWith(Session.PREFIX)){
						String ids[] = message.split("#");
						if(ids.length == 2){
							HttpSessionEvent event = new HttpSessionEvent(XSession.buildSessionOnlyId(ids[1]));
							for(HttpSessionListener l : listeners){
								l.sessionDestroyed(event);
							}
						}
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
