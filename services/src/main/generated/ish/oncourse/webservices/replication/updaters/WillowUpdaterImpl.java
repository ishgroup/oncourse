package ish.oncourse.webservices.replication.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.v10.updaters.V10UpdatersMap;
import ish.oncourse.webservices.replication.v11.updaters.V11UpdatersMap;
import ish.oncourse.webservices.replication.v7.updaters.V7UpdatersMap;
import ish.oncourse.webservices.replication.v9.updaters.V9UpdatersMap;
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
 
	private Map<String, IWillowUpdater> v7updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v9updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v10updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v11updaterMap = new HashMap<>();

	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {


		V7UpdatersMap v7map = new V7UpdatersMap();
		v7map.initMap(textileConverter);
		v7updaterMap = v7map.getUpdaterMap();

		V9UpdatersMap v9map = new V9UpdatersMap();
		v9map.initMap(textileConverter);
		v9updaterMap = v9map.getUpdaterMap();

		V10UpdatersMap v10map = new V10UpdatersMap();
		v10map.initMap(textileConverter);
		v10updaterMap = v10map.getUpdaterMap();

		V11UpdatersMap v11map = new V11UpdatersMap();
		v11map.initMap(textileConverter);
		v11updaterMap = v11map.getUpdaterMap();

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

			case V7:
				updater = v7updaterMap.get(key);
				break;

			case V9:
				updater = v9updaterMap.get(key);
				break;

			case V10:
				updater = v10updaterMap.get(key);
				break;

			case V11:
				updater = v11updaterMap.get(key);
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
