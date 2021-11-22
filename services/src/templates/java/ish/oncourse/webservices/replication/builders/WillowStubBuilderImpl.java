package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.exception.BuilderNotFoundException;<% replicationVersions.each { v -> %>
import ish.oncourse.webservices.replication.v${v}.builders.V${v}BuildersMap;<% } %>
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;

import java.util.HashMap;
import java.util.Map;

public class WillowStubBuilderImpl implements IWillowStubBuilder {

<% replicationVersions.each { v -> %>
	private Map<String, IWillowStubBuilder> v${v}builderMap = new HashMap<>();
<% } %>

	public WillowStubBuilderImpl() {
<% replicationVersions.each { v -> %>
		V${v}BuildersMap v${v}map = new V${v}BuildersMap();
		v${v}map.initMap();
		v${v}builderMap = v${v}map.getBuilderMap();
<% } %>
    }

	@Override
	public GenericReplicationStub convert(QueuedRecord entity, final SupportedVersions version) {
		String key = entity.getEntityIdentifier();

		final IWillowStubBuilder builder;
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				builder = v${v}builderMap.get(key);
				break;
<% } %>
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
<% replicationVersions.each { v -> %>
			case V${v}:
				builder = v${v}builderMap.get(key);
				break;
<% } %>
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
