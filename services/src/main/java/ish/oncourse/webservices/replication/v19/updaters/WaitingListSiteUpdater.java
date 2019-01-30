package ish.oncourse.webservices.replication.v19.updaters;

import ish.oncourse.model.Site;
import ish.oncourse.model.WaitingList;
import ish.oncourse.model.WaitingListSite;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v19.stubs.replication.WaitingListSiteStub;

public class WaitingListSiteUpdater extends AbstractWillowUpdater<WaitingListSiteStub, WaitingListSite> {

	@Override
	protected void updateEntity(WaitingListSiteStub stub, WaitingListSite entity, RelationShipCallback callback) {
		entity.setSite(callback.updateRelationShip(stub.getSiteId(), Site.class));
		entity.setWaitingList(callback.updateRelationShip(stub.getWaitingListId(), WaitingList.class));
	}
}
