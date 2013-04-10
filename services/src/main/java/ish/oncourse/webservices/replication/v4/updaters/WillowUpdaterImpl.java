package ish.oncourse.webservices.replication.v4.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.replication.v5.updaters.V5UpdatersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import java.util.HashMap;
import java.util.Map;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WillowUpdaterImpl implements IWillowUpdater {
	@Inject
	private IWebSiteService webSiteService;
	
	/**
	 * Willow updaters mappings
	 */
	private Map<String, IWillowUpdater> v4updaterMap = new HashMap<String, IWillowUpdater>();
	private Map<String, IWillowUpdater> v5updaterMap = new HashMap<String, IWillowUpdater>();
	
	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {
		final V4UpdatersMap v4map = new V4UpdatersMap();
		v4map.initMap(textileConverter);
		v4updaterMap = v4map.getUpdaterMap();
		final V5UpdatersMap v5map = new V5UpdatersMap();
		v5map.initMap(textileConverter);
		v5updaterMap = v5map.getUpdaterMap();
	}

	@Override
	public void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		final SupportedVersions version = PortHelper.getVersionByReplicationStub(stub);
		if (version == null) {
			throw new IllegalArgumentException("Unsupported stub version used.");
		}
		String key = EntityMapping.getWillowEntityIdentifer(stub.getEntityIdentifier());
		final IWillowUpdater updater = SupportedVersions.V4.equals(version) ? v4updaterMap.get(key) : v5updaterMap.get(key) ;
		if (updater == null) {
			throw new UpdaterNotFoundException(String.format("Updater not found for entity with key:%s", key), key);
		}
		if (entity.getCollege() == null) {
			College currentCollege = webSiteService.getCurrentCollege();
			if (currentCollege != null) {
				entity.setCollege((College) entity.getObjectContext().localObject(currentCollege));
			}	
		}
		updater.updateEntityFromStub(stub, entity, callback);
	}
}
