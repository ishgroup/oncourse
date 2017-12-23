package ish.oncourse.webservices.replication.updaters;

import ish.common.types.EntityMapping;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.v10.updaters.V10UpdatersMap;
import ish.oncourse.webservices.replication.v13.updaters.V13UpdatersMap;
import ish.oncourse.webservices.replication.v14.updaters.V14UpdatersMap;
import ish.oncourse.webservices.replication.v15.updaters.V15UpdatersMap;
import ish.oncourse.webservices.replication.v16.updaters.V16UpdatersMap;
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
 
	private Map<String, IWillowUpdater> v10updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v13updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v14updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v15updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v16updaterMap = new HashMap<>();
 
	private Map<String, IWillowUpdater> v17updaterMap = new HashMap<>();

	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {


		V10UpdatersMap v10map = new V10UpdatersMap();
		v10map.initMap(textileConverter);
		v10updaterMap = v10map.getUpdaterMap();

		V13UpdatersMap v13map = new V13UpdatersMap();
		v13map.initMap(textileConverter);
		v13updaterMap = v13map.getUpdaterMap();

		V14UpdatersMap v14map = new V14UpdatersMap();
		v14map.initMap(textileConverter);
		v14updaterMap = v14map.getUpdaterMap();

		V15UpdatersMap v15map = new V15UpdatersMap();
		v15map.initMap(textileConverter);
		v15updaterMap = v15map.getUpdaterMap();

		V16UpdatersMap v16map = new V16UpdatersMap();
		v16map.initMap(textileConverter);
		v16updaterMap = v16map.getUpdaterMap();

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

			case V10:
				updater = v10updaterMap.get(key);
				break;

			case V13:
				updater = v13updaterMap.get(key);
				break;

			case V14:
				updater = v14updaterMap.get(key);
				break;

			case V15:
				updater = v15updaterMap.get(key);
				break;

			case V16:
				updater = v16updaterMap.get(key);
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
