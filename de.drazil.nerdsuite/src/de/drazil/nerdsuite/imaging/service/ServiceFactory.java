package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

	private static Map<String, IService> serviceCacheMap = new HashMap<>();

	private ServiceFactory() {

	}

	public static <S extends IService> S getService(Class<? super S> serviceClass) {
		String name = serviceClass.getName();
		S service = (S) serviceCacheMap.get(name);
		if (null == service) {
			try {
				service = (S) serviceClass.newInstance();
				serviceCacheMap.put(name, service);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return service;
	}
}
