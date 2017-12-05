package ish.oncourse.tapestry;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.apache.tapestry5.ioc.def.DecoratorDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.def.ServiceDef;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class WillowModuleDef implements ModuleDef {

	private static final Logger logger = LogManager.getLogger();

	private DataSourceServiceDef dataSourceServiceDef;
	private ServerRuntimeServiceDef serverRuntimeServiceDef;

	public WillowModuleDef(DataSource dataSource, ServerRuntime serverRuntime) {
		this.dataSourceServiceDef = new DataSourceServiceDef(dataSource);
		this.serverRuntimeServiceDef = new ServerRuntimeServiceDef(serverRuntime);
	}

	@Override
	public Set<String> getServiceIds() {
		LinkedHashSet<String> r = new LinkedHashSet<>();
		r.add(DataSourceServiceDef.ID);
		r.add(ServerRuntimeServiceDef.ID);
		return r;
	}

	@Override
	public ServiceDef getServiceDef(String serviceId) {
		switch (serviceId) {
			case DataSourceServiceDef.ID:
				return this.dataSourceServiceDef;
			case ServerRuntimeServiceDef.ID:
				return this.serverRuntimeServiceDef;
			default:
				throw new IllegalArgumentException(serviceId);
		}
	}

	@Override
	public Set<DecoratorDef> getDecoratorDefs() {
		return Collections.emptySet();
	}

	@Override
	public Set<ContributionDef> getContributionDefs() {
		return Collections.emptySet();
	}

	@Override
	public Class<?> getBuilderClass() {
		return null;
	}

	@Override
	public String getLoggerName() {
		return WillowModuleDef.class.getName();
	}
}
