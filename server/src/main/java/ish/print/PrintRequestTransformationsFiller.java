/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.print;

import ish.print.transformations.PrintTransformation;

import java.util.List;

public class PrintRequestTransformationsFiller {
    public static void fillWithTransformations(PrintRequest request, List<?> records,
                                               String outputEntity, String keycode){
        for (Object record : records) {
            String entityName = record.getClass().getSimpleName();
            if (!request.containsTransformationFor(entityName)) {
                PrintTransformation transformation = PrintTransformationsFactory
                        .getPrintTransformationFor(entityName, outputEntity, keycode);

                if (transformation != null) {
                    request.addPrintTransformation(entityName, transformation);
                }
            }
        }
    }
}
