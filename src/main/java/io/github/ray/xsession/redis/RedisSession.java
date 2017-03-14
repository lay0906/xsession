package io.github.ray.xsession.redis;

import io.github.ray.xsession.Session;
import io.github.ray.xsession.SessionConfig;
import io.github.ray.xsession.constant.XSessionConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisSession implements Session {
	
	/**
	 * session id
	 */
	private String id;
	/**
	 * 保存属性的Map
	 */
	private Map<String, Object> attrs = new HashMap<String, Object>();
	/**
	 * 创建时间
	 */
	private long creationTime = System.currentTimeMillis();
	/**
	 * 最近访问时间
	 */
	private long lastAccessedTime = creationTime;
	/**
	 * 超时时间
	 */
	private int maxInactiveInterval = XSessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL * 60;
	/**
	 * 是否新创建
	 */
	private boolean isNew;
	
    public RedisSession(String id) {
    	this.id = id;
    	maxInactiveInterval = SessionConfig.getInstance().getSessionTimeOut() * 60;
    }
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Object getAttribute(String attributeName) {
		return attrs.get(attributeName);
	}
	
	@Override
	public Set<String> getAttributeNames() {
		return attrs.keySet();
	}
	
	@Override
	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeValue == null) {
			removeAttribute(attributeName);
		} else {
			attrs.put(attributeName, attributeValue);
		}
	}
	
	@Override
	public void removeAttribute(String attributeName) {
		attrs.remove(attributeName);
	}
	
	@Override
	public long getCreationTime() {
		return creationTime;
	}
	
	@Override
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	@Override
	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
	
	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
	
	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}
	
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	@Override
	public boolean isExpired() {
		return isExpired(System.currentTimeMillis());
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}
	
	@Override
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	boolean isExpired(long now) {
		if (maxInactiveInterval < 0) {
			return false;
		}
		return now - TimeUnit.SECONDS.toMillis(maxInactiveInterval) >= lastAccessedTime;
	}
	
	public Map<String, Object> getAttrs(){
		return attrs;
	}
}
