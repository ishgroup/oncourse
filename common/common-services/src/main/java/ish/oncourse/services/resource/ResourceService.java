package ish.oncourse.services.resource;

import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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

	private static final Logger LOGGER = Logger
			.getLogger(ResourceService.class);


	public ResourceService(@Inject IPropertyService propertyService,
			@Inject IWebSiteService siteService, @Inject ILookupService lookupService) {

		String customComponentsPath = (String) lookupService.lookup(Property.CustomComponentsPath.value());

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
		String siteFolder = siteService.getCurrentWebSite().getResourceFolderName();

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
	public PrivateResource getTemplateResource(String layoutKey,
			String fileName) {

		File[] roots = getResourceRoots();

		FileResource res = null;

		for (File file : roots) {
			if (file != null) {
				res = new FileResource(file + File.separator
						+ LAYOUT_FOLDER + File.separator + layoutKey, fileName);
			}
			if (res.exists()) {
				if ((res.getFile() != null) && res.getFile().canRead()) {
					break;
				} else if ((res.getFile() != null) && ! res.getFile().canRead()) {
					LOGGER.error("The application does not have permission to read the '"
							+ res.getFile().getPath()
							+ "' template. Please check the file system permissions.");
				}
			}
		}

		return res;
	}
	
	/**
	 * 
	 */
	public List<PrivateResource> getConfigResources(String fileName) {
		List<PrivateResource> configs = new LinkedList<>();

		String siteFolder = siteService.getCurrentWebSite().getResourceFolderName();

		if (siteFolder != null) {
			PrivateResource config = new FileResource(customComponentsRoot
					+ File.separator + siteFolder + File.separator
					+ CONFIGS_FOLDER, fileName);
			
			if (config.exists()) {
				if ((config.getFile() != null) && config.getFile().canRead()) {
					configs.add(config);
				} else if ((config.getFile() != null) && ! config.getFile().canRead()) {
					LOGGER.error("The application does not have permission to read the '"
							+ config.getFile().getPath()
							+ "' config. Please check the file system permissions.");
				}

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

	/**
	 * @see IResourceService#getCustomComponentRoot()
	 */
	@Override
	public File getCustomComponentRoot() {
		return customComponentsRoot;
	}
}
