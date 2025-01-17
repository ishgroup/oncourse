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

package ish.util;

import ish.common.util.ImageHelper;
import ish.common.util.ImageRequest;
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO;
import ish.oncourse.server.preference.UserPreferenceService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static ish.common.util.ImageHelper.*;

/**
 *
 */

public class ExtendedImageHelper {

	public static final int DEFAULT_MAX_IMAGE_SCALE = 4;
	public static final int MAX_IMAGE_SCALE = 10;

	private static final Logger logger = LogManager.getLogger();


	public static byte[] convertImageToPdf(byte[] imageSrc) throws IOException {
		var image = getAsBufferedImage(imageSrc);
		if(image == null)
			return null;
		final PDDocument doc = new PDDocument();
		PDPageContentStream contentStream;

		int width = image.getWidth();
		int height = image.getHeight();

		width = width < height ? A4_PIXELS_WIDTH : A4_PIXELS_HEIGHT;
		height = image.getWidth() < height ? A4_PIXELS_HEIGHT : A4_PIXELS_WIDTH;

		var scaledImage = smoothScaleImageTo(width, height, image);
		imageSrc = getAsByteArray(scaledImage, PDF_PREVIEW_FORMAT);

		PDPage page = new PDPage(new PDRectangle(width, height));
		doc.addPage(page);

		PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, imageSrc, null);
		contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

		contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());

		IOUtils.closeQuietly(contentStream);

		var stream = new ByteArrayOutputStream();
		doc.save(stream);
		return stream.toByteArray();
	}

	public static byte[] scaleImageToPreviewSize(byte[] imgSrc) {
		try {
			if(imgSrc == null)
				return null;
			var image = getAsBufferedImage(imgSrc);
			if(image == null)
				return imgSrc;
			int width = image.getWidth() > image.getHeight() ? PDF_PREVIEW_HEIGHT : PDF_PREVIEW_WIDTH;
			int height = image.getWidth() > image.getHeight() ? PDF_PREVIEW_WIDTH : PDF_PREVIEW_HEIGHT;
			var scaledImage = scaleImageToSize(width, height, image, false);
			return imageAsPdfPreviewByteArray(scaledImage);
		} catch (IOException e) {
			logger.error("Preview of pdf report was not an image on formatting stage");
			return null;
		}
	}

	/**
	 * Generates 400x564 (A4 format demention) preview from pdf byte array.
	 *
	 * @param pdfContent - pdf byte array
	 * @return - binary content of generated preview, null - if transformation can't be performed
	 */
	public static byte[] generateHighQualityPdfPreview(byte[] pdfContent, int quality) {
		var request = new ImageRequest.Builder(pdfContent)
				.qualityScale(quality)
				.a4FormatRequired(true)
				.build();

		var result = generateQualityPreview(request);
		return result == null ? null : result.get(0);
	}

	public static int getBackgroundQualityScale(UserPreferenceService userPreferenceService){
		var highQualityScaleStr = userPreferenceService.get(PreferenceEnumDTO.BACKGROUND_QUALITY_SCALE);
		if(highQualityScaleStr != null){
			try {
				var highQualityScale = Integer.parseInt(highQualityScaleStr);
				if(highQualityScale <= 0)
					highQualityScale = 2;
				if(highQualityScale > MAX_IMAGE_SCALE)
					highQualityScale = MAX_IMAGE_SCALE;
				return highQualityScale;
			} catch(Exception ignore){}
		}
		return MAX_IMAGE_SCALE;
	}

	/**
	 * Generates 400x564 (A4 format demention) background from pdf byte array.
	 *
	 * @param pdfContent - pdf byte array
	 * @return - binary content of generated preview, null - if transformation can't be performed
	 */
	public static byte[] generateBackgroundImage(byte[] pdfContent, UserPreferenceService userPreferenceService) {
		return generateHighQualityPdfPreview(pdfContent, getBackgroundQualityScale(userPreferenceService));
	}

	public static List<byte[]> generateOriginalHighQuality(byte[] pdfContent) {
		var request = new ImageRequest.Builder(pdfContent)
				.qualityScale(DEFAULT_MAX_IMAGE_SCALE)
				.fullBackgroundRequired(true)
				.build();
		return generateQualityPreview(request);
	}

	public static Boolean isPortrait(byte[] content) {
		try(PDDocument doc = PDDocument.load(content)) {
			return ImageHelper.isPortrait(doc.getPage(0));
		} catch (IOException e) {
			try {
				var image = getAsBufferedImage(content);
				if (image == null)
					throw new IOException();
				return image.getHeight() > image.getWidth();
			} catch (IOException ex) {
				logger.error("Unable to read image or pdf doc.");
				logger.catching(e);
				return null;
			}
		}
	}
}
