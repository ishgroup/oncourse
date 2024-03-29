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
package ish.common.util;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ImageHelper {

	public static final int PDF_PREVIEW_WIDTH = 165;
	public static final int PDF_PREVIEW_HEIGHT = 240;
	public static final int A4_PIXELS_WIDTH = 595;
	public static final int A4_PIXELS_HEIGHT = 845;
	public static final String PDF_PREVIEW_FORMAT = "png";
	protected static final String MIME_TYPE_IMAGE_PREFIX = "image/";
	private static final Logger logger = LogManager.getLogger();

	public static BufferedImage scaleImageToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc) {
		return scaleImageToSize(nMaxWidth, nMaxHeight, imgSrc, false);
	}

	public static BufferedImage smoothScaleImageTo(int width, int height, BufferedImage image){
		Image tmp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return scaledImage;
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
	 * Determines if document version is an image by its MIME type
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
	 * Determines if document version is a Microsoft Office document by its MIME type
	 * according <a href="https://www.iana.org/assignments/media-types/media-types.xhtml"> IANA </a>}
	 */
	public static boolean isDoc(String mimeType) {
		List<String> types = Arrays.asList("application/msword",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		return types.contains(mimeType);
	}

	/**
	 * Determines if document version is a Microsoft Excel document by its MIME type
	 * according <a href="https://www.iana.org/assignments/media-types/media-types.xhtml"> IANA </a>}
	 */
	public static boolean isExcel(String mimeType) {
		List<String> types = Arrays.asList("application/vnd.ms-excel",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		return types.contains(mimeType);
	}

	/**
	 * Determines if document version is a .CSV document by its MIME type
	 * according <a href="https://www.iana.org/assignments/media-types/media-types.xhtml"> IANA </a>}
	 */
	public static boolean isCsv(String mimeType) {
		return mimeType.equals("text/csv");
	}

	/**
	 * Determines if document version is a text document by its MIME type
	 * according <a href="https://www.iana.org/assignments/media-types/media-types.xhtml"> IANA </a>}
	 */
	public static boolean isText(String mimeType) {
		List<String> types = Arrays.asList("application/xml", "text/html", "text/calendar", "text/javascript",
				"application/vnd.oasis.opendocument.text", "application/x-httpd-php", "text/rtf", "text/plain", "text/xml");
		return types.contains(mimeType);
	}

	/**
	 * Generates 400x564 (A4 format demention) preview from pdf byte array.
	 *
	 * @param pdfContent - pdf byte array
	 * @return - binary content of generated preview, null - if transformation can't be performed
	 */
	public static byte[] generatePdfPreview(byte[] pdfContent) {
		ImageRequest imageRequest = new ImageRequest.Builder(pdfContent)
				.qualityScale(2)
				.a4FormatRequired(true)
				.cutRequired(true)
				.build();
		List<byte[]> result = generateQualityPreview(imageRequest);
		return result == null ? null : result.get(0);
	}


	/**
	 * Generates 400x564 (A4 format demention) preview from pdf or image byte array.
	 *
	 * @param imageRequest - request for generating image from pdf background
	 * @return - binary content of generated preview, null - if transformation can't be performed
	 */
	public static List<byte[]> generateQualityPreview(ImageRequest imageRequest) {
		List<BackgroundData> parsedBackground = parseBackground(imageRequest);
		if(parsedBackground == null) {
			return null;
		}
		ArrayList<byte[]> result = new ArrayList<>();
		for(BackgroundData backgroundData:parsedBackground){
			result.add(processImageToPreview(backgroundData, imageRequest));
		}
		return result;
	}

	private static List<BackgroundData> parseBackground(ImageRequest imageRequest){
		try {
			return parseBackAsPdf(imageRequest);
		} catch (Exception e) {
			try {
				BufferedImage image = getAsBufferedImage(imageRequest.getPdfContent());
				if (image == null)
					throw new Exception();
				boolean landscape = image.getWidth() > image.getHeight();
				return new ArrayList<BackgroundData>(){{add(new BackgroundData(image, landscape));}};
			} catch (Exception ex) {
				logger.error("Unable to load background" );
				logger.catching(e);
				return null;
			}
		}
	}

	private static List<BackgroundData> parseBackAsPdf(ImageRequest imageRequest) throws Exception {
		BufferedImage image;
		boolean landscape = false;
		List<BackgroundData> backgrounds = new ArrayList<>();
		try(PDDocument doc = PDDocument.load(imageRequest.getPdfContent())){
			PDFRenderer renderer = new PDFRenderer(doc);
			int pagesNumber = imageRequest.isFullBackgroundRequired() ? doc.getNumberOfPages() : 1;
			float scale = imageRequest.getQualityScale();
			for(int pageIndex = 0; pageIndex < pagesNumber; pageIndex++) {
				image = renderer.renderImage(pageIndex, scale);
				if(backgrounds.isEmpty()){
					landscape = !isPortrait(doc.getPage(0));
				}
				backgrounds.add(new BackgroundData(image, landscape));
			}
			return backgrounds;
		}
	}

	private static byte[] processImageToPreview(BackgroundData parsedBackground, ImageRequest imageRequest){
		boolean landscape = parsedBackground.isLandscape();
		BufferedImage image = parsedBackground.getBufferedImage();

		if(imageRequest.isA4FormatRequired()) {
			int width = landscape ? PDF_PREVIEW_HEIGHT : PDF_PREVIEW_WIDTH;
			int height = landscape ? PDF_PREVIEW_WIDTH : PDF_PREVIEW_HEIGHT;

			if(!imageRequest.isCutRequired() && image.getWidth() > width && image.getHeight() > height){
				width*=3.5;
				height*=3.5;
			}

			image = smoothScaleImageTo(width, height, image);
		}

		return imageAsPdfPreviewByteArray(image);
	}

	public static byte[] imageAsPdfPreviewByteArray(BufferedImage image){
		try {
			return getAsByteArray(image, PDF_PREVIEW_FORMAT);
		} catch (IOException e) {
			logger.error("Unable to generate pdf preiew" );
			logger.catching(e);
			return null;
		}
	}

	public static Boolean isPortrait(PDPage page) {
		PDRectangle rectangle = page.getMediaBox();
		int rotation = page.getRotation();

		return !((rectangle.getWidth() > rectangle.getHeight()) || rotation == 90 || rotation == 270);
	}
}
