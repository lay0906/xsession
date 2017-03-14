package io.github.ray.xsession;

public interface SessionRepository{

	Session createSession(String id);

	void save(Session session);

	Session getSession(String id);

	void delete(String id);
}