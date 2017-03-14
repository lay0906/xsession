package io.github.ray.xsession.init;

import io.github.ray.xsession.SessionConfig;
import io.github.ray.xsession.SessionInit;
import io.github.ray.xsession.constant.XSessionConstant;

public class XSessionInit implements SessionInit {

	@Override
	public void init(SessionConfig config) {
		config.setEnable(SessionProperties.getSessionEnable());
		config.setSupportExpireEvent(SessionProperties.getSupportExpireEvent());
		
		Integer timeout = SessionProperties.getSessionTimeout();
		if(timeout == null){
			timeout = (null != WebXmlConfig.getSessionTimeOut()) ? WebXmlConfig.getSessionTimeOut() : XSessionConstant.DEFAULT_SESSION_TIMEOUT;
		}
		config.setSessionTimeOut(timeout);
		
		config.setListeners(WebXmlConfig.getSessionListeners());
	}

	@Override
	public boolean isSupportClusterSession(SessionConfig config) {
		return config.isEnable();
	}
}
