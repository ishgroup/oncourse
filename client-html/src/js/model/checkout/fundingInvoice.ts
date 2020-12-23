/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
 Contact, Enrolment, Invoice, InvoicePaymentPlan, TrainingPlan
} from "@api/model";
import { CheckoutSummaryListItem } from "./index";

export type CheckoutFundingInvoiceItem = Enrolment & Invoice & {
  enrolment: CheckoutSummaryListItem;
}

export interface CheckoutFundingInvoice {
  active: boolean;
  company: Contact;
  item: CheckoutFundingInvoiceItem;
  trackAmountOwing: boolean;
  relatedFundingSourceId: number;
  fundingProviderId: number;
  vetPurchasingContractID: string;
  trainingPlans: TrainingPlan[];
  paymentPlans: InvoicePaymentPlan[];
  total: number;
}
