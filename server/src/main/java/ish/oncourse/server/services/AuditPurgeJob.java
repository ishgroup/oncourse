/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.license.LicenseService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLExec;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

@DisallowConcurrentExecution
public class AuditPurgeJob implements Job {

    private static final Logger logger = LogManager.getLogger();

    private ICayenneService cayenneService;
    private LicenseService licenseService;

    @Inject
    AuditPurgeJob(ICayenneService cayenneService, LicenseService licenseService) {
        this.cayenneService = cayenneService;
        this.licenseService = licenseService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ObjectContext objectContext = cayenneService.getNewContext();
        Date sysDate = DateUtils.addMonths(new Date(), (-1) * licenseService.getMax_audit_log_store());
        String sysDateString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sysDate);
        String sql = String.format("DELETE FROM Audit WHERE created < '%s'", sysDateString);

        logger.debug("SQL which should delete audit logs: " + sql);

        SQLExec.query(sql).execute(objectContext);
    }
}
