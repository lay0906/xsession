package io.github.ray.xsession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface SessionEventHandler {

	void init(SessionConfig config);

	void onNewSession(HttpSession session, HttpServletRequest request, HttpServletResponse response);

	void onInvalidateSession(String sessionId, HttpServletRequest request, HttpServletResponse response);
}
