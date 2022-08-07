/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EnrolmentDialogs } from "../../../../model/entities/Enrolment";

export interface EnrolmentsState {
  invoiceLines: any[];
  dialogOpened: EnrolmentDialogs;
  processing: boolean;
}
