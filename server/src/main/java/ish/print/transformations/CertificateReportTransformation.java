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

package ish.print.transformations;

import ish.print.AdditionalParameters;

/**
 * Created by anarut on 8/8/16.
 */
public class CertificateReportTransformation extends PrintTransformation {

    public CertificateReportTransformation() {
        PrintTransformationField<Boolean> printQrCode = new PrintTransformationField<>(AdditionalParameters.PRINT_QR_CODE.toString(), "Print QR code", Boolean.class, Boolean.TRUE);
        addFieldDefinition(printQrCode);

        setInputEntityName("Certificate");
        setOutputEntityName("Certificate");

        setTransformationFilter("id in $sourceIds");
    }
}
