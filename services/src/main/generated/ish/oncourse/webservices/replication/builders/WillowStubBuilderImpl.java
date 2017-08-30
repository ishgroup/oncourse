package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.v10.builders.V10BuildersMap;
import ish.oncourse.webservices.replication.v13.builders.V13BuildersMap;
import ish.oncourse.webservices.replication.v14.builders.V14BuildersMap;
import ish.oncourse.webservices.replication.v15.builders.V15BuildersMap;
import ish.oncourse.webservices.replication.v16.builders.V16BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {


	private Map<String, IWillowStubBuilder> v10builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v13builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v14builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v15builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v16builderMap = new HashMap<>();


	public WillowStubBuilderImpl() {

		V10BuildersMap v10map = new V10BuildersMap();
		v10map.initMap();
		v10builderMap = v10map.getBuilderMap();

		V13BuildersMap v13map = new V13BuildersMap();
		v13map.initMap();
		v13builderMap = v13map.getBuilderMap();

		V14BuildersMap v14map = new V14BuildersMap();
		v14map.initMap();
		v14builderMap = v14map.getBuilderMap();

		V15BuildersMap v15map = new V15BuildersMap();
		v15map.initMap();
		v15builderMap = v15map.getBuilderMap();

		V16BuildersMap v16map = new V16BuildersMap();
		v16map.initMap();
		v16builderMap = v16map.getBuilderMap();

    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {

			case V10:
				builder = v10builderMap.get(key);
				break;

			case V13:
				builder = v13builderMap.get(key);
				break;

			case V14:
				builder = v14builderMap.get(key);
				break;

			case V15:
				builder = v15builderMap.get(key);
				break;

			case V16:
				builder = v16builderMap.get(key);
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

			case V13:
				builder = v13builderMap.get(key);
				break;

			case V14:
				builder = v14builderMap.get(key);
				break;

			case V15:
				builder = v15builderMap.get(key);
				break;

			case V16:
				builder = v16builderMap.get(key);
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
