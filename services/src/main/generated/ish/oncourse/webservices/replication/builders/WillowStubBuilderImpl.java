package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.replication.v10.builders.V10BuildersMap;
import ish.oncourse.webservices.replication.v11.builders.V11BuildersMap;
import ish.oncourse.webservices.replication.v12.builders.V12BuildersMap;
import ish.oncourse.webservices.replication.v6.builders.V6BuildersMap;
import ish.oncourse.webservices.replication.v7.builders.V7BuildersMap;
import ish.oncourse.webservices.replication.v9.builders.V9BuildersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {


	private Map<String, IWillowStubBuilder> v6builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v7builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v9builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v10builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v11builderMap = new HashMap<>();

	private Map<String, IWillowStubBuilder> v12builderMap = new HashMap<>();

	public WillowStubBuilderImpl() {

		V6BuildersMap v6map = new V6BuildersMap();
		v6map.initMap();
		v6builderMap = v6map.getBuilderMap();

		V7BuildersMap v7map = new V7BuildersMap();
		v7map.initMap();
		v7builderMap = v7map.getBuilderMap();

		V9BuildersMap v9map = new V9BuildersMap();
		v9map.initMap();
		v9builderMap = v9map.getBuilderMap();

		V10BuildersMap v10map = new V10BuildersMap();
		v10map.initMap();
		v10builderMap = v10map.getBuilderMap();

		V11BuildersMap v11map = new V11BuildersMap();
		v11map.initMap();
		v11builderMap = v11map.getBuilderMap();

		V12BuildersMap v12map = new V12BuildersMap();
		v12map.initMap();
		v12builderMap = v12map.getBuilderMap();

    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {

			case V6:
				builder = v6builderMap.get(key);
				break;

			case V7:
				builder = v7builderMap.get(key);
				break;

			case V9:
				builder = v9builderMap.get(key);
				break;

			case V10:
				builder = v10builderMap.get(key);
				break;

			case V11:
				builder = v11builderMap.get(key);
				break;

			case V12:
				builder = v12builderMap.get(key);
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

			case V6:
				builder = v6builderMap.get(key);
				break;

			case V7:
				builder = v7builderMap.get(key);
				break;

			case V9:
				builder = v9builderMap.get(key);
				break;

			case V10:
				builder = v10builderMap.get(key);
				break;

			case V11:
				builder = v11builderMap.get(key);
				break;

			case V12:
				builder = v12builderMap.get(key);
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
