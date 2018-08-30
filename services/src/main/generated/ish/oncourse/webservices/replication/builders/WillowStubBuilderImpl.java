package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.v13.builders.V13BuildersMap;
import ish.oncourse.webservices.replication.v17.builders.V17BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {


	private Map<String, IWillowStubBuilder> v13builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v17builderMap = new HashMap<>();


	public WillowStubBuilderImpl() {

		V13BuildersMap v13map = new V13BuildersMap();
		v13map.initMap();
		v13builderMap = v13map.getBuilderMap();

		V17BuildersMap v17map = new V17BuildersMap();
		v17map.initMap();
		v17builderMap = v17map.getBuilderMap();

    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {

			case V13:
				builder = v13builderMap.get(key);
				break;

			case V17:
				builder = v17builderMap.get(key);
				break;

			default:
				builder = null;
				break;
		}

		if (builder == null) {
			throw new BuilderNotFoundException(String.format("Builder not found during record conversion: %s.", key),
					key);
		}

		return builder.convert(entity, version);
	}

	@Override
	public GenericReplicationStub convert(Queueable entity, final SupportedVersions version) {
		String key = entity.getObjectId().getEntityName();

		final IWillowStubBuilder builder;
		switch (version) {

			case V13:
				builder = v13builderMap.get(key);
				break;

			case V17:
				builder = v17builderMap.get(key);
				break;

			default:
				builder = null;
				break;
		}

		if (builder == null) {
			throw new BuilderNotFoundException(String.format("Builder not found during record conversion: %s.", key),
					key);
		}

		return builder.convert(entity, version);
	}
}
