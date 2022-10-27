package de.drazil.nerdsuite.imaging.service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

    private static final String COMMON = "COMMON";
    private static Map<String, Map<String, IService>> serviceOwnerMap = new HashMap<>();

    private ServiceFactory() {

    }

    public static boolean checkService(String id) {
        return serviceOwnerMap.containsKey(id);
    }

    public static <S extends IService> S getCommonService(Class<? super S> serviceClass) {
        return getService(COMMON, serviceClass);
    }

    @SuppressWarnings("unchecked")
    public static <S extends IService> S getService(String owner, Class<? super S> serviceClass) {
        Map<String, IService> serviceCacheMap = getServiceCacheMap(owner);
        String name = serviceClass.getName();
        S service = (S) serviceCacheMap.get(name);
        if (null == service) {
            try {
                service = (S) serviceClass.getDeclaredConstructor().newInstance();
                service.setOwner(owner);
                serviceCacheMap.put(name, service);
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        return service;
    }

    public static void addService(String owner, IService service, boolean override) {
        Map<String, IService> serviceCacheMap = getServiceCacheMap(owner);
        String name = service.getClass().getName();
        IService s = serviceCacheMap.get(name);
        if (s != null && override || s == null) {
            serviceCacheMap.put(name, service);
        }
        service.setOwner(owner);
    }

    public static Map<String, IService> getServiceCacheMap(String owner) {
        Map<String, IService> serviceCacheMap = serviceOwnerMap.get(owner);
        if (serviceCacheMap == null) {
            serviceCacheMap = new HashMap<String, IService>();
            serviceOwnerMap.put(owner, serviceCacheMap);
        }
        return serviceCacheMap;
    }
}
