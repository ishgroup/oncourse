package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.v21.builders.V21BuildersMap;
import ish.oncourse.webservices.replication.v22.builders.V22BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {


	private Map<String, IWillowStubBuilder> v21builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v22builderMap = new HashMap<>();


	public WillowStubBuilderImpl() {

		V21BuildersMap v21map = new V21BuildersMap();
		v21map.initMap();
		v21builderMap = v21map.getBuilderMap();

		V22BuildersMap v22map = new V22BuildersMap();
		v22map.initMap();
		v22builderMap = v22map.getBuilderMap();

    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {

			case V21:
				builder = v21builderMap.get(key);
				break;

			case V22:
				builder = v22builderMap.get(key);
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

			case V21:
				builder = v21builderMap.get(key);
				break;

			case V22:
				builder = v22builderMap.get(key);
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
