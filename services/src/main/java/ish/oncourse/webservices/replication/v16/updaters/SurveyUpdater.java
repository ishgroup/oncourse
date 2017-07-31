package ish.oncourse.webservices.replication.v16.updaters;

import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v16.stubs.replication.SurveyStub;

public class SurveyUpdater extends AbstractWillowUpdater<SurveyStub, Survey> {
    @Override
    protected void updateEntity(SurveyStub stub, Survey entity, RelationShipCallback callback) {
	}
}
