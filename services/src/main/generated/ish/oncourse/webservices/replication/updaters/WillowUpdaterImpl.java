package ish.oncourse.webservices.replication.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.v21.updaters.V21UpdatersMap;
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
 

	private Map<String, IWillowUpdater> v21updaterMap = new HashMap<>();

	public WillowUpdaterImpl(@Inject IReachtextConverter textileConverter) {

		V21UpdatersMap v21map = new V21UpdatersMap();
		v21map.initMap(textileConverter);
		v21updaterMap = v21map.getUpdaterMap();

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
			case V21:
				updater = v21updaterMap.get(key);
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
		updater.setCayenneService(cayenneService);
		updater.updateEntityFromStub(stub, entity, callback);
	}
	
	@Override
	public void setCayenneService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}
}
