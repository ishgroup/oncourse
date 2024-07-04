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

import com.google.inject.Inject;
import io.bootique.annotation.BQConfig;
import ish.math.Country;
import ish.math.CurrencyFormat;
import ish.oncourse.common.ResourcesUtil;
import ish.oncourse.server.api.dao.UserDao;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.db.SchemaUpdateService;
import ish.oncourse.server.http.HttpFactory;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.messaging.EmailDequeueJob;
import ish.oncourse.server.messaging.MailDeliveryService;
import ish.oncourse.server.services.ISchedulerService;
import ish.oncourse.server.services.*;
import ish.persistence.Preferences;
import ish.util.RuntimeUtil;
import org.apache.cayenne.access.DataContext;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.yaml.snakeyaml.Yaml;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static ish.oncourse.server.api.v1.function.UserFunctions.sendInvitationEmailToNewSystemUser;
import static ish.oncourse.server.services.ISchedulerService.*;
import static ish.persistence.Preferences.ACCOUNT_CURRENCY;
import static ish.validation.ValidationUtil.isValidEmailAddress;


@BQConfig
public class AngelServerFactory {

    private static final Logger LOGGER =  LogManager.getLogger();

    public final static String YAML_SYSTEM_USERS_FILE = "createAdminUsers.yaml";
    public static boolean QUIT_SIGNAL_CAUGHT = false;


    @Inject
    public AngelServerFactory() {
        ResourcesUtil.initialiseLogging(true);
        RuntimeUtil.assertDefaultLocale();

        /**
         * We need to increase 'org.apache.cxf.stax.maxChildElements' property to 100000 because
         * willow side can replicate more than 50000 (default value for the property) records.
         */
        System.setProperty(StaxUtils.MAX_CHILD_ELEMENTS, "100000");

        // this.applicationThread = Thread.currentThread();
        LOGGER.debug("AngelServer constructing... [{}:{}]", Thread.currentThread().getThreadGroup().getName(), Thread.currentThread().getName());

        // ----------------------------------------------
        final var version = ResourcesUtil.getReleaseVersionString();
        // 1.a. initialise logging and OS specific options
        LOGGER.warn("onCourse server (build {}) starting...", version);

        // 2. log debugging info: classpath and user dir
        LOGGER.debug("Classpath is:\t\t'{}", System.getProperty("java.class.path"));
        LOGGER.debug("User directory is:\t\t'{}", System.getProperty("user.dir"));

    }

    /**
     * Starts the server.
     */
    public void start(PreferenceController prefController,
                      SchemaUpdateService schemaUpdateService,
                      ISchedulerService schedulerService,
                      Scheduler scheduler,
                      LicenseService licenseService,
                      CayenneService cayenneService,
                      PluginService pluginService,
                      MailDeliveryService mailDeliveryService,
                      HttpFactory httpFactory) {
        try {

            // Create DB schema
            LOGGER.warn("Creating database schema");
            schemaUpdateService.updateSchema();

            LOGGER.warn("Upgrade data");
            schemaUpdateService.upgradeData();
            createSystemUsers(cayenneService.getNewContext(), licenseService.getCurrentHostName(), httpFactory.getIp(), httpFactory.getPort(), prefController, mailDeliveryService);

        } catch (Throwable e) {
            LOGGER.catching(e);
            LOGGER.error("Server start failed on basic level, aborting startup of other services");
            // total failure, some essential services cannot be started
            throw new RuntimeException("Server start failed to create or update the database. Aborting startup of other services.", e);
        }

        try {
            LOGGER.warn("Configuring cron tasks");

            // randomise the cron jobs so hosted sites don't all run at once
            var random = new Random();

            // starting other services:
            // email hander (every minute)
            schedulerService.scheduleCronJob(EmailDequeueJob.class, EMAIL_DEQUEUEING_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    EMAIL_DEQUEUEING_JOB_INTERVAL, prefController.getOncourseServerDefaultTimezone(), false, false);

            // job responsible for GL transfers for delayed income feature
            schedulerService.scheduleCronJob(DelayedEnrolmentIncomePostingJob.class,
                    DELAYED_ENROLMENT_INCOME_POSTING_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    DELAYED_ENROLMENT_INCOME_POSTING_JOB_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(), true, false);

            // job responsible for GL transfers for voucher expiry
            schedulerService.scheduleCronJob(VoucherExpiryJob.class,
                    VOUCHER_EXPIRY_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    VOUCHER_EXPIRY_JOB_CRON_SCHEDULE,
                    prefController.getOncourseServerDefaultTimezone(),
                    true, false);

            if ((Boolean) prefController.getValueForKey(Preferences.AUTO_DISABLE_INACTIVE_ACCOUNT)) {
                // between 3:00 and 3:59 every Monday disable inactive 4 years or never loged in users
                var randomSchedule = String.format(USER_DISAIBLE_JOB_TEMPLATE, random.nextInt(59));
                schedulerService.scheduleCronJob(UserDisableJob.class,
                        USER_DISAIBLE_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                        randomSchedule, prefController.getOncourseServerDefaultTimezone(),
                        true, false);
            }

            // between 1:00am and 1:59am update overdue amount for unpaid invoices
            var randomSchedule = String.format(INVOICE_OVERDUE_UPDATE_JOB_CRON_SCHEDULE_TEMPLATE, random.nextInt(59));
            schedulerService.scheduleCronJob(InvoiceOverdueUpdateJob.class,
                    INVOICE_OVERDUE_UPDATE_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    randomSchedule, prefController.getOncourseServerDefaultTimezone(),
                    true, false);

            // between 2:00am and 2:59am purge the audit table
            randomSchedule = String.format(AUDIT_PURGE_JOB_CRON_SCHEDULE_TEMPLATE, random.nextInt(59));
            schedulerService.scheduleCronJob(AuditPurgeJob.class, AUDIT_PURGE_JOB, BACKGROUND_JOBS_GROUP_ID,
                    randomSchedule, prefController.getOncourseServerDefaultTimezone(),
                    false, false);

            schedulerService.scheduleCronJob(FundingContractUpdateJob.class,
                    FUNDING_CONTRACT_JOB_ID,
                    BACKGROUND_JOBS_GROUP_ID,
                    FUNDING_CONTRACT_JOB_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    true,
                    false);

            /*schedulerService.scheduleCronJob(CertificateUpdateWatcher.class,
                    CERTIFICATE_UPDATE_WATCHER_ID, BACKGROUND_JOBS_GROUP_ID,
                    CERTIFICATE_UPDATE_WATCHER_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    false,
                    false);*/

            
            // disable cristmas theme automatical update since we did not prepare anything this year (2021-2022)
            schedulerService.removeJob(JobKey.jobKey(CHRISTMAS_THEME_DISABLE_JOB_ID,BACKGROUND_JOBS_GROUP_ID ));
            schedulerService.removeJob(JobKey.jobKey(CHRISTMAS_THEME_ENABLE_JOB_ID,BACKGROUND_JOBS_GROUP_ID ));
            

            schedulerService.scheduleCronJob(PermanentlyDeleteDocumentsJob.class,
                    PERMANENTLY_DELETE_DOCUMENTS_ID, BACKGROUND_JOBS_GROUP_ID,
                    PERMANENTLY_DELETE_DOCUMENTS_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    false,
                    false);

            LOGGER.warn("Starting cron");
            scheduler.start();
            var preference = prefController.getPreference(ACCOUNT_CURRENCY, false);
            if ((preference != null) && (preference.getValueString() != null)) {
                var country = Country.forCurrencySymbol(preference.getValueString());
                CurrencyFormat.updateLocale(country.locale());
            } else {
                prefController.setValue(ACCOUNT_CURRENCY, false, Country.AUSTRALIA.currencySymbol());
                CurrencyFormat.updateLocale(Country.AUSTRALIA.locale());
            }

        } catch (SchedulerException e1) {
            throw new RuntimeException("Server scheduler failed to initialise, aborting startup", e1);
        } catch (ParseException e2) {
            throw new RuntimeException("Scheduled service failed to initialise, aborting startup", e2);
        }

        pluginService.onStart();

        LOGGER.warn("Server ready.");
    }

    private void createSystemUsers(DataContext context, String hostName, String ipAddress, Integer port, PreferenceController preferenceController, MailDeliveryService mailDeliveryService) {
        Yaml yaml = new Yaml();
        Path systemUsersFile = Paths.get(YAML_SYSTEM_USERS_FILE);
        List<Map<String, Object>> users;
        try (InputStream inputStream = new FileInputStream(systemUsersFile.toFile())) {
            users = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            LOGGER.warn("File with system users not found.");
            return;
        } catch (Exception e) {
            LOGGER.error("Can not parse system users file.", e);
            return;
        }

        users.forEach(userYaml -> {
            if (userYaml.get("first") == null || userYaml.get("last") == null || userYaml.get("email") == null) {
                LOGGER.warn("Incorrect user format. User {} wasn't created.", userYaml);
                return;
            }
            String email = parseEmail(String.valueOf(userYaml.get("email")));
            if (email == null) {
                LOGGER.warn("Specified email for user {} is not valid.", userYaml.get("email"));
                return;
            }
            SystemUser user = UserDao.getByEmail(context, email);
            if (user != null) {
                user.setPassword(null);
                user.setPasswordLastChanged(null);
                user.setLoginAttemptNumber(0);
                user.setIsActive(true);
            } else {
                user = UserDao.createSystemUser(context, Boolean.TRUE);
                user.setEmail(email);
            }

            user.setFirstName(String.valueOf(userYaml.get("first")));
            user.setLastName(String.valueOf(userYaml.get("last")));

            try {
                String invitationToken = sendInvitationEmailToNewSystemUser(null, user, preferenceController, mailDeliveryService, hostName, ipAddress, port);
                user.setInvitationToken(invitationToken);
                user.setInvitationTokenExpiryDate(DateUtils.addDays(new Date(), 7));
            } catch (MessagingException ex) {
                LOGGER.catching(ex);
                LOGGER.warn("An invitation to user {} wasn't sent. Check you SMTP settings.", userYaml);
                return;
            }

            context.commitChanges();

            LOGGER.warn("System user {} has been added successfully.", userYaml);
        });

        if (systemUsersFile.toFile().delete()) {
            LOGGER.warn("The file with system users has been deleted successfully!");
        }
    }

    private String parseEmail(String specifiedEmail) {
        if (specifiedEmail.startsWith("<")) {
            specifiedEmail = specifiedEmail.substring(1);
        }
        if (specifiedEmail.endsWith(">")) {
            specifiedEmail = specifiedEmail.substring(0, specifiedEmail.length()-1);
        }
        if (isValidEmailAddress(specifiedEmail)) {
            return specifiedEmail;
        }
        return null;
    }

}
