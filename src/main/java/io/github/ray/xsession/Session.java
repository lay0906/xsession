package io.github.ray.xsession;

import java.util.Set;

public interface Session {

	String PREFIX = "sessionID#";
	String CREATIONTIME = "creationTime";
	String LASTACCESSEDTIME = "lastAccessedTime";
	String MAXINACTIVEINTERVAL = "maxInactiveInterval";

	String getId();

	Object getAttribute(String attributeName);

	Set<String> getAttributeNames();

	void setAttribute(String attributeName, Object attributeValue);

	void removeAttribute(String attributeName);

	long getCreationTime();

	void setCreationTime(long creationTime);

	void setLastAccessedTime(long lastAccessedTime);

	long getLastAccessedTime();

	void setMaxInactiveInterval(int interval);

	int getMaxInactiveInterval();

	boolean isExpired();

	boolean isNew();

	void setNew(boolean isNew);
}
