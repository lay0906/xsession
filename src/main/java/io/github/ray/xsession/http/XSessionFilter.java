package io.github.ray.xsession.http;

import io.github.ray.xsession.SessionConfig;
import io.github.ray.xsession.SessionEventHandler;
import io.github.ray.xsession.SessionInit;
import io.github.ray.xsession.spi.SpiServiceFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XSessionFilter implements Filter{
	
	protected ServletContext servletContext;

	protected  SessionInit init ;
	
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
		servletContext = filterConfig.getServletContext();
		init = SpiServiceFactory.getService(SessionInit.class);
		SessionConfig config = SessionConfig.getInstance();
		init.init(config);
		String ctx = servletContext.getContextPath();
	    if(ctx != null){
	    	ctx = ctx.replaceAll("/", "");
	    }
	    config.setCtx(ctx);
		if(init.isSupportClusterSession(config)){
			SessionEventHandler event = (SessionEventHandler)SpiServiceFactory.getService(SessionEventHandler.class);
			event.init(config);
		}
    }

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if(!init.isSupportClusterSession(SessionConfig.getInstance())){
			chain.doFilter(request, response);
		}else{
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;
			XSessionRequestWrapper warpReq = new XSessionRequestWrapper(req, res, servletContext);
			try {
				chain.doFilter(warpReq, response);
			} finally {
				warpReq.commitSession();
			}
		}
    }
	
	@Override
    public void destroy() {
    }
}
