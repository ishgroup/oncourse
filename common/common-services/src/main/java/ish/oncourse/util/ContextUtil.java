package ish.oncourse.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class ContextUtil {
	
	private static final Logger LOGGER = Logger.getLogger(ContextUtil.class);
	
	public static final String CACHE_ENABLED_PROPERTY_KEY = "cache.enabled";
	
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
    
    public static boolean isQueryCacheEnabled() {
    	try {
    		Boolean cacheEnabled = (Boolean) lookup(CACHE_ENABLED_PROPERTY_KEY);
    		
    		return cacheEnabled;
    	} catch (Exception e) {
    		LOGGER.warn("cache.enabled option is not configured in context.xml, query caching will be enabled for the app.");
    		return true;
    	}
    }

}
