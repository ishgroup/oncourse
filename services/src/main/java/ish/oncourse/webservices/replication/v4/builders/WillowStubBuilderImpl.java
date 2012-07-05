package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.replication.v5.builders.V5BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {

	private Map<String, IWillowStubBuilder> v4builderMap = new HashMap<String, IWillowStubBuilder>();
	private Map<String, IWillowStubBuilder> v5builderMap = new HashMap<String, IWillowStubBuilder>();

	public WillowStubBuilderImpl() {
		final V4BuildersMap v4map = new V4BuildersMap();
		v4map.initMap();
		v4builderMap = v4map.getBuilderMap();
		final V5BuildersMap v5map = new V5BuildersMap();
		v5map.initMap();
		v5builderMap = v5map.getBuilderMap();
    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {
			case V4:
				builder = v4builderMap.get(key);
				break;
			case V5:
				builder = v5builderMap.get(key);
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
			case V4:
				builder = v4builderMap.get(key);
				break;
			case V5:
				builder = v5builderMap.get(key);
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
