/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import org.apache.cayenne.ObjectContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * User: akoiro
 * Date: 28/8/17
 */
public class TestCase {
	private TestEnv<TransportConfig> testEnv;
	private BiConsumer<GenericTransactionGroup, GenericParametersMap> fillStubs;
	private Function<GenericTransactionGroup, String> getSessionId;
	private Consumer<ObjectContext> assertQueuedRecords;
	private Consumer<GenericTransactionGroup> assertResponse;
	private Consumer<String> processPayment;

	public TestCase(TestEnv<TransportConfig> testEnv, BiConsumer<GenericTransactionGroup, GenericParametersMap> fillStubs,
					Function<GenericTransactionGroup, String> getSessionId,
					Consumer<String> processPayment,
					Consumer<ObjectContext> assertQueuedRecords,
					Consumer<GenericTransactionGroup> assertResponse) {
		super();
		this.testEnv = testEnv;
		this.fillStubs = fillStubs;
		this.getSessionId = getSessionId;
		this.assertQueuedRecords = assertQueuedRecords;
		this.assertResponse = assertResponse;
		this.processPayment = processPayment;
	}

	public void test() {
		//check that empty queuedRecords
		ObjectContext context = testEnv.getCayenneService().newNonReplicatingContext();
		testEnv.checkQueueBeforeProcessing(context);
		testEnv.authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(testEnv.getSupportedVersion());
		GenericParametersMap parametersMap = PortHelper.createParametersMap(testEnv.getSupportedVersion());
		fillStubs.accept(transaction, parametersMap);
		//process payment
		transaction = testEnv.processPayment(transaction, parametersMap);
		//check the response, validate the data and receive the sessionid
		String sessionId = getSessionId.apply(transaction);
		testEnv.checkQueueAfterProcessing(context);
		//check the status via service
		testEnv.checkNotProcessedResponse(testEnv.getTransportConfig().getPaymentStatus(sessionId));
		//call page processing
		processPayment.accept(sessionId);
		//check that async replication works correct
		assertQueuedRecords.accept(context);
		//check the status via service when processing complete
		assertResponse.accept(testEnv.getTransportConfig().getPaymentStatus(sessionId));

	}
}
