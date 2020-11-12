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
package ish.oncourse.server;

import org.apache.cayenne.DataChannelQueryFilter;
import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;

import javax.sql.DataSource;


/**
 */
public interface ICayenneService {

	DataContext getNewContext();

	DataContext getSharedContext();

	DataContext getNewNonReplicatingContext();

	DataContext getNewReadonlyContext();

	DataNode getDataNode();

	/**
	 * Registers an annotated persistent object event listener.
	 */
	void addListener(Object listener);

	/**
	 * Register data channel sync filter
	 */
	void addSyncFilter(DataChannelSyncFilter filter);

	/**
	 * Register data channel query filter
	 */
	void addQueryFilter(DataChannelQueryFilter filter);

	ServerRuntime getServerRuntime();

	DataSource getDataSource();
}
