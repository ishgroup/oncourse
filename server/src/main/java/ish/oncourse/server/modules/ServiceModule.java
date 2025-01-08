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

import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import ish.oncourse.BootiqueInjector;
import ish.oncourse.aql.AqlService;
import ish.oncourse.aql.impl.AntlrAqlService;
import ish.oncourse.entity.services.*;
import ish.oncourse.server.AuditListener;
import ish.oncourse.server.CayenneListenersService;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.db.SanityCheckService;
import ish.oncourse.server.db.TransactionCheckService;
import ish.oncourse.server.deduplication.ContactMergeService;
import ish.oncourse.server.entity.mixins.MixinHelper;
import ish.oncourse.server.lifecycle.InvoiceLineInitHelper;
import ish.oncourse.server.messaging.EmailDequeueJob;
import ish.oncourse.server.payroll.PayrollService;
import ish.oncourse.server.print.PrintService;
import ish.oncourse.server.quality.QualityService;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.services.*;
import ish.oncourse.server.services.chargebee.ChargebeeUploadJob;
import ish.oncourse.server.users.SystemUserService;

/**
 */
public class ServiceModule implements BQModule {

	public ServiceModule() {
	}

	/**
	 * @see io.bootique.di.BQModule#configure(Binder)
	 */
	@Override
	public void configure(Binder binder) {
		binder.bind(SanityCheckService.class).inSingletonScope();
		binder.bind(TransactionCheckService.class).inSingletonScope();
		binder.bind(PrintService.class).inSingletonScope();

		// jobs
		binder.bind(EmailDequeueJob.class);
		binder.bind(ChargebeeUploadJob.class);
		binder.bind(StatsService.class).inSingletonScope();

		binder.bind(AccountTransactionService.class).inSingletonScope();
		binder.bind(AuditService.class).inSingletonScope();
		binder.bind(AuditListener.class).inSingletonScope();
		binder.bind(CayenneListenersService.class).initOnStartup();

		binder.bind(MixinHelper.class).initOnStartup();

		binder.bind(InvoiceLineInitHelper.class);

		binder.bind(ISchedulerService.class).to(SchedulerService.class);
//		binder.bind(GroovyScriptService.class).toProvider(GroovyScriptServiceProvider.class).inSingletonScope();
		binder.bind(GroovyScriptService.class).inSingletonScope();
		binder.bind(QualityService.class).inSingletonScope();

		binder.bind(BootiqueInjector.class).initOnStartup();

		// entity service classes
		binder.bind(CourseClassService.class).inSingletonScope();
		binder.bind(StudentConcessionService.class).inSingletonScope();
		binder.bind(TagService.class).inSingletonScope();
		binder.bind(SessionService.class).inSingletonScope();
		binder.bind(CourseService.class).inSingletonScope();
		binder.bind(CertificateService.class).inSingletonScope();
		binder.bind(StudentService.class).inSingletonScope();
		binder.bind(InvoiceLineService.class).inSingletonScope();
		binder.bind(EnrolmentService.class).inSingletonScope();
		binder.bind(IAutoIncrementService.class).to(ClusteredAutoincrementService.class).inSingletonScope();
		binder.bind(TransactionLockedService.class).inSingletonScope();
		binder.bind(CustomFieldTypeService.class).inSingletonScope();
		binder.bind(ISystemUserService.class).to(SystemUserService.class).inSingletonScope();
		binder.bind(AqlService.class).to(AntlrAqlService.class).inSingletonScope();
		binder.bind(PayrollService.class).inSingletonScope();
		binder.bind(ContactMergeService.class).inSingletonScope();
	}
}
