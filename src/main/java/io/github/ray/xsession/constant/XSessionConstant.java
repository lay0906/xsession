package io.github.ray.xsession.constant;

import io.github.ray.xsession.SessionConfig;


public class XSessionConstant {
	
	/**
	 * 默认超时时间(20分钟)
	 */
	public static final int DEFAULT_MAX_INACTIVE_INTERVAL = 20;
	
	/**
	 * session超时时间-默认20分钟
	 */
	public static final Integer DEFAULT_SESSION_TIMEOUT = 20;
	
	/**
	 * session创建事件channel
	 */
	private static  String SESSION_CREATE;
	
	/**
	 * session销毁事件channel
	 */
	private static  String SESSION_DEL;
	
	/**
	 * session过期事件channel
	 */
	public static final String SESSION_EXPRIED = "__keyevent@0__:expired";
	
	public static final String SESSION_CREATE_KEY(){
		if(SESSION_CREATE == null){
			synchronized (XSessionConstant.class) {
				SESSION_CREATE = "session-" + SessionConfig.getInstance().getCtx() + "#create";
			}
		}
		return SESSION_CREATE;
	}
	
	public static final String SESSION_DEL_KEY(){
		if(SESSION_DEL == null){
			synchronized (XSessionConstant.class) {
				SESSION_DEL = "session-" + SessionConfig.getInstance().getCtx() + "#del";
			}
		}
		return SESSION_DEL;
	}
}
