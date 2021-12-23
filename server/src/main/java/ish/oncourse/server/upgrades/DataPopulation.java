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
package ish.oncourse.server.upgrades;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import ish.oncourse.common.ResourceProperty;
import ish.oncourse.common.ResourceType;
import ish.oncourse.common.ResourcesUtil;
import ish.oncourse.server.AngelModule;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.report.IReportService;
import ish.report.ImportReportResult;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static ish.oncourse.server.upgrades.DataPopulationUtils.removeFromDbDeletedResources;

/**
 * Import resources from files into the database, such as scripts, imports, reports, etc
 */
public class DataPopulation implements Runnable {

	private static final Logger logger = LogManager.getLogger();

	private final String angelVersion;
	private final ICayenneService cayenneService;
	private final IReportService reportService;

	@Inject
	public DataPopulation(@Named(AngelModule.ANGEL_VERSION) String angelVersion, ICayenneService cayenneService, IReportService reportService) {
		this.angelVersion = angelVersion;
		this.cayenneService = cayenneService;
		this.reportService = reportService;
	}

	public static String getPropertyFromXml(final StringBuffer xmlData, final String property) {
		final var startString = "<property name=\"" + property + "\" value=\"";
		final var startIndex = xmlData.indexOf(startString);
		final var endIndex = xmlData.indexOf("\"/>", startIndex + startString.length());
		if (startIndex > 0 && endIndex > startIndex + startString.length()) {
			return xmlData.substring(startIndex + startString.length(), endIndex);
		}
		return null;
	}

	private List<Map<String, Object>> getResourcesList(ResourceType type) {
		List<Map<String, Object>> resourcesList = new ArrayList<>();

		String filePattern = type.getFilePattern();
		String resourcePath = type.getResourcePath();
		Set<String> filePaths = PluginService.getPluggableResources(resourcePath, filePattern);

		for (var path : filePaths) {
			try (InputStream resourceAsStream = ResourcesUtil.getResourceAsInputStream(path)) {
					Yaml yaml = new Yaml();
					Map<String, Object> yamlMap = yaml.load(resourceAsStream);
					resourcesList.add(yamlMap);
			} catch (IOException ex) {
				logger.warn("failed to import file {}: {}", type.getDisplayName(), path);
			}
		}
		return resourcesList;
	}

	public void run() {
		var context = cayenneService.getNewContext();

		logger.warn("Resource loading: reports");
		var resourcesList = PluginService.getPluggableResources(ResourceType.REPORT.getResourcePath(), ResourceType.REPORT.getFilePattern());
		Set<Long> importedReportsIds = new HashSet<>();
		for (var path : resourcesList) {
			try (InputStream inputStream = ResourcesUtil.getResourceAsInputStream(path)) {
				logger.debug("importing report {}", path);
				if (inputStream != null) {
					ImportReportResult reportResult = this.reportService.
							importReport(IOUtils.toString(inputStream, Charset.defaultCharset()));
					importedReportsIds.add(reportResult.getReportId());
					logger.debug("...imported");
				}
			} catch (Exception e ) {
				logger.error("Failed to import report: {}", path, e);
			}
		}
		logger.warn("Deleted from sourses resource removing: reports");
        DataPopulationUtils.removeDeletedReports(context, importedReportsIds);


        logger.warn("Resource loading: scripts");
		var scripts = getResourcesList(ResourceType.SCRIPT);
		scripts.forEach( props -> {
			try {
				DataPopulationUtils.updateScript(context, props);
			} catch (Exception e) {
				logger.error("{} {} was not imported", ResourceType.SCRIPT.getDisplayName(), props.get(ResourceProperty.NAME.getDisplayName()), e);
			}
		});
		removeFromDbDeletedResources(context, scripts, ResourceType.SCRIPT);

		logger.warn("Resource loading: imports");
		var imports = getResourcesList(ResourceType.IMPORT);
		imports.forEach( props -> {
			try {
				DataPopulationUtils.updateImport(context, props);
			} catch (Exception e) {
				logger.error("{} {} was not imported", ResourceType.IMPORT.getDisplayName(), props.get(ResourceProperty.NAME.getDisplayName()), e);
			}
		});
		removeFromDbDeletedResources(context, imports, ResourceType.IMPORT);

		logger.warn("Resource loading: message templates");
		var emailYamls = getResourcesList(ResourceType.MESSAGING);
		emailYamls.forEach( props -> {
			try {
				DataPopulationUtils.updateMessage(context, props);
			} catch (Exception e) {
				logger.error("{} {} was not importes", ResourceType.MESSAGING.getDisplayName(), props.get(ResourceProperty.NAME.getDisplayName()), e);
			}
		});
		removeFromDbDeletedResources(context, emailYamls, ResourceType.MESSAGING);

		logger.warn("Resource loading: exports");
		var exports = getResourcesList(ResourceType.EXPORT);
		exports.forEach(props -> {
			try {
				DataPopulationUtils.updateExport(context, props);
			} catch (Exception e) {
				logger.error("{} {} was not importes", ResourceType.EXPORT.getDisplayName(), props.get(ResourceProperty.NAME.getDisplayName()), e);
			}
		});
		removeFromDbDeletedResources(context, exports, ResourceType.EXPORT);

		logger.warn("Resource loading thread finished.");
	}
}
