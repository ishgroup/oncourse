/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Enrolment, Invoice } from "@api/model";
import { CheckoutSummaryListItem } from "./index";

export type CheckoutFundingInvoiceItem = Enrolment & Invoice & {
  enrolment: CheckoutSummaryListItem;
  fundingProviderId?: number;
  trainingPlans?: any[];
}

export interface CheckoutFundingInvoice {
  companies?: any[];
  item?: CheckoutFundingInvoiceItem;
  trackAmountOwing?: boolean;
}
