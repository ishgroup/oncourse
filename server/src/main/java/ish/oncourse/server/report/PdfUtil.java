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

package ish.oncourse.server.report;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.*;
import ish.util.ImageHelper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An utility to split/merge/overlay PDFs
 */
public final class PdfUtil {
	private static final Logger logger = LogManager.getLogger();

	private static File PDF_OPERATIONS_TEMP_FOLDER = new File(FileUtils.getTempDirectory(), "PDFOperations");

	private PdfUtil() {}

	private static File moveIntoOperationsFolder(File original) throws IOException {
		FileUtils.moveFileToDirectory(original, PDF_OPERATIONS_TEMP_FOLDER, true);
		var copy = new File(PDF_OPERATIONS_TEMP_FOLDER, original.getName());
		return copy;
	}

	/**
	 * Overlays pdf over background
	 *
	 * @param pdf The PDF to overlay, it will be replaced during the process with the overlayed document
	 * @param backgroundPdf  The PDF to be in the background. If pdf.numberOfPages() > backgroundPdf.numberOfPages(), the last page of backgroundPdf will be used for the remaining pages.
	 *
	 * Remotelly based on https://github.com/boldt/Okular2PDF
	 */
	public static void overlayPDFs(File pdf, File backgroundPdf) throws Exception {
		// if the pdf does not have to be overlayed, return;
		if (!checkPDFsOverlay(pdf, backgroundPdf)) {
			return;
		}
		logger.debug("Merging {} and {}", pdf.getAbsolutePath(), backgroundPdf.getAbsolutePath());


		var data = overlayPDFs(FileUtils.readFileToByteArray(pdf),FileUtils.readFileToByteArray(backgroundPdf));
		if (data != null) {
			pdf.delete();

			FileUtils.writeByteArrayToFile(pdf, data.toByteArray());
		}
	}

	/**
	 * Check if the pdfs can be overlayed. Three outcomes are possible: pdfs can be overlayed, there is no point of overlaying them or the operation is not possible at all.
	 *
	 * @param pdf The PDF to overlay
	 * @param backgroundPdf  The PDF to be in the background.
	 * @return true if the PDFs can be overlaid. false if there is no point overlaying the pdfs.
	 * @throws Exception if the pdf cannot be overlayed due to invalid geometry etc.
	 *
	 * Remotelly based on https://github.com/boldt/Okular2PDF
	 */
	private static boolean checkPDFsOverlay(File pdf, File backgroundPdf) throws Exception {
		logger.debug("Checking {} and {}", pdf, backgroundPdf);
		// check arguments
		if (pdf == null) {
			throw new Exception("No pdf file provided to overlay!");
		}

		if (backgroundPdf == null) {
			logger.warn("No backgroud specified, skipping");
			return false;
		}

		return checkPDFsOverlay(FileUtils.readFileToByteArray(pdf), FileUtils.readFileToByteArray(backgroundPdf));
	}


	/**
	 * Overlays pdf over background
	 *
	 * @param pdf The PDF to overlay, it will be replaced during the process with the overlayed document
	 * @param backgroundPdf  The PDF to be in the background. If pdf.numberOfPages() > backgroundPdf.numberOfPages(), the last page of backgroundPdf will be used for the remaining pages.
	 *
	 * Remotelly based on https://github.com/boldt/Okular2PDF
	 */
	public static ByteArrayOutputStream overlayPDFs(ByteArrayOutputStream pdf, byte[] backgroundPdf) throws Exception {
		return overlayPDFs(pdf.toByteArray(), backgroundPdf);
	}


	/**
	 * Overlays pdf over background
	 *
	 * @param pdf The PDF to overlay, it will be replaced during the process with the overlayed document
	 * @param background  The PDF to be in the background. If pdf.numberOfPages() > backgroundPdf.numberOfPages(), the last page of backgroundPdf will be used for the remaining pages.
	 *
	 * Remotelly based on https://github.com/boldt/Okular2PDF
	 */
	public static ByteArrayOutputStream overlayPDFs(byte[] pdf, byte[] background) throws Exception {

		// if the pdf does not have to be overlayed, return;
		try {
			if (!checkPDFsOverlay(pdf, background)) {
				return null;
			}
		} catch (Exception e) {
			try {
				background = ImageHelper.convertImageToPdf(background);
			} catch (IOException ex) {
				logger.error("cannot read background for report");
				logger.catching(e);
				return null;
			}
		}
		if (!checkPDFsOverlay(pdf, background)) {
			return null;
		}

		var result = new ByteArrayOutputStream();

		var foreground = new PdfReader(pdf);
		var backgroundPdf = new PdfReader(background);

		var numberOfPages = foreground.getNumberOfPages();

		// the output document, this will write to where the original document resided
		var writer = new PdfStamper(foreground, result);

		// iterate throught the pages
		for (var i = 1; i <= numberOfPages; i++) {
			addTemplate(writer, backgroundPdf, i);
		}

		writer.close();

		return result;
	}

	/**
	 * This method uses the Transformation Matrix to rotate page in PdfStamper.addTemplate method
	 * because (.addTemplate(pdfTemplate, v, v1)) method in PdfStamper IGNORES page rotation.
	 * But it has implementation with transformation matrix which rotates adding page.
	 * It counts parameters of this matrix and adds template right.
	 *
	 * [a b 0]
	 * [c d 0] - the Transformation Matrix
	 * [e f 1]
	 *
	 * where :
	 * a =  s1*X * cos(angle);
	 * b =  s2*Y * sin(angle);
	 * c = -s3*X * sin(angle);
	 * d =  s4*Y * cos(angle);
	 * e = t1*X;
	 * f = t2*Y;
	 *
	 * We can combine translation (t*X, t*Y), scaling (s*X, s*Y) rotation (angle) in one matrix
	 */
	private static void addTemplate(PdfStamper writer, PdfReader background, int pageNum) {
		// take background page
		var bgPageNum = Math.min(pageNum, background.getNumberOfPages());
		var pageUnderlay = writer.getImportedPage(background, bgPageNum);

		//add as 'under content' to the pdf
		var contentByte = writer.getUnderContent(pageNum);

		// use (360 - angle) to rotate clockwise because PdfReader rotates clockwise;
		var angleInDegrees =360 - background.getPageRotation(bgPageNum);
		var angleInRadians = Math.toRadians(angleInDegrees);

		//if PageRotation angle  = 0   : x = 0; y = 0;
		//of background:		 = 90  : x = 0; y = 1;
		//						 = 180 : x = 1; y = 1;
		//						 = 270 : x = 1; y = 0;
		var tX = background.getPageSizeWithRotation(bgPageNum).getWidth() * ((angleInDegrees<180)?1:0);
		var tY = background.getPageSizeWithRotation(bgPageNum).getHeight() * ((angleInDegrees%360<180)?0:1);

		contentByte.addTemplate(pageUnderlay,
				(float) Math.cos(angleInRadians),
				(float)Math.sin(angleInRadians),
				(float)-Math.sin(angleInRadians),
				(float)Math.cos(angleInRadians),
				tX,
				tY);
	}

	/**
	 * Check if the pdfs can be overlayed. Three outcomes are possible: pdfs can be overlayed, there is no point of overlaying them or the operation is not possible at all.
	 *
	 * @param pdf The PDF to overlay
	 * @param backgroundPdf  The PDF to be in the background.
	 * @return true if the PDFs can be overlaid. false if there is no point overlaying the pdfs.
	 * @throws Exception if the pdf cannot be overlayed due to invalid geometry etc.
	 *
	 * Remotelly based on https://github.com/boldt/Okular2PDF
	 */
	protected static boolean checkPDFsOverlay(byte[] pdf, byte[] backgroundPdf) throws Exception {

		// check arguments
		if (pdf == null) {
			throw new Exception("No pdf provided to overlay!");
		}

		if (backgroundPdf == null) {
			logger.warn("No backgroud specified, skipping");
			return false;
		}

		// create Pdf reader classes, check more params before merge
		var foreground = new PdfReader(pdf);
		var background = new PdfReader(backgroundPdf);

		// Check the amount of pages
		var foreground_num_pages = foreground.getNumberOfPages();
		var background_num_pages = background.getNumberOfPages();

		if (foreground_num_pages == 0 || background_num_pages == 0) {
			logger.warn("Provided documents with no pages, skipping");
			return false;
		}

		return true;
	}

	/**
	 * merges multiple pdf documents into one.
	 *
	 * @param inputs list of documents
	 * @return merged document
	 * @throws Exception
	 */
	public static ByteArrayOutputStream mergePdfs(List<byte[]> inputs) throws Exception {
		var result = new ByteArrayOutputStream();
		if (inputs == null || inputs.isEmpty()) {
			return result;
		}

		var document = new Document();
		var writer = new PdfCopy(document, result);
		document.open();
		for (var in : inputs) {
			var reader = new PdfReader(in);
			for (var i = 1; i <= reader.getNumberOfPages(); i++) {
				document.newPage();
				//import the page from source pdf
				var page = writer.getImportedPage(reader, i);
				//add the page to the destination pdf
				writer.addPage(page);
			}
		}
		result.flush();
		document.close();
		result.close();
		return result;
	}

}
