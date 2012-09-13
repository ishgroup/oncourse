package ish.oncourse.enrol.services;

import ish.oncourse.enrol.services.concessions.ConcessionsService;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.services.student.StudentService;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule({ ModelModule.class, ServiceModule.class })
public class EnrolTestModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(IConcessionsService.class, ConcessionsService.class);
		binder.bind(IStudentService.class, StudentService.class);
		binder.bind(IInvoiceProcessingService.class, InvoiceProcessingService.class);
	}

}
