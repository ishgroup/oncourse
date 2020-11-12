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

package ish.oncourse.server.lifecycle;

import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.FundingSource;

public class SetFundingContact {

    private Enrolment enrolment;

    private SetFundingContact() {

    }

    public void set() {
        var fundingContract = enrolment.getCourseClass().getRelatedFundingSource();
        if (fundingContract != null) {
            enrolment.setRelatedFundingSource(fundingContract);
        }
    }

    public static SetFundingContact valueOf(Enrolment enrolment) {
        var setFundingContact = new SetFundingContact();
        setFundingContact.enrolment = enrolment;
        return setFundingContact;
    }
}
