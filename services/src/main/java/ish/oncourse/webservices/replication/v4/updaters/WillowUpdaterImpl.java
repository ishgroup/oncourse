package ish.oncourse.webservices.replication.v4.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.v7.updaters.V7UpdatersMap;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.replication.v5.updaters.V5UpdatersMap;
import ish.oncourse.webservices.replication.v6.updaters.V6UpdatersMap;
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
	private Map<String, IWillowUpdater> v4updaterMap = new HashMap<>();
	private Map<String, IWillowUpdater> v5updaterMap = new HashMap<>();
	private Map<String, IWillowUpdater> v6updaterMap = new HashMap<>();
	private Map<String, IWillowUpdater> v7updaterMap = new HashMap<>();
	
	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {
		final V4UpdatersMap v4map = new V4UpdatersMap();
		v4map.initMap(textileConverter);
		v4updaterMap = v4map.getUpdaterMap();
		final V5UpdatersMap v5map = new V5UpdatersMap();
		v5map.initMap(textileConverter);
		v5updaterMap = v5map.getUpdaterMap();
		final V6UpdatersMap v6map = new V6UpdatersMap();
		v6map.initMap(textileConverter);
		v6updaterMap = v6map.getUpdaterMap();
		V7UpdatersMap v7map = new V7UpdatersMap();
		v7map.initMap(textileConverter);
		v7updaterMap = v7map.getUpdaterMap();
	}

	@Override
	public void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		final SupportedVersions version = PortHelper.getVersionByReplicationStub(stub);
		if (version == null) {
			throw new IllegalArgumentException("Unsupported stub version used.");
		}
		String key = EntityMapping.getWillowEntityIdentifer(stub.getEntityIdentifier());
		final IWillowUpdater updater;
		switch (version) {
			case V4:
				updater = v4updaterMap.get(key);
				break;
			case V5:
				updater = v5updaterMap.get(key);
				break;
			case V6:
				updater = v6updaterMap.get(key);
				break;
			case V7:
				updater = v7updaterMap.get(key);
				break;
			default:
				updater = null;
		}
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
