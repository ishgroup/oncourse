package ish.oncourse.webservices.replication.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.v13.updaters.V13UpdatersMap;
import ish.oncourse.webservices.replication.v17.updaters.V17UpdatersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.Map;

public class WillowUpdaterImpl implements IWillowUpdater {
	@Inject
	private IWebSiteService webSiteService;

	private ICayenneService cayenneService;
	
	/**
	 * Willow updaters mappings
	 */
 
	private Map<String, IWillowUpdater> v13updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v17updaterMap = new HashMap<>();

	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {


		V13UpdatersMap v13map = new V13UpdatersMap();
		v13map.initMap(textileConverter);
		v13updaterMap = v13map.getUpdaterMap();

		V17UpdatersMap v17map = new V17UpdatersMap();
		v17map.initMap(textileConverter);
		v17updaterMap = v17map.getUpdaterMap();

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

			case V13:
				updater = v13updaterMap.get(key);
				break;

			case V17:
				updater = v17updaterMap.get(key);
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
	
	@Override
	public void setCayenneService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}
}
