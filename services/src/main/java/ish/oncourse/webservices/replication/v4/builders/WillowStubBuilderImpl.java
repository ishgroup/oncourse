package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.v7.builders.V7BuildersMap;
import ish.oncourse.webservices.replication.v8.builders.V8BuildersMap;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.replication.v5.builders.V5BuildersMap;
import ish.oncourse.webservices.replication.v6.builders.V6BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {

	private Map<String, IWillowStubBuilder> v4builderMap = new HashMap<>();
	private Map<String, IWillowStubBuilder> v5builderMap = new HashMap<>();
	private Map<String, IWillowStubBuilder> v6builderMap = new HashMap<>();
	private Map<String, IWillowStubBuilder> v7builderMap = new HashMap<>();
	private Map<String, IWillowStubBuilder> v8builderMap = new HashMap<>();

	public WillowStubBuilderImpl() {
		final V4BuildersMap v4map = new V4BuildersMap();
		v4map.initMap();
		v4builderMap = v4map.getBuilderMap();
		final V5BuildersMap v5map = new V5BuildersMap();
		v5map.initMap();
		v5builderMap = v5map.getBuilderMap();
		final V6BuildersMap v6map = new V6BuildersMap();
		v6map.initMap();
		v6builderMap = v6map.getBuilderMap();
		V7BuildersMap v7map = new V7BuildersMap();
		v7map.initMap();
		v7builderMap = v7map.getBuilderMap();
		V8BuildersMap v8map = new V8BuildersMap();
		v8map.initMap();
		v8builderMap = v8map.getBuilderMap();
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
			case V6:
				builder = v6builderMap.get(key);
				break;
			case V7:
				builder = v7builderMap.get(key);
				break;
			case V8:
				builder = v8builderMap.get(key);
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
			case V6:
				builder = v6builderMap.get(key);
				break;
			case V7:
				builder = v7builderMap.get(key);
				break;
			case V8:
				builder = v8builderMap.get(key);
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
