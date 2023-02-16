/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util

import com.spire.doc.Document
import com.spire.doc.FileFormat
import com.spire.doc.documents.ImageType
import com.spire.xls.Workbook
import com.spire.xls.Worksheet
import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.awt.image.BufferedImage

import static ish.util.ImageHelper.*

def class Size {
    private int weight
    private int height

    Size(int weight, int height) {
        this.weight = weight
        this.height = height
    }

    int getWeight() {
        return weight
    }

    int getHeight() {
        return height
    }
}

class ThumbnailGenerator {

    private static final Logger logger = LogManager.getLogger();


    private static final int IMAGE_THUMBNAIL_SIZE = 140

    private static final int A4_PIXELS_WIDTH_600_PPI = 4960

    private static final int A4_PIXELS_HEIGHT_600_PPI = 7016


    /**
     * Generates 140x140 thumbnail from image.
     *
     * @param content - image binary content
     * @param type - image type
     * @return - binary content of generated thumbnail, null - if transformation can't be performed
     */
    static byte[] generateForImg(byte[] content, String type) throws IOException {
        String format = type

        // if type string is in MIME type format (e.g. image/png), remove "image/" prefix
        if (StringUtils.startsWithIgnoreCase(format, MIME_TYPE_IMAGE_PREFIX)) {
            format = StringUtils.removeStart(format, MIME_TYPE_IMAGE_PREFIX)
        }

        BufferedImage image = getAsBufferedImage(content)

        if (image == null) {
            logger.warn("Provided byte array can't be converted to any supported image type.")
            return null
        }

        return getAsByteArray(scaleImageToSize(IMAGE_THUMBNAIL_SIZE, IMAGE_THUMBNAIL_SIZE, image), format)
    }

    /**
     * Generate thumbnail for Microsoft Office Document.
     *  - size for portrait orientation: 595 x 845
     *  - size for landscape orientation: 845 x 595
     *
     * @param content - binary content
     * @return - binary content of generated thumbnail, null - if transformation can't be performed
     */
    static byte[] generateForDoc(byte[] content) {
        try {
            Document wordDoc = new Document()
            wordDoc.loadFromStream(new ByteArrayInputStream(content), FileFormat.Auto)

            BufferedImage image = wordDoc.saveToImages(0, ImageType.Bitmap)
            def scaledSize = calculateThumbnailSize(image, false)

            return getAsByteArray(scaleImageToSize(scaledSize.weight, scaledSize.height, image), PDF_PREVIEW_FORMAT)

        } catch (Exception e) {
            logger.warn("Can't generate thumbnail for provided Microsoft Office Document.")
            logger.catching(e)
        }
        return null
    }

    /**
     * Generate thumbnail for text documents
     *  - size for portrait orientation: 4960 x 7016 (600 PPI)
     *  - size for landscape orientation: 7016 x 4960 (600 PPI)
     *
     * @param content - binary content
     * @return - binary content of generated thumbnail, null - if transformation can't be performed
     */
    static byte[] generateForText(byte[] content) {
        try {

            Document text = new Document()
            text.loadText(new ByteArrayInputStream(content))

            BufferedImage image = text.saveToImages(0, ImageType.Bitmap)
            // remove page breaker (render as bottom  border)
            image = image.getSubimage(0, 0, image.width, image.height - 5)
            def scaledSize = calculateThumbnailSize(image, true)

            ByteArrayOutputStream scaledImageOutput = new ByteArrayOutputStream()
            Thumbnails.of(new ByteArrayInputStream(getAsByteArray(image, PDF_PREVIEW_FORMAT)))
                    .size(scaledSize.weight, scaledSize.height)
                    .outputFormat(PDF_PREVIEW_FORMAT)
                    .toOutputStream(scaledImageOutput)

            return scaledImageOutput.toByteArray()

        } catch (Exception e) {
            logger.warn("Can't generate thumbnail for provided text document.")
            logger.catching(e)
        }
        return null
    }

    /**
     * Generate thumbnail for Microsoft Office Excel.
     *  - size for portrait orientation: 595 x 845
     *  - size for landscape orientation: 845 x 595
     *
     * @param content - binary content
     * @return - binary content of generated thumbnail, null - if transformation can't be performed
     */
    static byte[] generateForExcel(byte[] content) {
        try {
            Workbook workbook = new Workbook()
            workbook.loadFromStream(new ByteArrayInputStream(content), true)
            workbook.getConverterSetting().setSheetFitToPage(true)
            Worksheet sheet = workbook.getWorksheets().get(0)

            for (int i = 1; i < sheet.getColumns().length; i++) {
                sheet.autoFitColumn(i)
            }

            BufferedImage image = sheet.saveToImage(
                    sheet.getFirstRow(), sheet.getFirstColumn(),
                    Math.min(sheet.getLastRow(), (int) (A4_PIXELS_HEIGHT * 0.12)), sheet.getLastColumn()
            )
            def scaledSize = calculateThumbnailSize(image, false)

            return getAsByteArray(scaleImageToSize(scaledSize.weight, scaledSize.height, image), PDF_PREVIEW_FORMAT)

        } catch (Exception e) {
            logger.warn("Can't generate thumbnail for provided Microsoft Office Excel.")
            logger.catching(e)
        }
        return null
    }

    /**
     * Generate thumbnail for .scv document
     *  - size for portrait orientation: 4960 x 7016 (600 PPI)
     *  - size for landscape orientation: 7016 x 4960 (600 PPI)
     *
     * @param content - binary content
     * @return - binary content of generated thumbnail, null - if transformation can't be performed
     */
    static byte[] generateForCsv(byte[] content) {
        try {
            Workbook workbook = new Workbook();
            workbook.loadFromStream(new ByteArrayInputStream(content), ",", 1, 1)
            workbook.getConverterSetting().setSheetFitToPage(true)
            Worksheet sheet = workbook.getWorksheets().get(0)

            for (int i = 1; i < sheet.getColumns().length; i++) {
                sheet.autoFitColumn(i)
            }

            BufferedImage image = sheet.saveToImage(
                    sheet.getFirstRow(), sheet.getFirstColumn(),
                    Math.min(sheet.getLastRow(), (int) (A4_PIXELS_HEIGHT * 0.12)), sheet.getLastColumn()
            )
            def scaledSize = calculateThumbnailSize(image, true)

            ByteArrayOutputStream scaledImageOutput = new ByteArrayOutputStream()
            Thumbnails.of(new ByteArrayInputStream(getAsByteArray(image, PDF_PREVIEW_FORMAT)))
                    .size(scaledSize.weight, scaledSize.height)
                    .outputFormat(PDF_PREVIEW_FORMAT)
                    .toOutputStream(scaledImageOutput)

            return scaledImageOutput.toByteArray()

        } catch (Exception e) {
            logger.warn("Can't generate thumbnail for provided .csv document.")
            logger.catching(e)
        }
        return null
    }

    private static Size calculateThumbnailSize(BufferedImage image, boolean highQuality) {
        int width = highQuality ? A4_PIXELS_WIDTH_600_PPI : A4_PIXELS_WIDTH
        int height = highQuality ? A4_PIXELS_HEIGHT_600_PPI : A4_PIXELS_HEIGHT

        // check document orientation
        return image.width < image.height ? new Size(width, height) : new Size(height, width)
    }
}
