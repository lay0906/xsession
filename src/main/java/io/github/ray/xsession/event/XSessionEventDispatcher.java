package io.github.ray.xsession.event;



public class XSessionEventDispatcher {
	private static XSessionEventDispatcher INS;

	public static synchronized XSessionEventDispatcher build(){
		if(INS == null){
			INS = new XSessionEventDispatcher();
			XSessionEventProcess p = new XSessionEventProcess();
			p.initEvent();
			XSessionExpireEventProcess ep = new XSessionExpireEventProcess();
			ep.initEvent();
		}
		return INS;
	}
}
