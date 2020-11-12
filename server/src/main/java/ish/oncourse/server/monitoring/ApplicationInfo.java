/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.api.servlet.ISessionManager;
import ish.oncourse.server.cayenne.Enrolment;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.MappedSelect;
import org.apache.commons.lang3.StringUtils;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import java.util.Map;

import static ish.oncourse.cayenne.MappedSelectParams.COUNT_COLUMN;
import static ish.oncourse.cayenne.MappedSelectParams.ENTITY_COUNT_QUERY;
import static ish.oncourse.cayenne.MappedSelectParams.ENTITY_NAME_PARAMETER;
import static ish.oncourse.cayenne.MappedSelectParams.WHERE_CLAUSE_PARAMETER;

public class ApplicationInfo extends StandardMBean implements ApplicationInfoMBean {

	private ICayenneService cayenneService;
	private ISessionManager sessionManager;
	private String version;
	private PreferenceController preferenceController;

	public ApplicationInfo(ICayenneService cayenneService, ISessionManager sessionManager, String version, PreferenceController preferenceController) throws NotCompliantMBeanException {
		super(ApplicationInfoMBean.class);
		this.cayenneService = cayenneService;
		this.sessionManager = sessionManager;
		this.version = version;
		this.preferenceController = preferenceController;
	}

	@Override
	public int getLoggedInUsersCount() {
		return sessionManager.getLoggedInUsersCount(preferenceController.getTimeoutThreshold());
	}

	@Override
	public int getSslPort() {
		return 0;
		/**
		 * TODO Refactoring
		 * return httpSessionService.getServerSslPort();
		 */
	}

	@Override
	public String getIpAddress() {

		String ip = null;

		/**
		 * TODO Refactoring
		 *

		 Connector[] connectors = httpSessionService.getConnectors();

		// picking up first of jetty's connectors (if any) and retrieving IP address which it is listening to

		if (connectors != null && connectors.length > 0) {
			String connectionName = connectors[0].getName();

			int ipEnd = connectionName.indexOf(IP_PORT_DELIMITER);

			if (ipEnd > 0) {
				ip = connectionName.substring(0, ipEnd);
			}
		}
		*/
		return ip;
	}

	@Override
	public int getEnrolmentsCount() {
		ObjectContext context = cayenneService.getSharedContext();

		MappedSelect query = MappedSelect.query(ENTITY_COUNT_QUERY)
				.param(ENTITY_NAME_PARAMETER, Enrolment.class.getSimpleName())
				.param(WHERE_CLAUSE_PARAMETER, StringUtils.EMPTY).forceNoCache();

		var row = (Map<String, Number>) context.performQuery(query).get(0);
		return row.get(COUNT_COLUMN).intValue();
	}

	@Override
	public String getVersion() {
		return version;
	}
}
