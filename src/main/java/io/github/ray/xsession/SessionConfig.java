package io.github.ray.xsession;

import javax.servlet.http.HttpSessionListener;
import java.io.Serializable;
import java.util.Set;

public class SessionConfig implements Serializable{

	private static final long serialVersionUID = 1560007972175322382L;
	
	
	private SessionConfig(){}
	
	private static SessionConfig INS = new SessionConfig();
	
	public static SessionConfig getInstance(){
		return INS;
	}
	
	/**
	 * session 超时时间
	 */
	private Integer sessionTimeOut = null;
	/**
	 * 是否启用Cluster Session
	 */
	private boolean isEnable = true;
	/**
	 * 是否支持过期时间
	 */
	private boolean isSupportExpireEvent = false;
	/**
	 * Session事件集合
	 */
	private Set<HttpSessionListener> listeners = null;
	/**
	 * 应用上下文
	 */
	private String ctx;

	public Integer getSessionTimeOut() {
		return sessionTimeOut;
	}

	public void setSessionTimeOut(Integer sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public boolean isSupportExpireEvent() {
		return isSupportExpireEvent;
	}

	public void setSupportExpireEvent(boolean isSupportExpireEvent) {
		this.isSupportExpireEvent = isSupportExpireEvent;
	}

	public Set<HttpSessionListener> getListeners() {
		return listeners;
	}

	public void setListeners(Set<HttpSessionListener> listeners) {
		this.listeners = listeners;
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}
}
