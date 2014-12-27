package ish.oncourse.webservices.replication.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.v6.updaters.V6UpdatersMap;
import ish.oncourse.webservices.replication.v7.updaters.V7UpdatersMap;
import ish.oncourse.webservices.replication.v8.updaters.V8UpdatersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.Map;

public class WillowUpdaterImpl implements IWillowUpdater {
	@Inject
	private IWebSiteService webSiteService;
	
	/**
	 * Willow updaters mappings
	 */
	private Map<String, IWillowUpdater> v6updaterMap = new HashMap<>();
	private Map<String, IWillowUpdater> v7updaterMap = new HashMap<>();
	private Map<String, IWillowUpdater> v8updaterMap = new HashMap<>();
	
	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {
		final V6UpdatersMap v6map = new V6UpdatersMap();
		v6map.initMap(textileConverter);
		v6updaterMap = v6map.getUpdaterMap();
		V7UpdatersMap v7map = new V7UpdatersMap();
		v7map.initMap(textileConverter);
		v7updaterMap = v7map.getUpdaterMap();
		V8UpdatersMap v8map = new V8UpdatersMap();
		v8map.initMap(textileConverter);
		v8updaterMap = v8map.getUpdaterMap();
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
			case V6:
				updater = v6updaterMap.get(key);
				break;
			case V7:
				updater = v7updaterMap.get(key);
				break;
			case V8:
				updater = v8updaterMap.get(key);
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
				entity.setCollege(entity.getObjectContext().localObject(currentCollege));
			}	
		}
		updater.updateEntityFromStub(stub, entity, callback);
	}
}
