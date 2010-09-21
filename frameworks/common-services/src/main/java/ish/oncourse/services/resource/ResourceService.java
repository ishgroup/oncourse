package ish.oncourse.services.resource;

import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ResourceService implements IResourceService {

	private final static String DEFAULT_FOLDER = "default";

	private final static String LAYOUT_FOLDER = "layouts";
	private final static String CONFIGS_FOLDER = "conf";
	private final static String WEB_FOLDER = "s";

	private final File customComponentsRoot;
	private final File customComponentsDefaultsRoot;
	private final File[] noCustomFolderDefaultsRoot;
	private IWebSiteService siteService;

	private static final Logger logger = Logger
			.getLogger(ResourceService.class);

	public ResourceService(@Inject IPropertyService propertyService,
			@Inject IWebSiteService siteService) {

		String customComponentsPath = "";
		try {
			Context ctx = new InitialContext();
			customComponentsPath = (String) ctx.lookup("java:comp/env/"
					+ Property.CustomComponentsPath.value());
			if (logger.isInfoEnabled()) {
				logger.info("CustomComponentsPath configured through JNDI to: "
						+ customComponentsPath);
			}
		} catch (NamingException ne) {
			logger.warn(
					"CustomComponentsPath not defined by JNDI, falling to secondary config",
					ne);
		}

		if ((customComponentsPath == null) || ("".equals(customComponentsPath))) {
			customComponentsPath = propertyService
					.string(Property.CustomComponentsPath);
		}

		if (customComponentsPath == null) {
			throw new IllegalStateException("Undefined property: "
					+ Property.CustomComponentsPath);
		}

		customComponentsRoot = new File(customComponentsPath);
		if (!customComponentsRoot.exists()) {
			throw new IllegalStateException(
					"Custom components root does not exist: "
							+ customComponentsPath);
		}

		if (!customComponentsRoot.isDirectory()) {
			throw new IllegalStateException(
					"Custom components root is not a valid directory: "
							+ customComponentsPath);
		}

		customComponentsDefaultsRoot = new File(customComponentsRoot,
				DEFAULT_FOLDER);

		if (!customComponentsDefaultsRoot.isDirectory()) {
			throw new IllegalStateException(
					"Defaults root is not a valid directory: "
							+ customComponentsDefaultsRoot.getAbsolutePath());
		}

		noCustomFolderDefaultsRoot = new File[] { customComponentsDefaultsRoot };

		this.siteService = siteService;
	}

	/**
	 * Returns root resource folders for the current website.
	 */
	private File[] getResourceRoots() {

		File[] resourceRoots = noCustomFolderDefaultsRoot;
		String siteFolder = siteService.getResourceFolderName();

		if (siteFolder != null) {
			resourceRoots = new File[] {
					new File(customComponentsRoot, siteFolder),
					customComponentsDefaultsRoot };
		}

		return resourceRoots;
	}
	
	/**
	 * 
	 */
	public PrivateResource getTemplateResource(String templateKey,
			String fileName) {

		File[] roots = getResourceRoots();

		String subfolder = (templateKey != null) ? templateKey : DEFAULT_FOLDER;

		FileResource res = new FileResource(roots[0] + File.separator
				+ LAYOUT_FOLDER + File.separator + subfolder, fileName);

		FileResource defaultRes = new FileResource(roots[1] + File.separator
				+ LAYOUT_FOLDER + File.separator + subfolder, fileName);

		return (res.exists()) ? res : defaultRes;
	}
	
	/**
	 * 
	 */
	public List<PrivateResource> getConfigResources(String fileName) {
		List<PrivateResource> configs = new LinkedList<PrivateResource>();

		String siteFolder = siteService.getResourceFolderName();

		if (siteFolder != null) {
			PrivateResource config = new FileResource(customComponentsRoot
					+ File.separator + siteFolder + File.separator
					+ CONFIGS_FOLDER, fileName);
			
			if (config.exists()) {
				configs.add(config);
			}
		}

		PrivateResource defaultConfig = new FileResource(
				customComponentsDefaultsRoot + File.separator + CONFIGS_FOLDER,
				fileName);
		
		configs.add(defaultConfig);

		return configs;
	}

	public Resource getWebResource(String fileName) {
		return new PublicFileResource(WEB_FOLDER, fileName);
	}
}
