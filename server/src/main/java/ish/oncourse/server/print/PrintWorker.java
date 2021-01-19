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

import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.common.ResourcesUtil;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Document;
import ish.oncourse.server.cayenne.Report;
import ish.oncourse.server.cayenne.ReportOverlay;
import ish.oncourse.server.document.DocumentService;
import ish.oncourse.server.report.PdfUtil;
import ish.persistence.CommonPreferenceController;
import ish.persistence.Preferences;
import ish.print.PageBreakType;
import ish.print.PrintRequest;
import ish.print.PrintResult;
import ish.print.PrintResult.ResultType;
import ish.print.transformations.PrintTransformation;
import ish.s3.AmazonS3Service;
import ish.util.EntityUtil;
import ish.util.MapsUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.server.UID;
import java.util.*;

import static ish.util.ImageHelper.generatePdfPreview;

/**
 * Worker which is serving specific {@link PrintRequest} identified by unique id.
 *
 */
public class PrintWorker implements Runnable {

	private static final Logger logger = LogManager.getLogger();

	private UID uid;
	private ICayenneService cayenneService;
	private DocumentService documentService;
	private PrintRequest printRequest;

	private Map<String, JasperReport> compiledReports = new LinkedHashMap<>();
	private Map<String, byte[]> images = new LinkedHashMap<>();
	private Map<String, Object> reportPreferences = new LinkedHashMap<>();

	private static final String REPORT_IMAGES_RELATIVE_LOCATION = "reports/Images/";

	private double progress;
	private Long finishTimestamp;
	private byte[] pdfResult;
	private ResultType result;
	private String errorMessage;

	private String reportName;

	public PrintWorker(PrintRequest printRequest, ICayenneService cayenneService, DocumentService documentService) {
		this.uid = printRequest.getUID();
		this.printRequest = printRequest;
		this.cayenneService = cayenneService;
		this.documentService = documentService;

		progress = 0d;
		result = ResultType.IN_PROGRESS;
	}

	/**
	 * Returns worker's unique id.
	 *
	 * @return uuid
	 */
	public UID getUID() {
		return uid;
	}

	/**
	 * Returns timestamp when worker finished execution with success or error.
	 *
	 * @return finished timestamp
	 */
	public Long getFinishTimestamp() {
		return finishTimestamp;
	}

	/**
	 * Returns print result filled with progress when request is still executing or with pdf result or error if it's finished.
	 *
	 * @return progress or pdf or error
	 */
	public PrintResult getResult() {
		PrintResult printResult;

		printResult = new PrintResult(result);
		printResult.setProgress((int) progress);
		printResult.setReportName(reportName);
		printResult.setResult(pdfResult);

		if (errorMessage != null) {
			printResult.setError(errorMessage);
		}

		return printResult;
	}

	/**
	 * Launches print job.
	 *
	 * @see Runnable#run()
	 */
	@Override
	public void run() {
		Report startingReport = null;
		try {
			logger.warn("begin to print: {}", printRequest);
			//get report and overlay to print
			startingReport = reportForKeyCode(printRequest.getReportCode());
			reportName = startingReport.getName();
			ReportOverlay overlay = null;

			if (StringUtils.trimToNull(printRequest.getBackground()) != null) {
				overlay = ObjectSelect.query(ReportOverlay.class).where(ReportOverlay.NAME.eq(printRequest.getBackground())).selectFirst(cayenneService.getSharedContext());
			}

			if (overlay == null) {
				overlay = startingReport.getBackground();
			}

			progress = 5d;

			// assemble list of reports to print (see Certificate report, where 'followingReports' value is set)
			List<Report> reports = new ArrayList<>();
			reports.add(startingReport);
			reports.addAll(getFollowingReports(startingReport));
			logger.info("reports to print: {}", reports.size());
			// compile reports
			for (var r : reports) {
				getCompiledReport(r);
			}
			progress = 10d;

			//get source list of records to print
			var step = 80d / Math.max(1, getNumberOfValues(printRequest.getIds())) / reports.size();

			//check if the report is to be run for each record separatelly (detail reports) or alltogether (list reports)
			var pageBreakType = startingReport.getPageBreakType();
			logger.info("report: {} 1 record per page? {}", startingReport.getName(), pageBreakType);

			List<byte[]> pdfs = new ArrayList<>();
			for (var entry : printRequest.getIds().entrySet()) {
				var sourceEntity = entry.getKey();
				var printTransformation = printRequest.getPrintTransformation(sourceEntity);
				logger.info("for {} using transformation: {}", sourceEntity, printTransformation);
				if (!PageBreakType.OFF.equals(pageBreakType) || reports.size() > 1) {

					if (PageBreakType.SOURCE.equals(pageBreakType)) {

						for (var sourceId : entry.getValue()) {

							// apply path transform, filter and sort
							var printables = transformRecords(Collections.singletonList(sourceId), printTransformation, startingReport.getSortOn());

							// work with once source record at a time
							var filledReports = fillReports(reports, printables);

							//export to pdf
							var temp = exportToPdf(filledReports, overlay, startingReport.getName());
							if (temp != null && temp.length > 0) {
								pdfs.add(temp);
							}
						}

					} else {

						// apply path transform, filter and sort
						var printables = transformRecords(entry.getValue(), printTransformation, startingReport.getSortOn());

						// work with one transformed record at a time
						for (var printable : printables) {

							//fill report(s)
							var filledReports = fillReports(reports, Collections.singletonList(printable));

							//export to pdf
							var temp = exportToPdf(filledReports, overlay, startingReport.getName());
							if (temp != null && temp.length > 0) {
								pdfs.add(temp);
							}
						}
					}

					progress = progress + step;
				} else {
					// work with all source record ids at the time
					// apply path transform, filter and sort
					var printables = transformRecords(entry.getValue(), printTransformation, startingReport.getSortOn());
					progress = 33d;
					//fill report(s)
					var filledReports = fillReports(reports, printables);
					progress = 66d;
					//export to pdf
					var temp = exportToPdf(filledReports, overlay, startingReport.getName());
					if (temp != null && temp.length > 0) {
						pdfs.add(temp);
					}
					progress = progress + step;
				}
			}

			progress = 95d;
			var pdf = PdfUtil.mergePdfs(pdfs).toByteArray();
			progress = 99d;

			if (pdf == null || pdf.length == 0) {
				throw new JRRuntimeException("The produced report has no data. Please verify the selection of records and the report.");
			}

			logger.info("report: {} printed successfully", startingReport.getName());
			pdfResult = pdf;
			result = ResultType.SUCCESS;
		} catch (Exception e) {
			result = ResultType.FAILED;
			errorMessage = e.getMessage();
			logger.error("Printing failed.", e);
		}

		if (printRequest.isCreatePreview() && startingReport != null && pdfResult != null && pdfResult.length != 0 ) {
			ObjectContext cc = cayenneService.getNewContext();
			var localReport = cc.localObject(startingReport);
			localReport.setPreview(generatePdfPreview(pdfResult));
			cc.commitChanges();
		}

		progress = 100;

		finishTimestamp = new Date().getTime();
	}

	/**
	 * @param ids map of entityName-list of ids to fetch
	 * @return a list of records for printing.
	 */
	protected List<PersistentObjectI> getRecords(Map<String, List<Long>> ids) {
		ObjectContext context = cayenneService.getNewContext();

		List<PersistentObjectI> records = new ArrayList<>();

		for (var entry : ids.entrySet()) {
			var entityName = entry.getKey();
			if (entityName.indexOf('.') > 0) {
				entityName = entityName.substring(0, entityName.indexOf("."));
			}

			List<? extends PersistentObjectI> recordList = EntityUtil.getObjectsByIds(context,
					EntityUtil.entityClassForName(entityName), entry.getValue());

			records.addAll(recordList);
		}
		return records;
	}

	/**
	 * transforms records from the original (raw cayenne objects) for to the printable form.
	 *
	 * @param recordIds to transform
	 * @return tansformed, filtered, wrapped, sorted list of PrintableObjetcs, ready to print
	 */
	protected List<PersistentObjectI> transformRecords(List<Long> recordIds, PrintTransformation transform, String sortOn) throws Exception {
		try {
			logger.info("transforming records, at start: {}, transform: {}", recordIds.size(), transform);
			List<PersistentObjectI>  printList;

			if (transform != null) {

				printList = transform.applyTransformation(cayenneService.getNewContext(), recordIds, printRequest.getAdditionalParameters());
	
			} else {
				Map<String, List<Long>> tempMap = new HashMap<>();
				tempMap.put(printRequest.getEntity(), recordIds);
				printList = getRecords(tempMap);
				logger.info("fetching records : {}", printList.size());
			}

			//order
			if (!StringUtils.isEmpty(sortOn)) {
				var orderings = ReportDataSource.getOrderingForSortOn(sortOn);
				Ordering.orderList(printList, orderings);
			}

			return printList;
		} catch (Exception e) {
			logger.warn("failed to apply transform :{}", transform, e);

			// if report filling failed wrap exception into a new one, to provide a useful message to the user.
			throw e;
		}
	}

	/**
	 * Fills a given report with data.
	 *
	 * @param report         to fill
	 * @param recordsToPrint used for filling
	 * @return filled report
	 * @throws Exception if anythig went wrong while filling the reports
	 */
	protected JasperPrint fillReport(Report report, List<?> recordsToPrint) throws Exception {
		var reportDataSource = new ReportDataSource(this, report, recordsToPrint);

		var result = reportDataSource.getJPrint();
		if (result.getPages().size() == 0) {
			logger.info("Skipped {} it's output had no pages.", report.getName());
			return null;
		}
		return result;
	}

	/**
	 * fills a sequence of reports with the same data.
	 *
	 * @param reports    to fill
	 * @param printables used for filling
	 * @return list of filled reports
	 */
	protected List<JasperPrint> fillReports(List<Report> reports, List<PersistentObjectI> printables) throws Exception {
		logger.info("filling {} report(s) with {} records", reports.size(), printables.size());
		List<JasperPrint> filledReports = new ArrayList<>();
		for (var r : reports) {
			try {
				var filledReport = fillReport(r, printables);
				if (filledReport != null) {
					filledReports.add(filledReport);
				}
			} catch (Exception e) {
				logger.warn("failed to fill report: {}. It will be missing from the output", r.getName(), e);

				// if report filling failed wrap exception into a new one, to provide a useful message to the user.
				throw e;
			}
		}
		return filledReports;
	}

	/**
	 * exports the filled reports to pdf. Applies overlay.
	 *
	 * @param printJobs  to export
	 * @param overlay    to use as background
	 * @param reportName only used for error handling and logging.
	 * @return resulting pdf as byte[]
	 */
	public static byte[] exportToPdf(List<JasperPrint> printJobs, ReportOverlay overlay, String reportName) {
		logger.info("{} exporting to pdf {} print jobs.", reportName, printJobs.size());
		if (printJobs.size() == 0) {
			return null;
		}
		try {
			var exportOutput = new ByteArrayOutputStream();

			var exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, printJobs);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, exportOutput);
			exporter.setParameter(JRPdfExporterParameter.IS_COMPRESSED, Boolean.TRUE);


			exporter.exportReport();
			if (overlay != null) {
				exportOutput = PdfUtil.overlayPDFs(exportOutput, overlay.getOverlay());
				logger.info("report: {} overlayed with {}", reportName, overlay.getName());
			}

			return exportOutput.toByteArray();
		} catch (Exception e) {
			throw new JRRuntimeException("failed to export report " + reportName + " to pdf", e);
		}
	}

	/**
	 * assembles a list of reports, used together with 'followingReport' property
	 *
	 * @param startingReport
	 * @return ordered list of reports
	 * @throws JRException
	 */
	private List<Report> getFollowingReports(Report startingReport) throws JRException {
		List<Report> result = new ArrayList<>();

		var compiledReports = startingReport.getFollowingReports();
		String[] followingReports = null;

		if (compiledReports != null) {
			followingReports = compiledReports.split(";");
		}

		if (followingReports != null) {
			for (var followingReport : followingReports) {
				var expression = Report.KEY_CODE.eq(followingReport);
				var rep = cayenneService.getSharedContext().select(SelectQuery.query(Report.class, expression));
				if (rep.size() == 1) {
					result.add(rep.get(0));
				} else if (rep.size() == 0) {
					throw new JRRuntimeException("Cannot find a report with keycode " + followingReport);
				} else {
					throw new JRRuntimeException("Found a duplicate report with keycode " + followingReport);
				}
			}
		}

		return result;
	}

	/**
	 * @param keyCode
	 * @return a report identified with the param keycode
	 */
	public Report reportForKeyCode(String keyCode) {
		var query = SelectQuery.query(Report.class);
		query.andQualifier(Report.KEY_CODE.eq(keyCode));

		return cayenneService.getSharedContext().selectOne(query);
	}



	public ICayenneService getCayenneService() {
		return cayenneService;
	}

	/**
	 * @return print request associated with this print worker
	 */
	public PrintRequest getPrintRequest() {
		return printRequest;
	}

	/**
	 * Returns a compiles version of the report. Compiled copy is stored for this instance of PrintWorker so it can be reused (see subreports).
	 *
	 * @param report to be compiled.
	 * @return compiled report
	 * @throws JRException
	 */
	public JasperReport getCompiledReport(Report report) throws JRException {
		var compiledReport = compiledReports.get(report.getKeyCode());
		if (compiledReport == null) {
			// report not compiled yet, do it now
			compiledReport = compileReport(report);
			if (compiledReport == null) {
				throw new JRException("Report " + report.getKeyCode() + " could not be compiled.");
			}
			compiledReports.put(report.getKeyCode(), compiledReport);
		}
		return compiledReport;
	}

	/**
	 * Simple step to compile report. The jdt compiler does not store or read reportsfrom/to disk anymore, just keep severything in memory those days.
	 *
	 * @param report
	 * @return compiler report
	 * @throws JRException if something went wrong
	 */
	private JasperReport compileReport(Report report) throws JRException {
		var cStart = new Date();
		var result = JasperCompileManager.compileReport(new ByteArrayInputStream(report.getData()));
		logger.debug("Report {} took {}ms to compile", report.getKeyCode(), new Date().getTime() - cStart.getTime());
		return result;
	}


	/**
	 * Returns an image as InputStream. <br/>
	 * Image is attempted to load from relative location on disk, if that fails query of the Attachment table. <br/>
	 * image is stored for this instance of PrintWorker so it can be reused.
	 *
	 * @param keyCode identifing the image
	 * @return image as InputStream
	 */
	public ByteArrayInputStream getImage(String keyCode) {
		if (images.containsKey(keyCode)) {
			return new ByteArrayInputStream(images.get(keyCode));
		}

		logger.debug("locating image resource, keyCode .. = {}", keyCode);

		byte[] imageData = null;
		InputStream is = null;
		try {
			is = ResourcesUtil.getResourceAsInputStream(REPORT_IMAGES_RELATIVE_LOCATION + keyCode + ".png");
			if (is != null) {
				imageData = IOUtils.toByteArray(is);
			}
		} catch (IOException e) {
			logger.info("Image {} not found in the relevant resources folder", keyCode);
		} finally {
			IOUtils.closeQuietly(is);
		}


		if (imageData == null || imageData.length == 0) {
			// query attachments
			var biCandidates = ObjectSelect.query(Document.class)
					.where(Document.NAME.like(keyCode))
					.and(Document.IS_REMOVED.eq(false))
					.select(cayenneService.getSharedContext());

			if (biCandidates.size() > 0) {
				var document = biCandidates.get(0);
				if (document.getFileUUID() == null) {
					if (document.getCurrentVersion().getAttachmentData() == null) {
						logger.error("Can't find attachment with name '{}', it doesn't have neither S3 file UUID nor db BinaryData record.", document.getName());
					} else {
						imageData = document.getCurrentVersion().getAttachmentData().getContent();
					}
				} else if (documentService.isUsingExternalStorage()) {
					var s3Service = new AmazonS3Service(documentService);
					try {
						var stringUrl = s3Service.getFileUrl(document.getFileUUID(), document.getCurrentVersion().getVersionId(), document.getWebVisibility());
						var url = new URL(stringUrl);
						imageData = IOUtils.toByteArray(url.openStream());
					} catch (IOException e) {
						logger.catching(e);
					}
				}

			}
		}

		if (imageData != null) {
			images.put(keyCode, imageData);
			logger.debug("found image:{} length:{}", keyCode, imageData.length);
			return new ByteArrayInputStream(imageData);
		}
		logger.warn("Can't load image: {}", keyCode);
		return null;
	}

	/**
	 * Returns value of a Preference. Preferences are cached to save trips to the database.
	 *
	 * @param keyCode identifing the preference
	 * @return preference value
	 */
	public Object getPreferenceValue(String keyCode) {

		if (reportPreferences.containsKey(keyCode)) {
			return reportPreferences.get(keyCode);
		}

		var result = PreferenceController.getController().getValueForKey(keyCode);

		if (keyCode.equals(Preferences.AVETMISS_STATE)) {
			result = MapsUtil.getKeyForValue(result, CommonPreferenceController.AddressStates);
		}
		reportPreferences.put(keyCode, result);

		return result;
	}

	/**
	 * @param map of <String, List<Long>>
	 * @return sum of lenghts of values for all keys in the param map.
	 */
	private long getNumberOfValues(Map<String, List<Long>> map) {
		long result = 0;
		for (var entry : map.entrySet()) {
			result = result + entry.getValue().size();
		}
		return result;
	}
}
