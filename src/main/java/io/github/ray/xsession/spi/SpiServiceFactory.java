package io.github.ray.xsession.spi;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class SpiServiceFactory {
	
	// spi的缓存map
	private static ConcurrentHashMap<Class<?>, SpiServiceLoader<?>> spis = new ConcurrentHashMap<Class<?>, SpiServiceLoader<?>>();

	public static <S> S getService(Class<S> service) {
		SpiServiceLoader<S> serviceLoader = (SpiServiceLoader<S>)spis.get(service);
		if (null == serviceLoader) {
			spis.putIfAbsent(service, SpiServiceLoader.load(service));
			serviceLoader = (SpiServiceLoader<S>)spis.get(service);
		}
		if (null != serviceLoader) {
			Iterator<S> iterator = serviceLoader.iterator();
			if (null != iterator && iterator.hasNext()) {
				return iterator.next();
			}
		}
		return null;
	}

	public static <S> S getService(Class<S> service, String type) {
		return getService(service, type, false);
	}

	public static <S> S getService(Class<S> service, String type, boolean reload) {
		SpiServiceLoader<S> serviceLoader = (SpiServiceLoader<S>)spis.get(service);
		if (null == serviceLoader) {
			serviceLoader = SpiServiceLoader.load(service);
			spis.putIfAbsent(service, serviceLoader);
		}
		serviceLoader = (SpiServiceLoader<S>)spis.get(service);
		if (null != serviceLoader) {
			return serviceLoader.getService(type, reload);
		}
		return null;
	}

	public static void setServiceLoader(SpiServiceLoader<?> serviceLoader) {
		spis.put(serviceLoader.getService(), serviceLoader);
	}
}
