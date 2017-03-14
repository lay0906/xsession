package io.github.ray.xsession.http;

import io.github.ray.xsession.Session;
import io.github.ray.xsession.SessionEventHandler;
import io.github.ray.xsession.SessionIdStrategy;
import io.github.ray.xsession.SessionRepository;
import io.github.ray.xsession.spi.SpiServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

public class XSessionRequestWrapper extends HttpServletRequestWrapper{
	
	/**
	 * session存放到request中的key
	 */
	private final String CURRENT_SESSION_ATTR = HttpServletRequestWrapper.class.getName();
	
	/**
	 * session id 策略
	 */
	private SessionIdStrategy sessionIdStrategy = SpiServiceFactory.getService(SessionIdStrategy.class);
	
	/**
	 * Session 事件处理器
	 */
	private SessionEventHandler sessionEventHandler = SpiServiceFactory.getService(SessionEventHandler.class);
	
	/**
	 * 持久化机制
	 */
	private SessionRepository sessionRepository = SpiServiceFactory.getService(SessionRepository.class);
	/**
	 * session 是否被清除
	 */
	private boolean requestedSessionInvalidated;
	/**
	 * 从cookie中获取的session id
	 */
	private String requestedSessionId;
	
	private HttpServletResponse response;
	private ServletContext servletContext;
	
	public XSessionRequestWrapper(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
	    super(request);
	    this.response = response;
	    this.servletContext = servletContext;
    }

	public void commitSession() {
		HttpSessionWrapper wrappedSession = getCurrentSession();
		if(wrappedSession == null) {
			if(isInvalidateClientSession()) {
				sessionEventHandler.onInvalidateSession(getRequestedSessionId(), this, response);
			}
		} else {
			Session session = wrappedSession.getSession();
			sessionRepository.save(session);
			//事件触发应该放在session持久化后
			if(session.isNew() && !session.getId().equals(requestedSessionId)) {
				sessionEventHandler.onNewSession(wrappedSession, this, response);
			}
		}
	}

	@Override
	public HttpSession getSession(boolean create){
		//当前request上下文是否存在
		HttpSessionWrapper currentSession = getCurrentSession();
		if(currentSession != null){
			return currentSession;
		}
		//判断session是否已经存在
		String requestedSessionId = getRequestedSessionId();
		Session session = null;
		if(requestedSessionId != null){
			session = getSession(requestedSessionId);
			if(session != null){
				currentSession = new HttpSessionWrapper(session, servletContext);
				session.setNew(false);
				setCurrentSession(currentSession);
				return currentSession;
			}
		}
		
		if(!create){
			return null;
		}
		//创建新Session
		session = sessionRepository.createSession(getNewSessionId());
		session.setNew(true);
		currentSession = new HttpSessionWrapper(session, servletContext);
		setCurrentSession(currentSession);
		
		//直接写回Cookie，防止重定向cookie未及时写到浏览器产生新的Session
		Cookie c = new Cookie(SessionIdStrategy.SESSIONID, session.getId());
		String ctx = getContextPath() + "/";
		c.setPath(ctx.replaceAll("/+", "/"));
		response.addCookie(c);
		return currentSession;
	}
	
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}
	
	@Override
	public String getRequestedSessionId() {
		if(requestedSessionId == null){
			requestedSessionId =  sessionIdStrategy.getSessionIdFromRequest(this);
		}
		return requestedSessionId;
	}
	
	public String getNewSessionId() {
		return sessionIdStrategy.newSessionId();
	}
	
	private boolean isInvalidateClientSession() {
		return getCurrentSession() == null && requestedSessionInvalidated;
	}
	
	private Session getSession(String sessionId) {
		Session session = sessionRepository.getSession(sessionId);
		return session;
	}
	
	private HttpSessionWrapper getCurrentSession() {
		return (HttpSessionWrapper) getAttribute(CURRENT_SESSION_ATTR);
	}

	private void setCurrentSession(HttpSession currentSession) {
		if(currentSession == null) {
			removeAttribute(CURRENT_SESSION_ATTR);
		} else {
			setAttribute(CURRENT_SESSION_ATTR, currentSession);
		}
	}
	
	private final class HttpSessionWrapper extends XSession {

		public HttpSessionWrapper(Session session, ServletContext servletContext) {
			super(session, servletContext);
		}

		public void invalidate() {
			super.invalidate();
			requestedSessionInvalidated = true;
			setCurrentSession(null);
			sessionRepository.delete(getId());
		}
	}
}
