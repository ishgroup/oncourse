package ish.oncourse.cms.services.persistence;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.cms.services.state.ISessionStoreService;
import ish.oncourse.model.services.cache.ICacheService;
import ish.oncourse.model.services.persistence.CayenneService;

public class CMSCayenneService extends CayenneService {

	@Inject
	private ISessionStoreService storeService;

	public CMSCayenneService(ICacheService cacheService) {
		super(cacheService);
	}

	@Override
	public ObjectContext sharedContext() {
		return (storeService.sessionContext() != null) ? storeService
				.sessionContext() : super.sharedContext();
	}
}
