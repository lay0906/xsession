package io.github.ray.xsession;

public interface SessionInit {

	void init(SessionConfig config);

	boolean isSupportClusterSession(SessionConfig config);
}
