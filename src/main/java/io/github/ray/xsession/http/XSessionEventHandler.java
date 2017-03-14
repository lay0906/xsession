package io.github.ray.xsession.http;

import io.github.ray.xcache.redis.RedisClient;
import io.github.ray.xcache.redis.RedisClientFactory;
import io.github.ray.xsession.SessionConfig;
import io.github.ray.xsession.SessionEventHandler;
import io.github.ray.xsession.constant.XSessionConstant;
import io.github.ray.xsession.event.XSessionEventDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class XSessionEventHandler implements SessionEventHandler{

	private RedisClient redis = RedisClientFactory.getRedisClient();
	
	@Override
	public void onNewSession(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		redis.publish(XSessionConstant.SESSION_CREATE_KEY(), session.getId());
	}

	@Override
	public void onInvalidateSession(String sessionId,
			HttpServletRequest request, HttpServletResponse response) {
		redis.publish(XSessionConstant.SESSION_DEL_KEY(), sessionId);
	}

	@Override
    public void init(SessionConfig config) {
		XSessionEventDispatcher.build();
    }

}
