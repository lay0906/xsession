package io.github.ray.xsession;

import javax.servlet.http.HttpServletRequest;

public interface SessionIdStrategy {
	
	String SESSIONID = "XSESSIONID";

	String newSessionId();

	String getSessionIdFromRequest(HttpServletRequest request);
}
