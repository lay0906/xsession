package io.github.ray.xsession.http;

import io.github.ray.xsession.SessionIdStrategy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class XSessionIdStrategy implements SessionIdStrategy {
	
	@Override
	public String newSessionId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public String getSessionIdFromRequest(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(Cookie cookie : cookies){
				if(SESSIONID.equals(cookie.getName())){
					String v = cookie.getValue();
					if(v != null && v.length() > 0){
						return v;
					}
				}
			}
		}
		return null;
	}
}
