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
package ish.oncourse.server.print;

import com.google.inject.Inject;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import ish.math.context.MoneyContext;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Report;
import ish.oncourse.server.document.DocumentService;
import ish.oncourse.server.messaging.DocumentParam;
import ish.oncourse.server.preference.UserPreferenceService;
import ish.oncourse.server.scripting.api.ReportSpec;
import ish.print.PrintRequest;
import ish.print.PrintResult;
import ish.print.PrintResult.ResultType;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.rmi.server.UID;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

import static ish.print.PrintRequestTransformationsFiller.fillWithTransformations;

/**
 * Service for printing PDF reports.
 *
 */
public class PrintService {

	private static final Logger logger = LogManager.getLogger();

	private static final int MAX_THREADS = 10;
	private static final long WORKER_TTL = 60 * 60 * 1000; // 1 hour

	private ICayenneService cayenneService;
	private DocumentService documentService;
	private UserPreferenceService userPreferenceService;
	private MoneyContext moneyContext;

	private final Map<UID, PrintWorker> workerMap;

	private ExecutorService workerThreadExecutor;
	private ScheduledExecutorService cleanupThreadExecutor;

	@Inject
	public PrintService(ICayenneService cayenneService, DocumentService documentService, UserPreferenceService userPreferenceService, MoneyContext moneyContext) {
		this.cayenneService = cayenneService;
		this.documentService = documentService;
		this.userPreferenceService = userPreferenceService;
		this.moneyContext = moneyContext;

		workerThreadExecutor = Executors.newFixedThreadPool(MAX_THREADS);
		cleanupThreadExecutor = Executors.newScheduledThreadPool(1);

		// cleanup thread scheduled every hour
		cleanupThreadExecutor.scheduleAtFixedRate(this::cleanOldWorkers, 1, 1, TimeUnit.HOURS);

		this.workerMap = new ConcurrentHashMap<>();
	}

	/**
	 * Queue print request.
	 *
	 * @param printRequest
	 * @return if request successfully queued
	 */
	public synchronized Future<PrintResult> print(PrintRequest printRequest) {
		if (!workerMap.containsKey(printRequest.getUID())) {
			var worker = new PrintWorker(printRequest, cayenneService, documentService, userPreferenceService, moneyContext);
			workerMap.put(printRequest.getUID(), worker);

			return workerThreadExecutor.submit(() -> {
				worker.run();
				return worker.getResult();
			});
		}
		return null;
	}

	/**
	 * Query print worker with specific UID for print result containing either progress, pdf or error.
	 *
	 * @param requestUid
	 * @return print result
	 */
	public synchronized PrintResult getPrintResult(UID requestUid) {
		if (workerMap.containsKey(requestUid)) {
			var worker = workerMap.get(requestUid);
			var result = worker.getResult();

			if (ResultType.RESULTS_FINISHED.contains(result.getResultType())) {
				workerMap.remove(worker.getUID());
			}

			return result;
		}

		var result = new PrintResult(ResultType.FAILED);
		var error = String.format("Worker job for given UID (%s) doesn't exist.", requestUid);
		result.setError(error);
		logger.error(error);
		return result;
	}

	public Object report(@DelegatesTo(ReportSpec.class) Closure cl) throws IOException {
		ReportSpec reportSpec = new ReportSpec();
		var build = cl.rehydrate(reportSpec, cl, this);
		build.setResolveStrategy(Closure.DELEGATE_FIRST);
		build.call();

		if (reportSpec.getFileName() == null) {
			return report(reportSpec);
		}
		return DocumentParam.valueOf(reportSpec.getFileName(), report(reportSpec));
	}

	public byte[] report(ReportSpec reportSpec) {
		var request = new PrintRequest();
		request.setRecords(reportSpec.getEntityRecords());
		request.setReportCode(reportSpec.getKeyCode());
		request.setBackground(reportSpec.getBackground());
		request.addParameters(reportSpec.getParam());
		request.setCreatePreview(reportSpec.getGeneratePreview());
		if (reportSpec.getEntityRecords() == null || reportSpec.getEntityRecords().isEmpty()) {
			throw new IllegalArgumentException("No records specified.");
		}

		Report report;
		try {
			report = ObjectSelect.query(Report.class).where(Report.KEY_CODE.eq(request.getReportCode())).selectOne(cayenneService.getNewContext());
		} catch (CayenneRuntimeException e) {
			throw new IllegalArgumentException("No report with such key code exist: " + request.getReportCode());
		}

		if(report == null)
			throw new IllegalArgumentException("No report with such key code exist: " + request.getReportCode());

		fillWithTransformations(request, reportSpec.getEntityRecords(), report.getEntity(), request.getReportCode());

		try {
			PrintResult result = print(request).get();

			if (ResultType.FAILED.equals(result.getResultType())) {
				return result.getError() != null ? result.getError().getBytes() : null;
			}

			return result.getResult();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Removes references to all outdated workers so they can be GC'ed.
	 */
	private void cleanOldWorkers() {
		synchronized (workerMap) {
			var currentTimestamp = new Date().getTime();
			for (var uid : workerMap.keySet()) {
				if (currentTimestamp - workerMap.get(uid).getFinishTimestamp() > WORKER_TTL) {
					workerMap.remove(uid);
				}
			}
		}
	}

}
