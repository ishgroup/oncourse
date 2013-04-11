package ish.oncourse.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Utility class to perform context lookups.
 * 
 * @author dzmitry
 */
public class ContextUtil {
	
	private static final Logger LOGGER = Logger.getLogger(ContextUtil.class);
	
	public static final String CACHE_ENABLED_PROPERTY_KEY = "cache.enabled";
	public static final String OSCACHE_CAPACITY_PROPERTY_KEY = "oscache.cache.capacity";
	
	/**
	 * Look up specified path in the apps context
	 * 
	 * @param path
	 * @return value
	 * @throws NamingException
	 */
    public static Object lookup(String path) throws NamingException {
        InitialContext context = new InitialContext();
        Context ctx = null;
        try {
            ctx = (Context) context.lookup("java:comp/env");
        } catch (NamingException e) {
            ctx = context;
            LOGGER.warn(e);
        }
        return ctx.lookup(path);
    }
    
    /**
     * Reads cache.enabled parameter from apps context.
     * 
     * @return if cayenne cache should be enabled
     */
    public static boolean isQueryCacheEnabled() {
    	try {
    		Boolean cacheEnabled = (Boolean) lookup(CACHE_ENABLED_PROPERTY_KEY);
    		
    		return cacheEnabled;
    	} catch (Exception e) {
    		LOGGER.warn("cache.enabled option is not configured in context.xml, query caching will be enabled for the app.");
    		return true;
    	}
    }
    
    /**
     * Reads "oscache.cache.capacity" option from app context.
     * 
     * @return OSCache capacity value defined in context.xml
     */
    public static Integer getOSCacheCapacity() {
    	try {
    		Integer cacheCapacity = (Integer) lookup(OSCACHE_CAPACITY_PROPERTY_KEY);
    		
    		return cacheCapacity;
    	} catch (Exception e) {
    		LOGGER.warn("oscache.cache.capacity option is not configured in context.xml, using default from oscache.properties configuration file.");
    		return null;
    	}
    }

}
