package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Site;
import ish.oncourse.model.WaitingList;
import ish.oncourse.model.WaitingListSite;
import ish.oncourse.webservices.v4.stubs.replication.WaitingListSiteStub;

public class WaitingListSiteUpdater extends AbstractWillowUpdater<WaitingListSiteStub, WaitingListSite> {

	@Override
	protected void updateEntity(WaitingListSiteStub stub, WaitingListSite entity, RelationShipCallback callback) {
		entity.setSite(callback.updateRelationShip(stub.getSiteId(), Site.class));
		entity.setWaitingList(callback.updateRelationShip(stub.getWaitingListId(), WaitingList.class));
	}
}
