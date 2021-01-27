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

import org.quartz.*;

import java.text.ParseException;

/**
 */
public interface ISchedulerService {

	String TRIGGER_POSTFIX = "Trigger";

	String EMAIL_DEQUEUEING_JOB_ID = "emailDequeueingJob";
	String BACKUP_JOB_ID = "backupJob";
	String DELAYED_ENROLMENT_INCOME_POSTING_JOB_ID = "delayedEnrolmentIncomePostingJob";
	String VOUCHER_EXPIRY_JOB_ID = "voucherExpiryJob";
	String INVOICE_OVERDUE_UPDATE_JOB_ID = "invoiceOverdueUpdateJob";
	String CERTIFICATE_UPDATE_WATCHER_ID = "certificateUpdateWatcher";
	String CHRISTMAS_THEME_ENABLE_JOB_ID = "christmasThemeUpdateJob";
	String CHRISTMAS_THEME_DISABLE_JOB_ID = "christmasThemeDisableJob";
	String PERMANENTLY_DELETE_DOCUMENTS_ID = "permanentlyDeleteDocumentsJob";

	String BACKGROUND_JOBS_GROUP_ID = "backgroundJobs";
	String CUSTOM_SCRIPT_JOBS_GROUP_ID = "customScriptJobs";

	String EMAIL_DEQUEUEING_JOB_INTERVAL = "45 * * * * ?";
	String BACKUP_JOB_INTERVAL = "0 0 * * * ?";
	String DELAYED_ENROLMENT_INCOME_POSTING_JOB_INTERVAL = "0 30 0am * * ?";
	String VOUCHER_EXPIRY_JOB_CRON_SCHEDULE = "0 45 0am * * ?";
	String INVOICE_OVERDUE_UPDATE_JOB_CRON_SCHEDULE_TEMPLATE = "0 %d 1am * * ?";

	String FUNDING_CONTRACT_JOB_ID = "fundingContractJob";
	String FUNDING_CONTRACT_JOB_INTERVAL = "1 15 0am * * ?";
	String CERTIFICATE_UPDATE_WATCHER_INTERVAL = "0 0 0/1 ? * * *";
	String CHRISTMAS_THEME_ENABLE_JOB_INTERVAL = "0 0 0 9 12 ? *";
	String CHRISTMAS_THEME_DISABLE_JOB_INTERVAL = "0 0 0 12 1 ? *";
	String PERMANENTLY_DELETE_DOCUMENTS_INTERVAL = "0 0 7 ? * SUN *";
	/**
	 * schedules a job with interval in seconds
	 *
	 * @param provider - class of the job
	 * @param id - unique identifier for the job
	 * @param groupId - group identifier for the job
	 * @param intervalInSeconds - interval in seconds
	 * @param startNow - whether the first execution of the job should be right now, then according to schedule
	 * @param preventWhenHeadlessStart - whether the job shoudl be instantiated in headless environment
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	void schedulePeriodicJob(Class<? extends Job> provider, String id, String groupId, int intervalInSeconds,
		boolean startNow, boolean preventWhenHeadlessStart) throws ParseException, SchedulerException;

	/**
	 * schedules a job qith cron-like parameter
	 *
	 * @param provider - class of the job
	 * @param id - unique identifier for the job
	 * @param groupId - group identifier for the job
	 * @param cron - cron-like string defining the task repetition
	 * @param startNow - whether the first execution of the job should be right now, then according to schedule
	 * @param preventWhenHeadlessStart - whether the job shoudl be instantiated in headless environment
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	void scheduleCronJob(Class<? extends Job> provider, String id, String groupId, String cron, String timeZoneId, boolean startNow,
		boolean preventWhenHeadlessStart) throws ParseException, SchedulerException;

	/**
	 * Schedules job defined in {@link JobDetail} with trigger. If job with the specified id already exists
	 * it will be unscheduled and then scheduled again with the specified trigger.
	 *
	 * @param jobDetail - job parameters
	 * @param trigger - trigger
	 * @throws SchedulerException
	 */
	void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException;

	/**
	 * Unschedules job with the specified {@link JobKey}.
	 *
	 * @param jobKey - job key to unschedule
	 * @throws SchedulerException
	 */
	void removeJob(JobKey jobKey) throws SchedulerException;

	String getCronExp(String name) throws SchedulerException;
}
