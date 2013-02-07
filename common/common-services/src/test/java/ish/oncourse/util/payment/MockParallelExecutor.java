package ish.oncourse.util.payment;

import static org.junit.Assert.assertFalse;

import java.util.concurrent.Future;

import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

public class MockParallelExecutor implements ParallelExecutor {
	private PaymentProcessController paymentProcessController;
	
	MockParallelExecutor() {
		this(null);
	}

	MockParallelExecutor(PaymentProcessController paymentProcessController) {
		this.paymentProcessController = paymentProcessController;
	}
	
	@Override
	public <T> T invoke(Class<T> proxyType, Invokable<T> invocable) {
		return null;
	}
	
	boolean isProcessFinished() {
		return paymentProcessController != null && paymentProcessController.isProcessFinished();
	}

	@Override
	public <T> Future<T> invoke(Invokable<T> invocable) {
		if (invocable instanceof ProcessPaymentInvokable) {
			invocable.invoke();
		}
		if (invocable instanceof StackedPaymentMonitor) {
			assertFalse("We should fire and re-fire the watchdog to abandon the payments only when the processing not finished", isProcessFinished());
		}
		return null;
	}
}
