package io.github.ray.xsession.http;

import io.github.ray.xsession.Session;
import io.github.ray.xsession.redis.RedisSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Set;

public class XSession implements HttpSession{

	private Session session;
	private ServletContext servletContext;
	private boolean invalidated;
	private boolean old;
	
	public static XSession buildSessionOnlyId(String id){
		Session s = new RedisSession(id);
		return new XSession(s, null);
	}

	public XSession(Session session, ServletContext servletContext) {
		this.session = session;
		this.servletContext = servletContext;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public long getCreationTime() {
		checkState();
		return session.getCreationTime();
	}

	public String getId() {
		return session.getId();
	}

	public long getLastAccessedTime() {
		checkState();
		return session.getLastAccessedTime();
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	public HttpSessionContext getSessionContext() {
		return NOOP_SESSION_CONTEXT;
	}

	public Object getAttribute(String name) {
		checkState();
		return session.getAttribute(name);
	}

	public Object getValue(String name) {
		return getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		checkState();
		return Collections.enumeration(session.getAttributeNames());
	}

	public String[] getValueNames() {
		checkState();
		Set<String> attrs = session.getAttributeNames();
		return attrs.toArray(new String[0]);
	}

	public void setAttribute(String name, Object value) {
		checkState();
		session.setAttribute(name, value);
	}

	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		checkState();
		session.removeAttribute(name);
	}

	public void removeValue(String name) {
		removeAttribute(name);
	}

	public void invalidate() {
		checkState();
		this.invalidated = true;
	}

	public void setNew(boolean isNew) {
		this.old = !isNew;
	}

	public boolean isNew() {
		checkState();
		return !old;
	}

	private void checkState() {
		if(invalidated) {
			throw new IllegalStateException("The HttpSession has already be invalidated.");
		}
	}

	private static final HttpSessionContext NOOP_SESSION_CONTEXT = new HttpSessionContext() {
		public HttpSession getSession(String sessionId) {
			return null;
		}

		public Enumeration<String> getIds() {
			return EMPTY_ENUMERATION;
		}
	};

	private static final Enumeration<String> EMPTY_ENUMERATION = new Enumeration<String>() {
		public boolean hasMoreElements() {
			return false;
		}

		public String nextElement() {
			throw new NoSuchElementException("a");
		}
	};
}
