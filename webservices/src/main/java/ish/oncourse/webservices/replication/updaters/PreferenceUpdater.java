package ish.oncourse.webservices.replication.updaters;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import ish.oncourse.model.Preference;
import ish.oncourse.webservices.v4.stubs.replication.PreferenceStub;

public class PreferenceUpdater extends AbstractWillowUpdater<PreferenceStub, Preference> {

	private static final String REPL_PROP = "replication.v4.enabled";

	@Override
	protected void updateEntity(PreferenceStub stub, Preference entity, RelationShipCallback callback) {

		if (stub.getName().equals(REPL_PROP)) {
			ObjectContext ctx = entity.getObjectContext();
			
			Preference p = getReplicationPreference(ctx);
			p.setValueString(stub.getValueString());
			
			ctx.deleteObject(entity);
			
			return;
		}

		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setValue(stub.getValue());
		entity.setValueString(stub.getValueString());
	}

	private Preference getReplicationPreference(ObjectContext ctx) {
		SelectQuery q = new SelectQuery(Preference.class);
		q.andQualifier(ExpressionFactory.matchExp(Preference.NAME_PROPERTY, REPL_PROP));
		return (Preference) Cayenne.objectForQuery(ctx, q);
	}
}
