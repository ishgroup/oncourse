/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  CheckoutArticle,
  CheckoutEnrolment,
  CheckoutMembership,
  CheckoutModel,
  CheckoutPaymentPlan,
  CheckoutVoucher,
  ContactNode,
  Invoice,
  InvoiceInvoiceLine,
  InvoicePaymentPlan
} from "@api/model";
import { differenceInMinutes, format, isBefore } from "date-fns";
import { YYYY_MM_DD_MINUSED } from "../../../common/utils/dates/format";
import { decimalMinus, decimalPlus } from "../../../common/utils/numbers/decimalCalculation";
import {
  CheckoutCourse,
  CheckoutCourseClass,
  CheckoutDiscount,
  CheckoutEntity,
  CheckoutItem,
  CheckoutState, CheckoutSummary,
  CheckoutSummaryListItem
} from "../../../model/checkout";
import { CheckoutFundingInvoice } from "../../../model/checkout/fundingInvoice";
import { CheckoutCurrentStep, CheckoutCurrentStepType } from "../constants";
import { getFundingInvoices } from "./fundingInvoice";

export const filterPastClasses = courseClasses => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return courseClasses.filter(c => c.startDateTime === null || isBefore(today, new Date(c.endDateTime)));
};

export const getExpireDate = month => {
  const toDay = new Date();
  const curMonth = toDay.getMonth();
  if (curMonth > month) {
    toDay.setFullYear(toDay.getFullYear() + 1, month, 1);
  } else if (curMonth === month && toDay.getDate() > 1) {
    toDay.setFullYear(toDay.getFullYear() + 1, month, 1);
  } else {
    toDay.setFullYear(toDay.getFullYear(), month, 1);
  }
  return toDay;
};

export const isPromotionalCodeExist = (code, checkout) => {
  const inDiscounts = checkout.summary.discounts.filter(d => d.code === code).length;
  const inVouchers = checkout.summary.vouchers.filter(v => v.code === code).length;
  return !!(inDiscounts || inVouchers);
};

export interface ListPreviousInvoicesArgs {
  summary: CheckoutSummary;
  type: "previousCredit" | "previousOwing";
  itemIndex: number;
  isUnCheckAll: boolean;
  payDueAmounts: boolean;
}

export const listPreviousInvoices = ({
  summary, type, itemIndex, isUnCheckAll, payDueAmounts
}: ListPreviousInvoicesArgs) => {
  const previousInvoiceList = {
    [type]: {
      ...summary[type],
      payDueAmounts,
      invoices: [],
      invoiceTotal: 0,
    }
  };

  let total = 0;

  const totalMarker = payDueAmounts ? "nextDue" : "amountOwing";

  previousInvoiceList[type].invoices = summary[type].invoices.map((item, i) => {
    const checked = isUnCheckAll
      ? !previousInvoiceList[type].unCheckAll
      : i === itemIndex
        ? !item.checked
        : item.checked;

    if (checked) {
      total = decimalPlus(total, item[totalMarker]);
    }

    return { ...item, checked };
  });

  previousInvoiceList[type].invoiceTotal = total;
  previousInvoiceList[type].unCheckAll = !!previousInvoiceList[type].invoices.filter(i => !i.checked).length;

  return previousInvoiceList;
};

export const setSummaryListWithDefaultPayer = (summaryList, payerIndex = 0) => summaryList.map((l, i) => {
  if (i === payerIndex) {
    return { ...l, payer: true, sendInvoice: Boolean(l.contact.email) };
  }
  return { ...l, payer: false, sendInvoice: false };
});

export const modifySummaryLisItem = (summaryList, itemData, toggleCheck = false) => summaryList.map((l, li) => {
    if (li === itemData.listIndex) {
      return {
        ...l,
        items: l.items.map((item, i) => {
          if (i === itemData.itemIndex) {
            if (toggleCheck) {
              return {
                ...item,
                ...item.voucherId && item.checked ? { voucherId: null } : {},
                checked: !item.checked
              };
            }
            return { ...item, ...itemData.value };
          }
          return item;
        })
      };
    }
    return l;
  });

export const checkoutProductMap = p => ({
  id: p.id,
  code: p.sku,
  name: p.name,
  price: p.price_with_tax ? parseFloat(p.price_with_tax) : null,
  expiryType: p.expiryType,
  expiryDays: p.expiryDays ? parseFloat(p.expiryDays) : null
});

export const checkoutVoucherMap = p => ({
  id: p.id,
  code: p.sku,
  name: p.name,
  price: p.priceExTax ? parseFloat(p.priceExTax) : null,
  expiryDays: p.expiryDays ? parseFloat(p.expiryDays) : null
});

export const mergeInvoicePaymentPlans = (paymentPlans: InvoicePaymentPlan[]) => {
  let paidAmount = paymentPlans
    .filter(p => p.entityName === "PaymentIn" && p.successful)
    .reduce((p, c) => decimalPlus(p, c.amount), 0);

  const filteredPaymentPlans = paymentPlans
    .filter(p => p.entityName === "InvoiceDueDate")
    .sort((a, b) => (a.date > b.date ? 1 : -1));

  for (let i = 0; i < filteredPaymentPlans.length; i++) {
    const amount = filteredPaymentPlans[i].amount;

    if (paidAmount >= amount) {
      filteredPaymentPlans[i].amount = 0;
      paidAmount = decimalMinus(paidAmount, amount);
    } else if (paidAmount > 0) {
      filteredPaymentPlans[i].amount = decimalMinus(filteredPaymentPlans[i].amount, paidAmount);
      paidAmount = 0;
      break;
    }
  }

  return filteredPaymentPlans.filter(({ amount }) => amount)
    .map(({ amount, date }) => ({
    amount,
    date: new Date(date)
  }));
};

export const getCheckoutModel = (
  state: CheckoutState,
  paymentPlans: CheckoutPaymentPlan[],
  fundingInvoices: CheckoutFundingInvoice[],
  summaryValues: any = {},
  pricesOnly?: boolean
): CheckoutModel => {
  const { summary, payment } = state;

  const payerItem = summary.list.find(l => l.payer);

  const vouchers: CheckoutVoucher[] = summary.voucherItems.filter(v => v.checked).map((v): CheckoutVoucher => ({
    productId: v.id,
    validTo: v.validTo,
    value: v.price,
    restrictToPayer: v.restrictToPayer
  }));

  const vouchersTotal = summary.vouchers.reduce((p, v) => decimalPlus(p, v.appliedValue), 0);

  const totalIncOwingExVouchers = decimalMinus(decimalPlus(summary.finalTotal, summary.previousOwing.invoiceTotal), vouchersTotal);

  const absCredit = Math.abs(summary.previousCredit.invoiceTotal);

  const paymentPlansTotal = paymentPlans.reduce((p, c) => decimalPlus(p, c.amount), 0);

  let payForThisInvoice = decimalMinus(
    summary.payNowTotal || 0,
    summary.previousCredit.invoiceTotal,
    -vouchersTotal
  );

  if (payForThisInvoice > summary.finalTotal) {
    payForThisInvoice = summary.finalTotal;
  }

  if (pricesOnly || payForThisInvoice < 0 || paymentPlansTotal >= summary.finalTotal) {
    payForThisInvoice = 0;
  }

  let invoicesCover = decimalMinus(summary.payNowTotal || 0, -vouchersTotal, summary.previousCredit.invoiceTotal, payForThisInvoice);

  if (invoicesCover < 0) {
    invoicesCover = 0;
  }

  let appliedCredit = totalIncOwingExVouchers > absCredit
    ? absCredit
    : decimalMinus(absCredit, decimalMinus(absCredit, totalIncOwingExVouchers));

  const previousInvoices = pricesOnly ? {} : [...summary.previousOwing.invoices, ...summary.previousCredit.invoices]
    .filter(i => i.checked)
    .reduce((p, c) => {
      const amount = c.amountOwing;
      const absAmount = Math.abs(amount);

      const pAmount = amount > 0
        ? (invoicesCover > amount ? amount : invoicesCover)
        : (appliedCredit > absAmount ? amount : -appliedCredit);

      if (pAmount !== 0) {
        p[c.id] = pAmount;
      }

      if (amount > 0) {
        if (invoicesCover > amount) {
          invoicesCover = decimalMinus(invoicesCover, amount);
        } else {
          invoicesCover = 0;
        }
      }

      if (amount < 0 && appliedCredit > 0) {
        appliedCredit = decimalMinus(appliedCredit, absAmount);
        if (appliedCredit < 0) {
          appliedCredit = 0;
        }
      }

      return p;
    }, {});

  const paymentType = payment.availablePaymentTypes.find(t => t.name === payment.selectedPaymentType);

  const redeemedVouchers = pricesOnly ? {} : summary.vouchers.reduce((p, v) => {
    p[v.id] = v.appliedValue;
    return p;
  }, {});

  return {
    payerId: payerItem ? payerItem.contact.id : null,

    paymentMethodId: paymentType && !pricesOnly
      ? paymentType.id
      : null,

    payNow: pricesOnly ? 0 : summary.payNowTotal,

    paymentDate: summary.paymentDate,

    paymentPlans,

    merchantReference: payment.merchantReference,

    contactNodes: summary.list.map((l): ContactNode => ({

      contactId: l.contact.id,

      enrolments: l.items.filter(i => i.checked && i.type === "course")
        .map((i): CheckoutEnrolment =>
          ({
            classId: i.class.id,
            appliedDiscountId: i.discount ? i.discount.id : null,
            studyReason: i.studyReason,
            totalOverride: i.priceOverriden,
            appliedVoucherId: i.voucherId,
          })),

      memberships: l.items.filter(i => i.checked && i.type === "membership")
        .map(({ id, validTo }): CheckoutMembership => ({
          productId: id, validTo
        })),

      vouchers: l.payer ? vouchers : [],

      products: l.items.filter(i => i.checked && i.type === "product").map((i): CheckoutArticle => ({
        productId: i.id,
        quantity: i.quantity
      })),

      sendConfirmation: l.sendEmail,

      fundingInvoices: fundingInvoices && getFundingInvoices(
        fundingInvoices.filter(fi => fi.trackAmountOwing && fi.item.enrolment.contact.id === l.contact.id)
      )
    })),

    sendInvoice: payerItem && payerItem.sendInvoice,

    previousInvoices,

    redeemedVouchers,

    allowAutoPay: summary.allowAutoPay,

    payWithSavedCard: pricesOnly ? false : payment.selectedPaymentType === "Saved credit card",

    payForThisInvoice,

    invoiceDueDate: (paymentPlans.length
      && paymentPlans.reduce((p, c) => (new Date(p.date) < new Date(c.date) ? p : c))?.date)
      || summary.invoiceDueDate,

    invoiceCustomerReference: summaryValues.invoiceCustomerReference,

    invoicePublicNotes: summaryValues.invoicePublicNotes
  };
};

export const getInvoiceLineKey = (entity: CheckoutEntity) => {
  switch (entity) {
    case "course":
      return "enrolment";
    case "product":
      return "article";
    default:
      return entity;
  }
};

const getInvoiceLinePrices = (item: CheckoutItem, lines: InvoiceInvoiceLine[], itemOriginal: CheckoutItem) => {
  const id = item.type === "course" ? item.class.id : item.id;
  const lineKey = getInvoiceLineKey(item.type);
  const targetLine = lines.find(l => l[lineKey] && (l[lineKey].productId === id || l[lineKey].classId === id));

  if (!targetLine) {
    return item;
  }

  const paymentPlansObj: any = {};

  if (item.type === "course" && itemOriginal.paymentPlans && itemOriginal.paymentPlans.length) {
    if (targetLine.discountEachExTax === 0) {
      paymentPlansObj.paymentPlans = [...itemOriginal.paymentPlans];
    } else {
      let discountedPrice = targetLine.finalPriceToPayIncTax;

      const paymentPlans = [];

      itemOriginal.paymentPlans.forEach(p => {
        if (p.date && discountedPrice > 0) {
          paymentPlans.push({
            ...p,
            amount: discountedPrice > p.amount ? p.amount : discountedPrice
          });
        }
        discountedPrice = decimalMinus(discountedPrice, p.amount);
      });

      paymentPlansObj.paymentPlans = paymentPlans;
    }
  }

  const prices = targetLine ? {
    price: targetLine.finalPriceToPayIncTax,
    ...item.type === "course" ? {
      priceExTax: targetLine.priceEachExTax,
      discountExTax: targetLine.discountEachExTax,
      taxAmount: targetLine.taxEach
    } : {}
  } : {
    price: 0
  };

  return {
    ...item,
    ...paymentPlansObj,
    ...prices
  };
};

export const getUpdatedSummaryItem = (li: CheckoutSummaryListItem, itemOriginals: CheckoutItem[], invoice: Invoice) => {
  if (!invoice) {
    return {
      ...li,
      itemTotal: 0
    };
  }

  const itemLines = invoice.invoiceLines.filter(l => l.contactId === li.contact.id);

  return {
    ...li,
    itemTotal: itemLines.reduce((p, c) => decimalPlus(p, c.finalPriceToPayIncTax), 0),
    items: li.items.map(item => ({
      ...getInvoiceLinePrices(
        item,
        itemLines,
        itemOriginals.find(or => or.id === item.id && or.type === item.type)
      )
    }))
  };
};

export const getUpdatedSummaryVouchers = (vouchers: CheckoutItem[], invoice: Invoice) => {
  if (!invoice) {
    return vouchers;
  }

  const voucherLines = invoice.invoiceLines.filter(l => l.voucher);

  if (voucherLines.length) {
    return vouchers.map(v => {
      const targetline = voucherLines.find(vl => vl.voucher.productId === v.id);

      return {
        ...v,
        price: targetline ? targetline.finalPriceToPayIncTax : v.price
      };
    });
  }
  return vouchers;
};

export const getUpdatedVoucherDiscounts = (
  classId: number,
  list: CheckoutSummaryListItem[],
  vouchers: CheckoutDiscount[]
): CheckoutDiscount[] => {
  const classesWithVouchers = [];
  let updated = vouchers;

  list.forEach(l => {
    l.items.forEach(li => {
      if (li.voucherId !== null && li.id === classId) {
        classesWithVouchers.push(li);
      }
    });
  });

  if (classesWithVouchers.length) {
    updated = vouchers.map(v => {
      if (classesWithVouchers.some(c => c.voucherId === v.id)) {
        return { ...v, appliedValue: decimalMinus(v.appliedValue, classesWithVouchers.reduce((p, c) => decimalPlus(p, c.price), 0)) };
      }
      return v;
    });
  }

  return updated;
};

export const checkoutCourseClassMap = ({ id, values }): CheckoutCourseClass => {
  const cc = {
    id: Number(id),
    name: `${values[1]}-${values[2]} ${values[0]}`,
    courseName: values[0],
    courseCode: values[1],
    code: values[2],
    price: Number(values[3]),
    startDateTime: values[4],
    endDateTime: values[5],
    placesLeft: JSON.parse(values[6]),
    room: values[7],
    site: values[8],
    siteTimezone: values[9],
    tutors: values[10],
    sessionStartDates: values[11],
    sessionEndDates: values[12],
    hours: 0,
    hasPaymentPlans: Boolean(JSON.parse(values[13]).length),
    isVet: JSON.parse(values[14]),
    isSelfPaced: JSON.parse(values[15]),
    message: values[16],
    relatedFundingSourceId: JSON.parse(values[17]),
    sessionIds: JSON.parse(values[18]),
    fundingProviderId: values[19] ? Number(values[19]) : null,
    vetPurchasingContractID: values[20]
  };

  const strTutorNames = cc.tutors;
  const subStr = strTutorNames.substring(1, strTutorNames.length - 1);
  const tutorsArray = subStr.substring(1, subStr.indexOf("]")).split(", ");
  if (tutorsArray[0].length <= 0) {
    cc.tutors = [];
  } else {
    cc.tutors = [...tutorsArray];
  }

  const { sessionStartDates, sessionEndDates } = cc;
  const sessionStartDatesArray = sessionStartDates.substring(1, sessionStartDates.length - 1).split(", ");
  const sessionEndDatesArray = sessionEndDates.substring(1, sessionEndDates.length - 1).split(", ");
  if (sessionEndDatesArray[0].length > 0) {
    sessionEndDatesArray.forEach((sessionEndDate, i) => {
      cc.hours += differenceInMinutes(new Date(sessionEndDate), new Date(sessionStartDatesArray[i]));
    });
  } else {
    cc.hours = 0;
  }

  return cc;
};

export const checkoutCourseMap = (courseBase, skipCheck?: boolean): CheckoutCourse => {
  const course: CheckoutCourse = { ...courseBase };
  course.courseId = courseBase.id;
  course.type = "course";
  course.checked = !skipCheck;
  course.class = null;
  course.price = 0;
  course.discount = null;
  course.studyReason = "Not stated";
  course.quantity = 1;
  course.discountExTax = 0;
  course.taxAmount = 0;
  course.tax = 0;

  return course;
};

export const calculateVoucherOrMembershipExpiry = (item: CheckoutItem) => {
  switch (item.type) {
    case "voucher": {
      if (item.expiryDays) {
        const today = new Date();
        today.setDate(today.getDate() + Number(item.expiryDays));
        item.validTo = format(today, YYYY_MM_DD_MINUSED);
      }
      break;
    }
    case "membership": {
      if (item.expiryType === "Never (Lifetime)") {
        item.expireNever = item.expiryType;
      } else {
        if (item.expiryType === "Days") {
          const today = new Date();
          today.setDate(today.getDate() + Number(item.expiryDays));
          item.validTo = format(today, YYYY_MM_DD_MINUSED);
        }
        if (item.expiryType === "1st July") {
          const date = getExpireDate(6);
          item.validTo = format(date, YYYY_MM_DD_MINUSED);
        }
        if (item.expiryType === "1st January") {
          const date = getExpireDate(0);
          item.validTo = format(date, YYYY_MM_DD_MINUSED);
        }
      }
    }
  }
};

export const processCheckoutSale = (row, type) => {
  if (typeof row.price === "string") {
    row.price = parseFloat(row.price);
  }
  row.type = type;
  row.checked = true;
  row.quantity = 1;
  row.originalPrice = row.price;
  if ( type === "voucher") {
    row.restrictToPayer = false;
  }
  calculateVoucherOrMembershipExpiry(row);
};

export const getCheckoutCurrentStep = (step: CheckoutCurrentStepType): number => {
  switch (step) {
    case CheckoutCurrentStep.shoppingCart:
      return 0;
    case CheckoutCurrentStep.summary:
      return 1;
    case CheckoutCurrentStep.fundingInvoice:
      return 2;
    case CheckoutCurrentStep.payment:
      return 3;
    default:
      return -1;
  }
};

export * from "./asyncActions";

