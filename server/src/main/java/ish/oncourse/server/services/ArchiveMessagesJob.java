/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.messaging.ArchivingMessagesService;
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
import java.time.LocalDate;
import java.util.Date;

@DisallowConcurrentExecution
public class ArchiveMessagesJob implements Job {

    private static final Logger logger = LogManager.getLogger();
    private static final long DEFAULT_MESSAGE_EXPIRE_DAYS = 365 * 3;

    private final ArchivingMessagesService archiveService;
    private final PreferenceController preferenceController;

    @Inject
    ArchiveMessagesJob(ArchivingMessagesService archiveService, PreferenceController preferenceController) {
        this.archiveService = archiveService;
        this.preferenceController = preferenceController;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var daysExpires = preferenceController.getDaysMessageExpires();
        if(daysExpires == null)
            daysExpires = DEFAULT_MESSAGE_EXPIRE_DAYS;

        logger.info("Messages archiving for "+ daysExpires+" days before started");

        var dateArchived = LocalDate.now().minusDays(daysExpires);
        archiveService.archiveMessages(dateArchived);

        logger.info("Messages archiving successfully ended");

    }
}
