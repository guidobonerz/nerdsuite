package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

	private static final String COMMON = "COMMON";
	private static Map<String, Map<String, IService>> serviceOwnerMap = new HashMap<>();

	private ServiceFactory() {

	}

	public static <S extends IService> S getCommonService(Class<? super S> serviceClass) {
		return getService(COMMON, serviceClass);
	}

	@SuppressWarnings("unchecked")
	public static <S extends IService> S getService(Object owner, Class<? super S> serviceClass) {

		String serviceOwner = owner.toString();
		Map<String, IService> serviceCacheMap = serviceOwnerMap.get(serviceOwner);
		if (serviceCacheMap == null) {
			serviceCacheMap = new HashMap<String, IService>();
			serviceOwnerMap.put(serviceOwner, serviceCacheMap);
		}

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
