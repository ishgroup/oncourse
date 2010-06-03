package ish.oncourse.services.assetgroup;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.MockResource;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.resource.Resource;


public class AssetGroupServiceTest extends Assert {

	@Test
	public void createAssetGroupCollection() throws Exception {

		IResourceService resourceService = new IResourceService() {

			public PrivateResource getConfigResource(final String fileName) {

				return new PrivateResource() {

					public URL getPrivateUrl() {

						String path = getClass().getPackage().getName()
								.replace('.', '/');
						String uri = path + "/" + fileName;

						URL url = Thread.currentThread()
								.getContextClassLoader().getResource(uri);

						assertNotNull("Bad file name: " + fileName, url);
						return url;
					}

					public File getFile() {
						throw new UnsupportedOperationException();
					}

					public boolean exists() {
						throw new UnsupportedOperationException();
					}
				};
			}

			public Resource getWebResource(final String framework,
					final String fileName) {

				return new MockResource() {
					@Override
					public String getPublicUrl() {
						String prefix = framework != null ? framework + "/"
								: "";
						return "http://dummy/" + prefix + fileName;
					}
				};
			}
			
			public PrivateResource getTemplateResource(String templateKey,
					String fileName) {
				throw new UnsupportedOperationException();
			}
		};

		AssetGroupService service = new AssetGroupService(resourceService);

		AssetGroupCollection collection = service
				.createAssetGroupCollection(AssetType.css);
		assertNotNull(collection);

		Collection<String> admin = collection.getAssetUrls("admin");
		assertNotNull(admin);
		assertEquals(1, admin.size());
		assertEquals("http://dummy/app/library/css/application/admin.css",
				admin.toArray()[0]);

		Collection<String> application = collection.getAssetUrls("application");
		assertNotNull(application);
		assertEquals(10, application.size());
		assertEquals("http://dummy/app/library/css/application/reset.css",
				application.toArray()[0]);

		assertNotNull(collection.getAssetUrls("ie"));
		assertEquals(2, collection.getAssetUrls("ie").size());

		Collection<String> staticCollection = collection.getAssetUrls("static");
		assertNotNull(staticCollection);
		assertEquals(2, staticCollection.size());
		assertEquals("http://dummy/site/design.css",
				staticCollection.toArray()[0]);
	}
}
