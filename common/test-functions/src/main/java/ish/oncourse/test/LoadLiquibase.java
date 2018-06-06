/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import liquibase.Liquibase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: akoiro
 * Date: 5/6/18
 */
public class LoadLiquibase {
	private String liquibaseChangelog = "liquibase.db.changelog.xml";


	private List<String> runChangeSet = new LinkedList<>();
	private MariaDB mariaDB;
	private boolean loadPostcodes = false;

	public LoadLiquibase mariaDB(MariaDB mariaDB) {
		this.mariaDB = mariaDB;
		return this;
	}

	public LoadLiquibase changeSet(String... changeSet) {
		runChangeSet.addAll(Arrays.stream(changeSet).collect(Collectors.toList()));
		return this;
	}

	public LoadLiquibase loadPostcodes(boolean loadPostcodes) {
		this.loadPostcodes = loadPostcodes;
		return this;
	}

	public LoadLiquibase load() {
		try {
			Connection connection = DriverManager.getConnection(mariaDB.getUrl(), mariaDB.getUser(), mariaDB.getPassword());

			Liquibase liquibase = Functions.createLiquibase(liquibaseChangelog, connection);
			liquibase.changeLogSync("production");

			runChangeSet.forEach((id) -> Functions.deleteChangeSet(mariaDB, id));

			liquibase.update("production");

			if (loadPostcodes)
				new LoadDataSet().dataSetFile("test.postcode_db.xml").load(connection);

			connection.close();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}
}