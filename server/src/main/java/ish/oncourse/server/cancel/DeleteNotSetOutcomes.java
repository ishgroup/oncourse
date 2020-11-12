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

package ish.oncourse.server.cancel;

import ish.oncourse.types.FundingStatus;
import ish.common.types.OutcomeStatus;
import ish.oncourse.server.cayenne.Outcome;
import ish.oncourse.server.cayenne.Enrolment;
import org.apache.cayenne.ObjectContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteNotSetOutcomes {

    public static void deleteOutcomes(Enrolment enrolment, ObjectContext context) {

        List<Outcome> outcomes = new ArrayList<>(enrolment.getOutcomes());

        for (Outcome o : Outcome.STATUS.eq(OutcomeStatus.STATUS_NOT_SET).filterObjects(outcomes)) {
            if (o.getCertificateOutcomes().isEmpty() ||
                    o.getCertificateOutcomes().stream()
                            .noneMatch(certOut -> certOut.getCertificate().getRevokedOn() != null)) {

                if (o.getFundingUploadOutcomes().stream()
                            .noneMatch(fuo -> Arrays.asList(FundingStatus.EXPORTED, FundingStatus.SUCCESS)
                                    .contains(fuo.getFundingUpload().getStatus()))) {

                    context.deleteObject(o);
                }
            }
        }
    }
}
