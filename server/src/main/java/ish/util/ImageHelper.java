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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 *
 */
public class ImageHelper {

	private static final Logger logger = LogManager.getLogger();

	private static final String MIME_TYPE_IMAGE_PREFIX = "image/";
	private static final int IMAGE_THUMBNAIL_SIZE = 140;

	private static final int PDF_PREVIEW_WIDTH = 165;
	private static final int PDF_PREVIEW_HEIGHT = 240;
	private static final String PDF_PREVIEW_FORMAT = "png";


	public static BufferedImage scaleImageToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc) {
		return scaleImageToSize(nMaxWidth, nMaxHeight, imgSrc, false);
	}

	public static BufferedImage scaleImageToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc, boolean returnNewImage) {
		int nHeight = imgSrc.getHeight();
		int nWidth = imgSrc.getWidth();
		double scaleX = (double) nMaxWidth / (double) nWidth;
		double scaleY = (double) nMaxHeight / (double) nHeight;
		double fScale = Math.min(scaleX, scaleY);
		return scaleImage(fScale, imgSrc, returnNewImage);
	}

	public static BufferedImage scaleImage(double scale, BufferedImage srcImg, boolean returnNewImage) {
		if (scale == 1) {
			return srcImg;
		}
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), null);
		if (returnNewImage) {
			srcImg = op.filter(srcImg, null);
			return srcImg;
		}
		return op.filter(srcImg, null);
	}

	public static byte[] getAsByteArray(RenderedImage image, String format) throws IOException {
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		ImageIO.write(image, format, oStream);
		return oStream.toByteArray();
	}

	public static BufferedImage getAsBufferedImage(byte[] data) throws IOException {
		return ImageIO.read(new ByteArrayInputStream(data));
	}

	public static int imageWidth(Object image) {
		if (image instanceof byte[]) {
			try {
				return getAsBufferedImage((byte[]) image).getWidth();
			} catch (Throwable e) {
				logger.warn("(BufferedImage) cannot create image from byte[]", e);
			}
		}
		return 0;
	}

	public static int imageHeight(Object image) {
		if (image instanceof byte[]) {
			try {
				return getAsBufferedImage((byte[]) image).getHeight();
			} catch (Throwable e) {
				logger.warn("(BufferedImage) cannot create image from byte[]", e);
			}
		}
		return 0;
	}

	/**
	 * Determines if document version is an image by its MIME type.
	 */
	public static boolean isImage(byte[] content, String mimeType) {
		BufferedImage image = null;

		if (content != null) {
			try {
				image = getAsBufferedImage(content);
			} catch (IOException e) {
				logger.warn(e);
			}
		}

		return image != null && mimeType != null && mimeType.startsWith(MIME_TYPE_IMAGE_PREFIX);
	}

	/**
	 * Generates 140x140 thumbnail from image.
	 *
	 * @param content - image binary content
	 * @param type - image type
	 * @return - binary content of generated thumbnail, null - if transformation can't be performed
	 */
	public static byte[] generateThumbnail(byte[] content, String type) throws IOException {
		String format = type;

		// if type string is in MIME type format (e.g. image/png), remove "image/" prefix
		if (StringUtils.startsWithIgnoreCase(format, MIME_TYPE_IMAGE_PREFIX)) {
			format = StringUtils.removeStart(format, MIME_TYPE_IMAGE_PREFIX);
		}

		BufferedImage bufferedImage = getAsBufferedImage(content);

		if (bufferedImage == null) {
			logger.warn("Provided byte array can't be converted to any supported image type.");

			return null;
		}

		return getAsByteArray(ImageHelper.scaleImageToSize(
				IMAGE_THUMBNAIL_SIZE, IMAGE_THUMBNAIL_SIZE, bufferedImage),
				format);
	}


	/**
	 * Generates 400x564 (A4 format demention) preview from pdf byte array.
	 *
	 * @param pdfContent - pdf byte array
	 * @return - binary content of generated preview, null - if transformation can't be performed
	 */
	public static byte[] generatePdfPreview(byte[] pdfContent) {

		try (PDDocument doc = PDDocument.load(pdfContent)) {

			boolean landscape = !isPortrait(doc.getPage(0));
			int width = landscape ? PDF_PREVIEW_HEIGHT : PDF_PREVIEW_WIDTH;
			int height = landscape ? PDF_PREVIEW_WIDTH : PDF_PREVIEW_HEIGHT;

			PDFRenderer renderer = new PDFRenderer(doc);
			BufferedImage image = renderer.renderImage(0,3);
			Image tmp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
			Graphics2D g2d = dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();

			return getAsByteArray(dimg, PDF_PREVIEW_FORMAT);
		} catch (Exception e) {
			logger.error("Unable to generate preiew" );
			logger.catching(e);
			return null;
		}

	}

	public static Boolean isPortrait(PDPage page) {
			PDRectangle rectangle = page.getMediaBox();
			int rotation = page.getRotation();

			return !((rectangle.getWidth() > rectangle.getHeight()) || rotation == 90 || rotation == 270);
	}

	public static Boolean isPortrait(byte[] pdfContent) {
		try (PDDocument doc = PDDocument.load(pdfContent)) {

			return isPortrait(doc.getPage(0));
		} catch (Exception e) {
			logger.error("Unable to read pdf document." );
			logger.catching(e);
			return null;
		}
	}
}
