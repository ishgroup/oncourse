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
package ish.oncourse.server.modules;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import ish.oncourse.GoogleGuiceInjector;
import ish.oncourse.aql.AqlService;
import ish.oncourse.aql.impl.AntlrAqlService;
import ish.oncourse.entity.services.CertificateService;
import ish.oncourse.entity.services.ContactService;
import ish.oncourse.entity.services.CourseClassService;
import ish.oncourse.entity.services.CourseService;
import ish.oncourse.entity.services.EnrolmentService;
import ish.oncourse.entity.services.InvoiceLineService;
import ish.oncourse.entity.services.OutcomeService;
import ish.oncourse.entity.services.SessionService;
import ish.oncourse.entity.services.StudentConcessionService;
import ish.oncourse.entity.services.StudentService;
import ish.oncourse.entity.services.TagService;
import ish.oncourse.server.AuditListener;
import ish.oncourse.server.CayenneListenersService;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.db.SanityCheckService;
import ish.oncourse.server.db.TransactionCheckService;
import ish.oncourse.server.deduplication.ContactMergeService;
import ish.oncourse.server.document.DocumentService;
import ish.oncourse.server.entity.mixins.MixinHelper;
import ish.oncourse.server.lifecycle.InvoiceLineInitHelper;
import ish.oncourse.server.messaging.EmailDequeueJob;
import ish.oncourse.server.payroll.PayrollService;
import ish.oncourse.server.print.PrintService;
import ish.oncourse.server.quality.QualityService;
import ish.oncourse.server.services.*;
import ish.oncourse.server.report.IReportService;
import ish.oncourse.server.report.ReportService;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.users.SystemUserService;

/**
 */
public class ServiceModule implements Module {

	public ServiceModule() {
	}

	/**
	 * @see com.google.inject.Module#configure(Binder)
	 */
	@Override
	public void configure(Binder binder) {
		binder.bind(SanityCheckService.class).in(Scopes.SINGLETON);
		binder.bind(TransactionCheckService.class).in(Scopes.SINGLETON);
		binder.bind(IReportService.class).to(ReportService.class).in(Scopes.SINGLETON);
		binder.bind(PrintService.class).in(Scopes.SINGLETON);


		// jobs
		binder.bind(EmailDequeueJob.class);
		binder.bind(BackupJob.class);
		binder.bind(StatsService.class).in(Scopes.SINGLETON);
		
		binder.bind(AccountTransactionService.class).in(Scopes.SINGLETON);
		binder.bind(AuditService.class).in(Scopes.SINGLETON);
		binder.bind(AuditListener.class).in(Scopes.SINGLETON);
		binder.bind(CayenneListenersService.class).asEagerSingleton();

		binder.bind(MixinHelper.class).asEagerSingleton();

		binder.bind(InvoiceLineInitHelper.class);

		binder.bind(ISchedulerService.class).to(SchedulerService.class);
		binder.bind(GroovyScriptService.class).in(Scopes.SINGLETON);
		binder.bind(QualityService.class).in(Scopes.SINGLETON);

		binder.bind(GoogleGuiceInjector.class).asEagerSingleton();

		// entity service classes
		binder.bind(ContactService.class).in(Scopes.SINGLETON);
		binder.bind(CourseClassService.class).in(Scopes.SINGLETON);
		binder.bind(StudentConcessionService.class).in(Scopes.SINGLETON);
		binder.bind(TagService.class).in(Scopes.SINGLETON);
		binder.bind(SessionService.class).in(Scopes.SINGLETON);
		binder.bind(OutcomeService.class).in(Scopes.SINGLETON);
		binder.bind(CourseService.class).in(Scopes.SINGLETON);
		binder.bind(CertificateService.class).in(Scopes.SINGLETON);
		binder.bind(StudentService.class).in(Scopes.SINGLETON);
		binder.bind(InvoiceLineService.class).in(Scopes.SINGLETON);
		binder.bind(EnrolmentService.class).in(Scopes.SINGLETON);
		binder.bind(DocumentService.class).in(Scopes.SINGLETON);
		binder.bind(IAutoIncrementService.class).to(ClusteredAutoincrementService.class).in(Scopes.SINGLETON);
		binder.bind(TransactionLockedService.class).in(Scopes.SINGLETON);
		binder.bind(CustomFieldTypeService.class).in(Scopes.SINGLETON);
		binder.bind(ISystemUserService.class).to(SystemUserService.class).in(Scopes.SINGLETON);
		binder.bind(AqlService.class).to(AntlrAqlService.class).in(Scopes.SINGLETON);
		binder.bind(PayrollService.class).in(Scopes.SINGLETON);
		binder.bind(ContactMergeService.class).in(Scopes.SINGLETON);
	}
}
