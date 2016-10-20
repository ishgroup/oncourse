package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.v10.builders.V10BuildersMap;
import ish.oncourse.webservices.replication.v11.builders.V11BuildersMap;
import ish.oncourse.webservices.replication.v12.builders.V12BuildersMap;
import ish.oncourse.webservices.replication.v13.builders.V13BuildersMap;
import ish.oncourse.webservices.replication.v14.builders.V14BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {
	

	private Map<String, IWillowStubBuilder> v10builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v11builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v12builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v13builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v14builderMap = new HashMap<>();

	public WillowStubBuilderImpl() {

		V10BuildersMap v10map = new V10BuildersMap();
		v10map.initMap();
		v10builderMap = v10map.getBuilderMap();

		V11BuildersMap v11map = new V11BuildersMap();
		v11map.initMap();
		v11builderMap = v11map.getBuilderMap();

		V12BuildersMap v12map = new V12BuildersMap();
		v12map.initMap();
		v12builderMap = v12map.getBuilderMap();

		V13BuildersMap v13map = new V13BuildersMap();
		v13map.initMap();
		v13builderMap = v13map.getBuilderMap();

		V14BuildersMap v14map = new V14BuildersMap();
		v14map.initMap();
		v14builderMap = v14map.getBuilderMap();

    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {

			case V10:
				builder = v10builderMap.get(key);
				break;

			case V11:
				builder = v11builderMap.get(key);
				break;

			case V12:
				builder = v12builderMap.get(key);
				break;

			case V13:
				builder = v13builderMap.get(key);
				break;

			case V14:
				builder = v14builderMap.get(key);
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

			case V10:
				builder = v10builderMap.get(key);
				break;

			case V11:
				builder = v11builderMap.get(key);
				break;

			case V12:
				builder = v12builderMap.get(key);
				break;

			case V13:
				builder = v13builderMap.get(key);
				break;

			case V14:
				builder = v14builderMap.get(key);
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
