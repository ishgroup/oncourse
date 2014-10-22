package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.SurveyStub;

public class SurveyUpdater extends AbstractWillowUpdater<SurveyStub, Survey> {
    @Override
    protected void updateEntity(SurveyStub stub, Survey entity, RelationShipCallback callback) {
	}
}
