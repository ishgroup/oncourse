package ish.oncourse.services.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;


public class ResourceService implements IResourceService {

	private final static String DEFAULTS_FOLDER = "defaults";

	private final static String LAYOUT_FOLDER = "layouts";
	private final static String CONFIGS_FOLDER = "config";
	private final static String WEB_FOLDER = "s";
	private final static String WEBSERVERRESOURCES_FOLDER = "WebServerResources";

	private final File customComponentsRoot;
	private final File customComponentsDefaultsRoot;
	private final File[] noCustomFolderDefaultsRoot;

	private IWebSiteService webSiteService;


	public ResourceService(@Inject IPropertyService propertyService,
			@Inject IWebSiteService webSiteService) {

		String customComponentsPath = propertyService
				.string(Property.CustomComponentsPath);

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
				DEFAULTS_FOLDER);

		if (!customComponentsDefaultsRoot.isDirectory()) {
			throw new IllegalStateException(
					"Defaults root is not a valid directory: "
							+ customComponentsDefaultsRoot.getAbsolutePath());
		}

		noCustomFolderDefaultsRoot = new File[] { customComponentsDefaultsRoot };

		this.webSiteService = webSiteService;
	}

	/**
	 * Returns root resource folders for the current website.
	 */
	private File[] getResourceRoots() {

		String siteFolder = webSiteService.getCurrentSite().getSiteIdentifier();

		if (siteFolder != null) {
			return new File[] { new File(customComponentsRoot, siteFolder),
					customComponentsDefaultsRoot };
		} else {
			return noCustomFolderDefaultsRoot;
		}
	}

	public PrivateResource getTemplateResource(String fileName) {
		return new FileResource(LAYOUT_FOLDER, fileName);
	}

	public PrivateResource getConfigResource(String fileName) {
		return new FileResource(CONFIGS_FOLDER, fileName);
	}

	public Resource getWebResource(String framework, String fileName) {

		if (framework == null || framework.length() == 0) {
			return getStaticResource(fileName);
		} else if ("app".equals(framework)) {
			return getWOAResource(fileName);
		} else {
			return getFrameworkResource(framework, fileName);
		}
	}

	private Resource getStaticResource(String fileName) {
		return new PublicFileResource(WEB_FOLDER, "", fileName);
	}

	private Resource getWOAResource(String fileName) {
		return new PublicFileResource(WEB_FOLDER, WEBSERVERRESOURCES_FOLDER,
				fileName);
	}

	private Resource getFrameworkResource(String framework, String fileName) {
		String path = "Frameworks/" + framework + ".framework/"
				+ WEBSERVERRESOURCES_FOLDER;
		return new PublicFileResource(WEB_FOLDER, path, fileName);
	}

	class PublicFileResource extends FileResource implements Resource {

		private String frameworkFolder;

		PublicFileResource(String folder, String frameworkFolder,
				String fileName) {
			super(folder, fileName);
			this.frameworkFolder = frameworkFolder != null
					&& frameworkFolder.length() > 0 ? frameworkFolder + "/"
					: "";
		}

		public String getPublicUrl() {

			// let Apache handle URL resolving of default vs. custom resource,
			// just return a normal URL

			// TODO: andrus, Nov 14 2009 - ZFS version part of the URL
			return "/" + folder + "/" + frameworkFolder + fileName;
		}
	}

	class FileResource implements PrivateResource {

		protected String fileName;
		protected String folder;

		FileResource(String folder, String fileName) {
			// TODO: denormalize abstract path for Windows?
			this.fileName = (fileName.startsWith("/")) ? fileName.substring(1)
					: fileName;
			this.folder = folder;
		}

		public File getFile() {

			StringBuilder messages = null;

			for (File root : getResourceRoots()) {

				File resourceFile = new File(root, folder + File.separator
						+ fileName);

				if (resourceFile.exists()) {
					return resourceFile;
				}

				if (messages == null) {
					messages = new StringBuilder(
							"Can't locate file. Location(s) checked: ");
				} else {
					messages.append(", ");
				}

				messages.append(resourceFile.getAbsolutePath());
			}

			// throw... don't allow missing files
			throw new IllegalStateException(messages.toString());
		}

		public URL getPrivateUrl() {
			try {
				return getFile().toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
