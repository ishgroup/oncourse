package ish.oncourse.webservices.jobs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Invokable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.utils.AbandonStackedPaymentInvokable;

@RunWith(MockitoJUnitRunner.class)
public class TimeoutExpireQEPaymentTest{
	private static final Logger LOGGER = Logger.getLogger(TimeoutExpireQEPaymentTest.class);
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
		when(payment.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
		final Date started = new Date();
		AbandonStackedPaymentInvokable abandonInvokable = new AbandonStackedPaymentInvokable(started.getTime(), payment) {
			@Override
			public long getTimeout() {
				return SLEEP_TIME;//3 second
			}
		};
		Future<Boolean> result = executorService.submit(toCallable(abandonInvokable));
		assertTrue("Thread should be executed", result.get());
		assertTrue("AbandonStackedPaymentInvokable should be processed but not canceled", abandonInvokable.isProcessed() 
			&& !abandonInvokable.isCanceled());
		//check that we call abandonPayment
		verify(payment).abandonPayment();
		//check that we not call abandonPaymentKeepInvoice
		verify(payment, never()).abandonPaymentKeepInvoice();
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableCanceled() throws InterruptedException, ExecutionException {
		when(payment.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
		final Date started = new Date();
		AbandonStackedPaymentInvokable abandonInvokable = new AbandonStackedPaymentInvokable(started.getTime(), payment) {
			@Override
			public long getTimeout() {
				return SLEEP_TIME;//3 second
			}
		};
		Future<Boolean> result = executorService.submit(toCallable(abandonInvokable));
		abandonInvokable.setCanceled(true);
		assertTrue("Thread should be executed", result.get());
		assertTrue("AbandonStackedPaymentInvokable should be processed because canceled", abandonInvokable.isProcessed() 
			&& abandonInvokable.isCanceled());
		//check that we not call abandonPayment
		verify(payment, never()).abandonPayment();
		//check that we not call abandonPaymentKeepInvoice
		verify(payment, never()).abandonPaymentKeepInvoice();
		//check that we have no interactions with the payment object because cancel the process
		verifyZeroInteractions(payment);
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableOperateWithAlreadyFailedPaymentIn() throws InterruptedException, ExecutionException {
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);
		final Date started = new Date();
		AbandonStackedPaymentInvokable abandonInvokable = new AbandonStackedPaymentInvokable(started.getTime(), payment) {
			@Override
			public long getTimeout() {
				return SLEEP_TIME;//3 second
			}
		};
		Future<Boolean> result = executorService.submit(toCallable(abandonInvokable));
		assertTrue("Thread should be executed", result.get());
		assertTrue("AbandonStackedPaymentInvokable should be processed but not canceled", abandonInvokable.isProcessed() 
			&& !abandonInvokable.isCanceled());
		//check that we not call abandonPayment
		verify(payment, never()).abandonPayment();
		//check that we not call abandonPaymentKeepInvoice
		verify(payment, never()).abandonPaymentKeepInvoice();
		//check that we read the status twice (first for check that payment not success, second to check that payment is failed)
		verify(payment, times(2)).getStatus();
		//check that we read the paymentInLines once to detect that failed payment have no linked success or failed enrollments
		verify(payment).getPaymentInLines();
		//check that this was all the interactions with the paymentin object
		verifyNoMoreInteractions(payment);
	}
	
	@Test
	public void testAbandonStackedPaymentInvokableOperateWithAlreadySuccessPaymentIn() throws InterruptedException, ExecutionException {
		when(payment.getStatus()).thenReturn(PaymentStatus.SUCCESS);
		final Date started = new Date();
		AbandonStackedPaymentInvokable abandonInvokable = new AbandonStackedPaymentInvokable(started.getTime(), payment) {
			@Override
			public long getTimeout() {
				return SLEEP_TIME;//3 second
			}
		};
		Future<Boolean> result = executorService.submit(toCallable(abandonInvokable));
		assertTrue("Thread should be executed", result.get());
		assertTrue("AbandonStackedPaymentInvokable should be processed but not canceled", abandonInvokable.isProcessed() 
			&& !abandonInvokable.isCanceled());
		//check that we not call abandonPayment
		verify(payment, never()).abandonPayment();
		//check that we not call abandonPaymentKeepInvoice
		verify(payment, never()).abandonPaymentKeepInvoice();
		//check that we read the status once (to check that payment is success)
		verify(payment).getStatus();
		//check that this was all the interactions with the paymentin object
		verifyNoMoreInteractions(payment);
	}
	

	@Test
	public void testAbandonStackedPaymentInvokableOperateWithAlreadyFailedPaymentInWhichHaveSuccessEnrolments() throws InterruptedException, ExecutionException {
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);
		PaymentInLine paymentInLine = mock(PaymentInLine.class);
		Invoice invoice = mock(Invoice.class);
		InvoiceLine invoiceLine = mock(InvoiceLine.class);
		Enrolment enrollment = mock(Enrolment.class);
		when(payment.getPaymentInLines()).thenReturn(Arrays.asList(paymentInLine));
		when(paymentInLine.getInvoice()).thenReturn(invoice);
		when(invoice.getInvoiceLines()).thenReturn(Arrays.asList(invoiceLine));
		when(invoiceLine.getEnrolment()).thenReturn(enrollment);
		when(enrollment.getStatus()).thenReturn(EnrolmentStatus.SUCCESS);
		final Date started = new Date();
		AbandonStackedPaymentInvokable abandonInvokable = new AbandonStackedPaymentInvokable(started.getTime(), payment) {
			@Override
			public long getTimeout() {
				return SLEEP_TIME;//3 second
			}
		};
		Future<Boolean> result = executorService.submit(toCallable(abandonInvokable));
		assertTrue("Thread should be executed", result.get());
		assertTrue("AbandonStackedPaymentInvokable should be processed but not canceled", abandonInvokable.isProcessed() 
			&& !abandonInvokable.isCanceled());
		//check that we not call abandonPayment
		verify(payment, never()).abandonPayment();
		//check that we not call abandonPaymentKeepInvoice
		verify(payment, never()).abandonPaymentKeepInvoice();
	}
	
	private static <T> Callable<T> toCallable(final Invokable<T> invocable){
        return new Callable<T>() {
            public T call() throws Exception {
                try {
                    return invocable.invoke();
                }
                finally {
                	LOGGER.info("Invokable invokation finished.");
                }
            }
        };
    }
}
