package ish.oncourse.webservices.jobs;

import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.Invokable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.concurrent.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeoutExpireQEPaymentTest{
	private static final Logger logger = LogManager.getLogger();
	private ThreadPoolExecutor executorService;
	
	@Mock
	private PaymentIn payment;
	
	@Mock
	private ObjectContext objectContext;
	
	@Mock
	private ICayenneService cayenneService;
	
	/**
	 * The college for payment.
	 */
	private static College college;
	
	/**
	 * Initializes parameters for the whole test.
	 */
	@BeforeClass
	public static void init() { 
		college = new College();
	}

	/**
	 * Performs common operations for every method.
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void initMethod() throws Exception {
		when(payment.getCollege()).thenReturn(college);
		when(payment.getPaymentInLines()).thenReturn(Collections.EMPTY_LIST);
		when(payment.getObjectContext()).thenReturn(objectContext);
		when(cayenneService.newNonReplicatingContext()).thenReturn(objectContext);
		this.executorService = new ThreadPoolExecutor(3, 5, 2l,
                TimeUnit.SECONDS, new LinkedBlockingQueue());
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableExecutedByTimeout() throws InterruptedException, ExecutionException {
//		when(payment.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
//		final Date started = new Date();
//		StackedPaymentMonitor monitor = new StackedPaymentMonitor(started.getTime(), payment) {
//			@Override
//			public long getTimeout() {
//				return SLEEP_TIME;//3 second
//			}
//		};
//		Future<Boolean> result = executorService.submit(toCallable(monitor));
//		assertTrue("Thread should be executed", result.get());
//		assertTrue("StackedPaymentMonitor should be processed but not canceled", monitor.isProcessed()
//			&& !monitor.isCanceled());
//		//check that we call abandonPayment
//		verify(payment).abandonPayment();
//		//check that we not call abandonPaymentKeepInvoice
//		verify(payment, never()).abandonPaymentKeepInvoice();
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableCanceled() throws InterruptedException, ExecutionException {
//		when(payment.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
//		final Date started = new Date();
//		StackedPaymentMonitor monitor = new StackedPaymentMonitor(started.getTime(), payment) {
//			@Override
//			public long getTimeout() {
//				return SLEEP_TIME;//3 second
//			}
//		};
//		Future<Boolean> result = executorService.submit(toCallable(monitor));
//		monitor.setCanceled(true);
//		assertTrue("Thread should be executed", result.get());
//		assertTrue("StackedPaymentMonitor should be processed because canceled", monitor.isProcessed()
//			&& monitor.isCanceled());
//		//check that we not call abandonPayment
//		verify(payment, never()).abandonPayment();
//		//check that we not call abandonPaymentKeepInvoice
//		verify(payment, never()).abandonPaymentKeepInvoice();
//		//check that we have no interactions with the payment object because cancel the process
//		verifyZeroInteractions(payment);
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableOperateWithAlreadyFailedPaymentIn() throws InterruptedException, ExecutionException {
//		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);
//		final Date started = new Date();
//		StackedPaymentMonitor monitor = new StackedPaymentMonitor(started.getTime(), payment) {
//			@Override
//			public long getTimeout() {
//				return SLEEP_TIME;//3 second
//			}
//		};
//		Future<Boolean> result = executorService.submit(toCallable(monitor));
//		assertTrue("Thread should be executed", result.get());
//		assertTrue("StackedPaymentMonitor should be processed but not canceled", monitor.isProcessed()
//			&& !monitor.isCanceled());
//		//check that we not call abandonPayment
//		verify(payment, never()).abandonPayment();
//		//check that we not call abandonPaymentKeepInvoice
//		verify(payment, never()).abandonPaymentKeepInvoice();
//		//check that we read the status twice (first for check that payment not success, second to check that payment is failed)
//		verify(payment, times(2)).getStatus();
//		//check that we read the paymentInLines once to detect that failed payment have no linked success or failed enrollments
//		verify(payment).getPaymentInLines();
//		//check that this was all the interactions with the paymentin object
//		verifyNoMoreInteractions(payment);
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableOperateWithAlreadySuccessPaymentIn() throws InterruptedException, ExecutionException {
//		when(payment.getStatus()).thenReturn(PaymentStatus.SUCCESS);
//		final Date started = new Date();
//		StackedPaymentMonitor monitor = new StackedPaymentMonitor(started.getTime(), payment) {
//			@Override
//			public long getTimeout() {
//				return SLEEP_TIME;//3 second
//			}
//		};
//		Future<Boolean> result = executorService.submit(toCallable(monitor));
//		assertTrue("Thread should be executed", result.get());
//		assertTrue("StackedPaymentMonitor should be processed but not canceled", monitor.isProcessed()
//			&& !monitor.isCanceled());
//		//check that we not call abandonPayment
//		verify(payment, never()).abandonPayment();
//		//check that we not call abandonPaymentKeepInvoice
//		verify(payment, never()).abandonPaymentKeepInvoice();
//		//check that we read the status once (to check that payment is success)
//		verify(payment).getStatus();
//		//check that this was all the interactions with the paymentin object
//		verifyNoMoreInteractions(payment);
	}
	

	@Test
	public void testAbandonStackedPaymentInvokableOperateWithAlreadyFailedPaymentInWhichHaveSuccessEnrolments() throws InterruptedException, ExecutionException {
//		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);
//		PaymentInLine paymentInLine = mock(PaymentInLine.class);
//		Invoice invoice = mock(Invoice.class);
//		InvoiceLine invoiceLine = mock(InvoiceLine.class);
//		Enrolment enrollment = mock(Enrolment.class);
//		when(payment.getPaymentInLines()).thenReturn(Arrays.asList(paymentInLine));
//		when(paymentInLine.getInvoice()).thenReturn(invoice);
//		when(invoice.getInvoiceLines()).thenReturn(Arrays.asList(invoiceLine));
//		when(invoiceLine.getEnrolment()).thenReturn(enrollment);
//		when(enrollment.getStatus()).thenReturn(EnrolmentStatus.SUCCESS);
//		final Date started = new Date();
//		StackedPaymentMonitor monitor = new StackedPaymentMonitor(started.getTime(), payment) {
//			@Override
//			public long getTimeout() {
//				return SLEEP_TIME;//3 second
//			}
//		};
//		Future<Boolean> result = executorService.submit(toCallable(monitor));
//		assertTrue("Thread should be executed", result.get());
//		assertTrue("StackedPaymentMonitor should be processed but not canceled", monitor.isProcessed()
//			&& !monitor.isCanceled());
//		//check that we not call abandonPayment
//		verify(payment, never()).abandonPayment();
//		//check that we not call abandonPaymentKeepInvoice
//		verify(payment, never()).abandonPaymentKeepInvoice();
	}
	
	private static <T> Callable<T> toCallable(final Invokable<T> invocable){
        return new Callable<T>() {
            public T call() throws Exception {
                try {
                    return invocable.invoke();
                }
                finally {
                	logger.info("Invokable invokation finished.");
                }
            }
        };
    }
}
