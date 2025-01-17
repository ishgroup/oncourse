/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  ArticleProduct,
  CheckoutPaymentPlan, CheckoutResponse,
  CheckoutSaleRelation,
  Course,
  CourseClass,
  Discount,
  EnrolmentStudyReason,
  EntityRelationCartAction,
  Invoice,
  MembershipProduct,
  PaymentMethod,
  Sale,
  VoucherProduct
} from '@api/model';
import { StringArgFunction } from 'ish-ui';
import { Dispatch } from 'redux';
import { IAction } from '../../common/actions/IshAction';

export type CheckoutEntity = "contact"
  | "course"
  | "voucher"
  | "product"
  | "membership"

export interface CheckoutListRow {
  type: CheckoutEntity;
  checked?: boolean;
  quantity?: number;
}

export type CheckoutDiscount = Discount & VoucherProduct & {
  type: "discount" | "voucher";
  expiryDate?: string;
  redemptionValue?: string;
  history?: any[];
  maxCoursesRedemption?: number;
  redeemedCourses?: number;
  purchaseDate?: string;
  purchaseValue?: number;
  appliedValue?: number;
  availableValue?: number;
  productId?: number;
  courseIds?: number[];
  redeemableById?: number;
  statusValue?: string;
}

export interface CheckoutCourseClass extends CourseClass {
  name: string;
  price: number;
  placesLeft: number;
  room: string;
  site: string;
  siteTimezone: string;
  tutors: string;
  sessionStartDates: string;
  sessionEndDates: string;
  hours: number;
  hasPaymentPlans: boolean;
  isVet: boolean;
  sessionIds?: number[];
  fundingProviderId?: number;
}

export type CheckoutCourse = Course & CheckoutListRow & {
  id: any;
  courseId: number;
  class: CheckoutCourseClass;
  tax: number;
  taxAmount: number;
  discount: Discount;
  discounts: Discount[];
  discountExTax: number;
  price: number;
  priceOverriden: number;
  priceExTax: number;
  studyReason: EnrolmentStudyReason;
  paymentPlans: {
    amount: number,
    date: Date
  }[],
  transferedClassId?: number;
  voucherId?: number;
}

export type CheckoutItem = CheckoutCourse & VoucherProduct & MembershipProduct & ArticleProduct & {
  type: CheckoutEntity;
  originalPrice?: number;
  validTo?: string;
  restrictToPayer?: boolean;
  expireNever?: string;
  cartAction?: EntityRelationCartAction;
  relationDiscount?: Discount;
  fromItemRelation?: Sale;
  quantity?: number;
};

export type CheckoutProductPurchase = {
  contactId: number;
  items: CheckoutItem[];
}

export interface CheckoutState {
  step: number;
  contacts?: CheckoutContact[];
  items?: CheckoutItem[];
  summary?: CheckoutSummary;
  contactEditRecord?: any;
  relatedContacts?: any[];
  courseClasses?: CheckoutCourseClass[];
  checkCourseClassEmpty?: boolean;
  itemEditRecord?: any;
  payment?: CheckoutPayment;
  hasErrors?: boolean;
  disableDiscounts?: boolean;
  salesRelations?: CheckoutSaleRelation[];
}

export interface CheckoutSummary {
  list?: CheckoutSummaryListItem[];
  finalTotal?: number;
  payNowTotal?: number;
  discounts?: CheckoutDiscount[];
  previousCredit?: PreviousInvoiceState;
  previousOwing?: PreviousInvoiceState;
  vouchers?: CheckoutDiscount[];
  voucherItems?: any[];
  allowAutoPay?: boolean;
  invoiceDueDate?: string;
  paymentDate?: string;
}

export interface CheckoutContactRelation {
  id: number;
  relationId: string;
  relatedContactName: string;
  relatedContactId: string;
}

export interface CheckoutContact extends CheckoutListRow {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  birthDate: string;
  isCompany: boolean;
  invoiceTerms: string;
  relations: CheckoutContactRelation[];
  message?: string;
  defaultSelectedOwing?: number;
}

export interface CheckoutSummaryListItem {
  contact?: CheckoutContact;
  items?: CheckoutItem[];
  itemTotal?: number;
  payer?: boolean;
  sendInvoice?: boolean;
  sendEmail?: boolean;
}

export type CheckoutPaymentGateway =
  'EWAY' |
  'EWAY_TEST' |
  'STRIPE' |
  'STRIPE_TEST' |
  'WINDCAVE' |
  'TEST' |
  'OFFLINE' |
  'DISABLED'

export interface CheckoutPaymentProcess {
  status?: "" | "cancel" | "fail" | "success";
  statusCode?: number;
  statusText?: string;
  data?: any;
}

export interface CheckoutPayment extends CheckoutResponse {
  availablePaymentTypes?: PaymentMethod[];
  selectedPaymentType?: any;
  paymentPlans?: CheckoutPaymentPlan[];
  isProcessing?: boolean;
  isFetchingDetails?: boolean;
  isSuccess?: boolean;
  wcIframeUrl?: string;
  xPaymentSessionId?: string;
  merchantReference?: string;
  process?: CheckoutPaymentProcess;
  invoice?: Invoice;
  paymentId?: number;
  savedCreditCard?: {
    creditCardName: string;
    creditCardNumber: string;
    creditCardType: string;
  }
}

export interface CheckoutPreviousInvoice extends Invoice {
  checked: boolean;
  nextDue?: number;
}

export interface PreviousInvoiceState {
  invoices: CheckoutPreviousInvoice[];
  invoiceTotal: number;
  unCheckAll: boolean;
  payDueAmounts?: boolean
}

export interface CheckoutEnrolmentCustom {
  contactId?: number;
  courseClass?: CheckoutCourse;
}

export interface CheckoutSaleExtended extends Sale {
  cartItem?: CheckoutItem;
  link?: string;
}

export interface CheckoutSaleRelationExtended extends CheckoutSaleRelation {
  contactId?: number;
  toItem?: CheckoutSaleExtended;
}

export type CheckoutAddItemsRequiest = {
  enrolments?: CheckoutEnrolmentCustom[],
  purchases?: CheckoutProductPurchase[],
  keepChecked?: boolean
}

export interface CreditCardPaymentPageProps {
  dispatch?: Dispatch<IAction>;
  classes?: any;
  summary?: CheckoutSummary;
  payment?: CheckoutPayment;
  isPaymentProcessing?: boolean;
  disablePayment?: boolean;
  hasSummarryErrors?: boolean;
  currencySymbol?: any;
  iframeUrl?: string;
  xPaymentSessionId?: string;
  merchantReference?: string;
  checkoutProcessCcPayment?: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => void;
  clearCcIframeUrl: () => void;
  checkoutGetPaymentStatusDetails: StringArgFunction;
  checkoutPaymentSetCustomStatus?: StringArgFunction;
  onCheckoutClearPaymentStatus: () => void;
  process?: CheckoutPaymentProcess;
  paymentInvoice?: any;
  paymentId?: number;
  payerName: string;
}