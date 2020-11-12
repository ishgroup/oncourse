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

package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.persistence.CommonPreferenceController;
import ish.util.DateFormatter;
import ish.util.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This thread wakes up periodically to perform backups of the data file. should blocks all other quartz Jobs, but this is not implemented yet.
 */
@DisallowConcurrentExecution
public class BackupJob implements Job {

	private static final Logger logger = LogManager.getLogger();

	private static final NumberFormat _NumberFormatter = new DecimalFormat("#00000.##");

	private static final String BACKUP_FILENAME_PATTERN = "Backup_(\\d+)\\.zip";
	private Pattern backupFilenamePattern;

	private final ICayenneService cayenneService;

	/**
	 * creates an instance of BackupJob
	 *
	 * @param cayenneService
	 */
	@Inject
	public BackupJob(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;

		try {
			final var databaseName
					= cayenneService
					.getDataSource()
					.getConnection()
					.getMetaData()
					.getDatabaseProductName();

			backupFilenamePattern = Pattern.compile(databaseName + BACKUP_FILENAME_PATTERN);
		} catch (Exception e1) {
			throw new IllegalStateException("pattern for backup cannot be compiled", e1);
		}
	}

	/**
	 * finds the last number of backup based on the path to backup directory
	 *
	 * @param backupDir path to directory
	 * @return Number of last backpu file
	 */
	private Number apparentLastNumber(final File backupDir) {
		if (backupDir != null && backupDir.isDirectory()) {

			String[] fileNames;

			fileNames = backupDir.list(new FilenameFilter() {

				@Override
				public boolean accept(final File dir, final String name) {
					return name != null && backupFilenamePattern.matcher(name).matches();
				}
			});
			if (fileNames != null && fileNames.length > 0) {
				Matcher m;

				Arrays.sort(fileNames);
				for (var i = fileNames.length - 1; i >= 0; i--) {
					try {
						m = backupFilenamePattern.matcher(fileNames[i]);
						if (m.matches() && m.groupCount() == 1) {
							return Integer.valueOf(m.group(1));
						}
					} catch (final Exception e) {
						// not important exception
						logger.catching(e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * invoked by quartz.<BR/>
	 * <BR/>
	 * then
	 * <ul>
	 * <li>checks if the hour of the day matches the preference</li>
	 * <li>validates the path to the backup directory</li>
	 * <li>performs backup</li>
	 * <li>deletes backup files older than specified number</li>
	 * </ul>
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.warn("performing backup");
		var preferenceController = PreferenceController.getController();

		var backupEnabled = preferenceController.getBackupEnabled();
		var backupHour = preferenceController.getBackupOnMinuteOfDay() / 60;

		if (!backupEnabled || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) != backupHour) {
			return;
		}

		var prefDir = PreferenceController.getController().getBackupDir();
		var defaultBackupLocation = new File(".");

		// check the preferred backup location
		if (StringUtils.isEmpty(prefDir)) {
			// if it is not set use the current dir
			prefDir = ".";

			var message = "Preference for backup destinaton directory is not set, backup saved in the default location: " +
					defaultBackupLocation.getAbsolutePath();
			logger.warn(message);
			PreferenceController.getController().setBackupDirWarning(message);

		} else if (!new File(prefDir).exists() || !new File(prefDir).isDirectory()) {
			// if the directory does not exist use current dir
			prefDir = ".";

			var message = "Backup destinaton (" + prefDir + ") does not exist or its not a directory, backup saved in the default location." +
					defaultBackupLocation.getAbsolutePath();
			logger.warn(message);
			PreferenceController.getController().setBackupDirWarning(message);
		}

		final var backupDirectory = new File(prefDir);

		// attempt to perform a backup
		logger.debug("performBackup to {}", prefDir);
		if (!backupDirectory.canRead() || !backupDirectory.canWrite()) {
			final var message = "onCourse backup cannot be performed. Insufficient privileges for directory:" + backupDirectory.getAbsolutePath();
			logger.warn(message);
			PreferenceController.getController().setBackupDirWarning(message);
		} else {
			Number currentNumber;
			final int nextNumber;
			final String destinationName;
			final File destinationFile;
			Connection connection = null;
			CallableStatement statement = null;

			logger.debug("performBackup: {}", backupDirectory);

			PreferenceController.getController().setBackupDirWarning(null);

			// get and increment global preference for next backup number (after all)

			currentNumber = PreferenceController.getController().getBackupNextNumber();
			logger.debug("performBackup knownCurrentNumber:{}", currentNumber);
			if (currentNumber == null) {
				currentNumber = apparentLastNumber(backupDirectory);
			}
			logger.debug("performBackup currentNumber:{}", currentNumber);
			nextNumber = currentNumber == null ? 1 : currentNumber.intValue();
			logger.debug("performBackup nextNumber:{}", nextNumber);
			PreferenceController.getController().setBackupNextNumber(nextNumber + 1);
			logger.debug("performBackup stored next number");

			// build backup path
			try {
				connection = this.cayenneService.getDataSource()
									.getConnection();

				destinationName = connection.getMetaData().getDatabaseProductName() + "Backup_" + _NumberFormatter.format(nextNumber);

				destinationFile = new File(backupDirectory, destinationName);

				logger.debug("performBackup saving to ({})", destinationFile);

				// execute backup
				statement = connection.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE_AND_ENABLE_LOG_ARCHIVE_MODE(?, ?)");
				statement.setString(1, destinationFile.getAbsolutePath());
				statement.setInt(2, 1); // deletes archived logs after successful backup
				statement.execute();
			} catch (Exception e1) {
				// re throw as JobExecution exception
				throw new JobExecutionException(e1);
			} finally {
				// close the connection gracefully, regardless of backup result
				try {
					statement.close();
					connection.close();
				} catch (SQLException e) {
					// ignore this exception.
				}
			}

			logger.debug("performBackup compressing...");

			// Now we need to compress the resulting folder
			try {
				File[] files;
				Integer preferredHistory;
				int minNumber;
				final int minBackupNumber;

				ZipUtil.compress(destinationFile);

				logger.debug("performBackup removing pre-compressed backup...");

				//
				// now remove the unzipped file
				//
				if (!ZipUtil.deleteFile(destinationFile)) {
					logger.error("failed to remove unzipped backup:{}", destinationFile.getAbsolutePath());
				}

				logger.debug("performBackup get preferred history...");
				preferredHistory = PreferenceController.getController().getBackupMaxNumber();
				minNumber = nextNumber;
				if (preferredHistory != null) {
					minNumber -= preferredHistory;
				} else {
					minNumber = 0;
				}

				minBackupNumber = minNumber;
				logger.debug("performBackup min number to find:{}", minBackupNumber);

				//
				// remove any backups that are outside the range of maxbackups held
				// 1. get list of files to delete
				// 2. delete each file
				//
				files = backupDirectory.listFiles(new FileFilter() {
					@Override
					public boolean accept(final File pathname) {
						if (pathname != null && backupDirectory.equals(pathname.getParentFile())) {

							var m = backupFilenamePattern.matcher(pathname.getName());
							if (m != null && m.matches() && m.groupCount() == 1) {
								var num = m.group(1);
								try {
									var value = _NumberFormatter.parse(num);

									return value.intValue() <= minBackupNumber;

								} catch (final ParseException e) {
									logger.error("unexpected parse of backup number", e);
								}
							}
						}
						return false;
					}
				});
				logger.debug("performBackup removing oldies...");
				for (final var afile : files) {
					if (afile != null && !afile.delete()) {
						logger.error("failed to remove previous backup:{}", afile.getName());
					}
				}
				logger.debug("performBackup removing oldies...done");

				if (StringUtils.isEmpty(PreferenceController.getController().getBackupDirWarning())) {
					var message = "Backup successfully completed to " + backupDirectory.getAbsolutePath() + " at " +
							DateFormatter.formatDate(new Date(), true);
					PreferenceController.getController().setBackupDirWarning(message);
				}

			} catch (final IOException e) {
				logger.error("failed during backup zip, release:{}", destinationFile, e);
			}
		}

		logger.debug("end performBackup");
	}
}
