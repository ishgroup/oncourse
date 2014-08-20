package ish.oncourse.services.resource;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ResourceService implements IResourceService {

	private final static String DEFAULT_FOLDER = "default";

	private final static String LAYOUT_FOLDER = "layouts";
	private final static String CONFIGS_FOLDER = "conf";
	private final static String WEB_FOLDER = "s";

	private final File customComponentsRoot;
	private final File customComponentsDefaultsRoot;
	private final File[] noCustomFolderDefaultsRoot;
	
	private IWebSiteService siteService;
	private IWebSiteVersionService siteVersionService;
	private ICayenneService cayenneService;

	private static final Logger LOGGER = Logger
			.getLogger(ResourceService.class);


	public ResourceService(@Inject IPropertyService propertyService,
			@Inject IWebSiteService siteService, @Inject ILookupService lookupService, 
			@Inject ICayenneService cayenneService, @Inject IWebSiteVersionService siteVersionService) {

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
		this.cayenneService = cayenneService;
		this.siteVersionService = siteVersionService;
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
	
	@Override
	public org.apache.tapestry5.ioc.Resource getDbTemplateResource(String layoutKey, String fileName) {
		ObjectContext context = cayenneService.sharedContext();

		SelectQuery query = new SelectQuery(WebTemplate.class);
		query.andQualifier(ExpressionFactory.matchExp(
				WebTemplate.LAYOUT_PROPERTY + "." + WebSiteLayout.WEB_SITE_VERSION_PROPERTY, 
				siteVersionService.getCurrentVersion()));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY + "." + WebSiteLayout.LAYOUT_KEY_PROPERTY, layoutKey));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.NAME_PROPERTY, fileName));

		WebTemplate template = (WebTemplate) Cayenne.objectForQuery(context, query);
		
		return template != null ? new DatabaseTemplateResource(template) : null;
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
