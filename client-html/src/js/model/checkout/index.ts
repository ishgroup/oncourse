/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  ArticleProduct, CheckoutPaymentPlan, CheckoutSaleRelation,
  Course,
  CourseClass,
  Discount,
  EnrolmentStudyReason, EntityRelationCartAction,
  MembershipProduct,
  PaymentMethod, Sale,
  VoucherProduct
} from "@api/model";

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
};

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

export interface CheckoutPaymentProcess {
  status?: "" | "cancel" | "fail" | "success";
  statusCode?: number;
  statusText?: string;
  data?: any;
}

export interface CheckoutPayment {
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
  invoice?: any;
  paymentId?: number;
  savedCreditCard?: {
    creditCardName: string;
    creditCardNumber: string;
    creditCardType: string;
  }
}

export interface PreviousInvoiceState {
  invoices?: any[];
  invoiceTotal?: number;
  unCheckAll?: boolean;
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
