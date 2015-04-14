package ish.oncourse.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Utility class to perform context lookups.
 * 
 * @author dzmitry
 */
public class ContextUtil {
	
	private static final Logger logger = LogManager.getLogger();
	
	public static final String CACHE_ENABLED_PROPERTY_KEY = "cache.enabled";
	public static final String OSCACHE_CAPACITY_PROPERTY_KEY = "oscache.cache.capacity";
	
	public static final String STORAGE_ACCESS_ID_PROPERTY_KEY = "storage.access.id";
	public static final String STORAGE_ACCESS_KEY_PROPERTY_KEY = "storage.access.key";
	
	public static final String S_ROOT = "s.root";
	public static final String CMS_EDIT_SCRIPT_PATH = "cms.script.edit";
	public static final String CMS_DEPLOY_SCRIPT_PATH = "cms.script.deploy";
	
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
            logger.warn(e);
        }
        return ctx.lookup(path);
    }
    
    /**
     * Reads cache.enabled parameter from apps context.
     * 
     * @return if cayenne cache should be enabled
     */
    public static boolean isObjectCacheEnabled() {
    	try {
    		Boolean cacheEnabled = (Boolean) lookup(CACHE_ENABLED_PROPERTY_KEY);
    		
    		return cacheEnabled;
    	} catch (Exception e) {
    		logger.warn("cache.enabled option is not configured in context.xml, query caching will be enabled for the app.");
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
    		logger.warn("oscache.cache.capacity option is not configured in context.xml, using default from oscache.properties configuration file.");
    		return null;
    	}
    }
	
	public static String getStorageAccessId() {
		try {
			return (String) lookup(STORAGE_ACCESS_ID_PROPERTY_KEY);
		} catch (Exception e) {
			logger.warn("storage.access.id is not configured in context.xml, S3 operations won't be available.");
			return null;
		}
	}
	
	public static String getStorageAccessKey() {
		try {
			return (String) lookup(STORAGE_ACCESS_KEY_PROPERTY_KEY);
		} catch (Exception e) {
			logger.warn("storage.access.key is not configured in context.xml, S3 operations won't be available.");
			return null;
		}
	}
	
	public static String getSRoot() {
		try {
			return (String) lookup(S_ROOT);
		} catch (Exception e) {
			logger.warn("s.root is not configured in context.xml, webdav functionality will not be available.");
			return null;
		}
	}

	public static String getCmsEditScriptPath() {
		try {
			return (String) lookup(CMS_EDIT_SCRIPT_PATH);
		} catch (Exception e) {
			logger.warn("cms.script.edit is not configured in context.xml, webdav resource editing will not function properly.");
			return null;
		}
	}

	public static String getCmsDeployScriptPath() {
		try {
			return (String) lookup(CMS_DEPLOY_SCRIPT_PATH);
		} catch (Exception e) {
			logger.warn("cms.script.deploy is not configured in context.xml, cms won't be able to deploy resources.");
			return null;
		}
	}
}
