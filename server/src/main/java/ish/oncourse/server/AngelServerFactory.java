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
import ish.oncourse.server.cayenne.Site;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.db.SchemaUpdateService;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.jmx.RegisterMBean;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.messaging.EmailDequeueJob;
import ish.oncourse.server.messaging.MailDeliveryService;
import ish.oncourse.server.services.ISchedulerService;
import ish.oncourse.server.report.JRRuntimeConfig;
import ish.oncourse.server.security.CertificateUpdateWatcher;
import ish.oncourse.server.services.BackupJob;
import ish.oncourse.server.services.ChristmasThemeDisableJob;
import ish.oncourse.server.services.ChristmasThemeEnableJob;
import ish.oncourse.server.services.DelayedEnrolmentIncomePostingJob;
import ish.oncourse.server.services.FundingContractUpdateJob;
import ish.oncourse.server.services.InvoiceOverdueUpdateJob;
import ish.oncourse.server.services.VoucherExpiryJob;
import ish.persistence.Preferences;
import ish.security.AuthenticationUtil;
import ish.util.RuntimeUtil;
import ish.util.SecurityUtil;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.staxutils.StaxUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

import static ish.oncourse.server.api.v1.function.UserFunctions.sendInvitationEmailToNewSystemUser;
import static ish.oncourse.server.services.ISchedulerService.*;
import static ish.persistence.Preferences.ACCOUNT_CURRENCY;
import static ish.validation.ValidationUtil.isValidEmailAddress;


@BQConfig
public class AngelServerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AngelServerFactory.class);

    public final static String TXT_SYSTEM_USERS_FILE = "createAdminUsers.txt";
    public final static String CSV_SYSTEM_USERS_FILE = "createAdminUsers.csv";
    public static boolean QUIT_SIGNAL_CAUGHT = false;
               // specify if repliation in debug mode


    @Inject
    public AngelServerFactory() {
        ResourcesUtil.initialiseLogging(true);
        RuntimeUtil.assertDefaultLocale();

        // ensure that Jasper writes temp files to a directory with write permissions
        System.setProperty("jasper.reports.compile.temp", System.getProperty("java.io.tmpdir"));

        initJRGroovyCompiler();

        /**
         * We need to increase 'org.apache.cxf.stax.maxChildElements' property to 100000 because
         * willow side can replication more then 50000 (default value for the property) records.
         * See willow db, table module and ishVersion = 1166
         */
        System.setProperty(StaxUtils.MAX_CHILD_ELEMENTS, "100000");

        // set the location of default Ish jasperreports properties file
        System.setProperty(DefaultJasperReportsContext.PROPERTIES_FILE, "jasperreports.properties");

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
                      Scheduler scheduler, RegisterMBean registerMBean,
                      LicenseService licenseService,
                      CayenneService cayenneService,
                      PluginService pluginService,
                      MailDeliveryService mailDeliveryService) {
        try {

            // Create DB schema
            LOGGER.warn("Creating database schema");
            schemaUpdateService.updateSchema();

            LOGGER.warn("Upgrade data");
            schemaUpdateService.upgradeData();

            createSystemUsers(cayenneService.getNewContext(), licenseService.getCollege_key(), prefController, mailDeliveryService);

            if (licenseService.isAdmin_password_reset()) {
                resetAdminPassword(cayenneService.getNewContext());
            }

            
        } catch (Throwable e) {
            // total failure, some of the essential services cannot be
            // started
            throw new RuntimeException("Server start failed on basic level, aborting startup of other services", e);
        }

        // only if no fail until now start the background cron-like tasks
        try {
            LOGGER.warn("Configuring cron tasks");

            var random = new Random();

            // starting other services:
            // email hander (every minute)
            schedulerService.scheduleCronJob(EmailDequeueJob.class, EMAIL_DEQUEUEING_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    EMAIL_DEQUEUEING_JOB_INTERVAL, prefController.getOncourseServerDefaultTimezone(), false, false);

            // scheduling backup job only for derby
            if (Preferences.DATABASE_USED_DERBY.equals(prefController.getDatabaseUsed())) {
                // backup service (every hour)
                schedulerService.scheduleCronJob(BackupJob.class, BACKUP_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                        BACKUP_JOB_INTERVAL, prefController.getOncourseServerDefaultTimezone(), false, false);
            }

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

            //generate unique cron schedule between 1:00am and 1:59am  to ensure all our hosted colleges don't hit this code on the same millsecond
            var randomSchedule = String.format(INVOICE_OVERDUE_UPDATE_JOB_CRON_SCHEDULE_TEMPLATE, random.nextInt(59));
            //update overdue amount for unpaid invoices
            schedulerService.scheduleCronJob(InvoiceOverdueUpdateJob.class,
                    INVOICE_OVERDUE_UPDATE_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    randomSchedule, prefController.getOncourseServerDefaultTimezone(),
                    true, false);

            schedulerService.scheduleCronJob(FundingContractUpdateJob.class,
                    FUNDING_CONTRACT_JOB_ID,
                    BACKGROUND_JOBS_GROUP_ID,
                    FUNDING_CONTRACT_JOB_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    true,
                    false);

            schedulerService.scheduleCronJob(CertificateUpdateWatcher.class,
                    CERTIFICATE_UPDATE_WATCHER_ID, BACKGROUND_JOBS_GROUP_ID,
                    CERTIFICATE_UPDATE_WATCHER_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    false,
                    false);

            schedulerService.scheduleCronJob(ChristmasThemeEnableJob.class,
                    CHRISTMAS_THEME_ENABLE_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    CHRISTMAS_THEME_ENABLE_JOB_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    false,
                    false);

            schedulerService.scheduleCronJob(ChristmasThemeDisableJob.class,
                    CHRISTMAS_THEME_DISABLE_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                    CHRISTMAS_THEME_DISABLE_JOB_INTERVAL,
                    prefController.getOncourseServerDefaultTimezone(),
                    false,
                    false);

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

        try {
            LOGGER.warn("Initializing monitoring services");

            registerMBean.register();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize monitoring MBean.", e);
        }

        initJRGroovyCompiler();

        pluginService.onStart();

        LOGGER.warn("Server ready");
    }

    public void resetAdminPassword(DataContext context) {

        var admin = ObjectSelect.query(SystemUser.class).
                where(SystemUser.LOGIN.eq("admin")).
                selectOne(context);

        if (admin == null) {
            admin = UserDao.createSystemUser(context, Boolean.TRUE);
            admin.setLogin("admin");
            admin.setLastName("onCourse");
            admin.setFirstName("Administrator");
            admin.setDefaultAdministrationCentre(Site.getDefaultSite(context));
        }

        var password = SecurityUtil.generateRandomPassword(6);
        admin.setPassword(AuthenticationUtil.generatePasswordHash(password));

        context.commitChanges();

        LOGGER.warn("\n******************************************************************************************************************************\n" +
                "Administrator password reset command found in onCourse.yml \n" +
                "********** Account with name \"admin\" now has password \"{}\" \n" +
                "********** onCourse Server will now shut down. Remove the line starting \"admin_password_reset\" before restarting \n" +
                "******************************************************************************************************************************\n", password);

        crashServer();
    }

    private void createSystemUsers(DataContext context, String collegeKey, PreferenceController preferenceController, MailDeliveryService mailDeliveryService) throws IOException {
        Path systemUsersFile = Paths.get(CSV_SYSTEM_USERS_FILE);
        if (!systemUsersFile.toFile().exists()) {
            systemUsersFile = Paths.get(TXT_SYSTEM_USERS_FILE);
        }
        Stream<String> lines;
        try {
             lines = Files.lines(systemUsersFile);
        } catch (NoSuchFileException ignored) {
            LOGGER.warn("File with system users not found.");
            return;
        }
        if (collegeKey == null) {
            LOGGER.warn("College key is not set! Specify your college key in onCourse.yml, please.");
            crashServer();
        }
        lines.forEach(line -> {
            String[] lineData = line.split("(, )+|([ ,\t])+");

            if (lineData.length == 3) {
                String email = parseEmail(lineData[2]);
                if (email == null) {
                    LOGGER.warn("Specified email for user {} is not valid.", line);
                    return;
                }
                SystemUser user = UserDao.getByEmail(context, email);
                if (user != null) {
                    LOGGER.warn("System user {} already added.", line);
                    return;
                }

                user = UserDao.createSystemUser(context, Boolean.TRUE);
                user.setFirstName(lineData[0]);
                user.setLastName(lineData[1]);
                user.setEmail(email);

                try {
                    String invitationToken = sendInvitationEmailToNewSystemUser(null, user, preferenceController, mailDeliveryService, collegeKey);
                    user.setInvitationToken(invitationToken);
                    user.setInvitationTokenExpiryDate(DateUtils.addDays(new Date(), 1));
                } catch (MessagingException ex) {
                    LOGGER.warn("An invitation to user {} wasn't sent. Check you SMTP settings.", line);
                    return;
                }

                context.commitChanges();

                LOGGER.warn("System user {} have added successfully.", line);
            }
        });

        if (systemUsersFile.toFile().delete()) {
            LOGGER.warn("File with system users have deleted successfully!");
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

    private void initJRGroovyCompiler() {
        new JRRuntimeConfig().config();
    }


    private static void crashServer() {
        LOGGER.debug("crashServer!", new Exception());
        System.exit(1);
    }
}
