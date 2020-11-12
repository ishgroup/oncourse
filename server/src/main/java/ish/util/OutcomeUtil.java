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

import ish.messaging.ICertificateOutcome;
import ish.messaging.IOutcome;

/**
 * Created by anarut on 11/14/16.
 */
public class OutcomeUtil {

    public static boolean isEditableStatus(IOutcome outcome) {
        return !outcome.getCertificateOutcomes().stream()
                .map(ICertificateOutcome::getCertificate)
                .filter(c -> c.getPrintedOn() != null && c.getRevokedOn() == null)
                .findAny()
                .isPresent();
    }
}
