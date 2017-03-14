package io.github.ray.xsession.redis;

import io.github.ray.xcache.CacheService;
import io.github.ray.xcache.provider.CacheProvider;
import io.github.ray.xsession.Session;
import io.github.ray.xsession.SessionRepository;

import java.util.Map;

public class RedisSessionRepository implements SessionRepository{
	
	private static CacheService redis = CacheProvider.getJdkSerializerCacheService();

	@Override
    public Session createSession(String id) {
		Session session = new RedisSession(id);
		session.setNew(true);
	    return session;
    }

	@Override
    public void save(Session session) {
		RedisSession S = (RedisSession)session;
	    String id = getRedisSessionId(session.getId());
	    if(session.isNew()){
	    	redis.setex(id, Session.CREATIONTIME, session.getCreationTime(), session.getMaxInactiveInterval());
	    }
	    redis.setex(id, Session.LASTACCESSEDTIME, session.getLastAccessedTime(), session.getMaxInactiveInterval());
	    redis.setex(id, Session.MAXINACTIVEINTERVAL, session.getMaxInactiveInterval(), session.getMaxInactiveInterval());
	    redis.msetex(id, S.getAttrs(), session.getMaxInactiveInterval());
    }

	@Override
    public Session getSession(String id) {
		String rSessionId = getRedisSessionId(id);
		Map<String, Object> attrs = redis.getMap(rSessionId, Object.class);
		if(attrs == null || attrs.isEmpty()){
			return null;
		}
		RedisSession session = new RedisSession(id);
		session.setCreationTime((long)attrs.get(Session.CREATIONTIME));
		session.setLastAccessedTime(System.currentTimeMillis());
		session.setMaxInactiveInterval((int)attrs.get(Session.MAXINACTIVEINTERVAL));
		for(Map.Entry<String, Object> entry : attrs.entrySet()){
			session.setAttribute(entry.getKey(), entry.getValue());
		}
	    return session;
    }

	@Override
    public void delete(String id) {
	    redis.remove(getRedisSessionId(id));
    }
	

	private String getRedisSessionId(String id){
		return Session.PREFIX + id;
	}
}


