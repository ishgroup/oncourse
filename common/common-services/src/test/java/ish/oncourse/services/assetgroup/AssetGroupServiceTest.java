package ish.oncourse.services.assetgroup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.resource.Resource;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class AssetGroupServiceTest extends Assert {

	@Test
	public void createAssetGroupCollection() throws Exception {

		IResourceService resourceService = new IResourceService() {

			@Override
			public File getCustomComponentRoot() {
				String path = getClass().getPackage().getName().replace('.', '/');
				return new File(path);
			}

			public List<PrivateResource> getConfigResources(final String fileName) {

				PrivateResource res = new PrivateResource() {

					public URL getPrivateUrl() {

						String path = getClass().getPackage().getName().replace('.', '/');
						String uri = path + "/" + fileName;

						URL url = Thread.currentThread().getContextClassLoader().getResource(uri);

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

				return Collections.singletonList(res);
			}

			public Resource getWebResource(final String fileName) {
				Resource mockResource = mock(Resource.class);

				when(mockResource.getPublicUrl()).thenReturn("http://dummy/" + fileName);

				return mockResource;
			}

			public PrivateResource getTemplateResource(String layoutKey, String fileName) {
				throw new UnsupportedOperationException();
			}

			@Override
			public org.apache.tapestry5.ioc.Resource getDbTemplateResource(String layoutKey, String fileName) {
				throw new UnsupportedOperationException();
			}
		};

		AssetGroupService service = new AssetGroupService(resourceService);

		AssetGroupCollection collection = service.createAssetGroupCollection(AssetType.css);
		assertNotNull(collection);

		Collection<String> admin = collection.getAssetUrls("admin");
		assertNotNull(admin);
		assertEquals(1, admin.size());
		assertEquals("http://dummy/[app]library/css/application/admin.css", admin.toArray()[0]);

		Collection<String> application = collection.getAssetUrls("application");
		assertNotNull(application);
		assertEquals(10, application.size());
		assertEquals("http://dummy/[app]library/css/application/reset.css", application.toArray()[0]);

		assertNotNull(collection.getAssetUrls("ie"));
		assertEquals(2, collection.getAssetUrls("ie").size());

		Collection<String> staticCollection = collection.getAssetUrls("static");
		assertNotNull(staticCollection);
		assertEquals(2, staticCollection.size());
		assertEquals("http://dummy/site/design.css", staticCollection.toArray()[0]);
	}
}
