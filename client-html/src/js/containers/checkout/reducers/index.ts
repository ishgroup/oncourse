/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../common/actions/IshAction";
import { decimalPlus } from "../../../common/utils/numbers/decimalCalculation";
import { CheckoutDiscount, CheckoutItem, CheckoutState } from "../../../model/checkout";
import { getContactName } from "../../entities/contacts/utils";
import {
  CHECKOUT_ADD_CONTACT,
  CHECKOUT_ADD_ITEM, CHECKOUT_ADD_ENROLMENTS,
  CHECKOUT_CHANGE_STEP,
  CHECKOUT_CLEAR_STATE,
  CHECKOUT_REMOVE_CONTACT,
  CHECKOUT_REMOVE_ITEM, CHECKOUT_SET_HAS_ERRORS,
  CHECKOUT_SUMMARY_SEND_CONTEXT,
  CHECKOUT_TOGGLE_SUMMARY_ITEM,
  CHECKOUT_TOGGLE_SUMMARY_VOUCHER_ITEM,
  CHECKOUT_UPDATE_CLASS_ITEM,
  CHECKOUT_UPDATE_CONTACT, CHECKOUT_UPDATE_RELATED_ITEMS
} from "../actions";
import {
  listPreviousInvoices,
  setSummaryListWithDefaultPayer,
  modifySummaryLisItem,
  getUpdatedSummaryItem,
  getUpdatedSummaryVouchers,
  getUpdatedVoucherDiscounts
} from "../utils";
import {
  CHECKOUT_CLEAR_CONTACT_EDIT_RECORD,
  CHECKOUT_GET_CONTACT_FULFILLED,
  CHECKOUT_GET_RELATED_CONTACT_FULFILLED,
  CHECKOUT_UPDATE_CONTACT_RELATIONS
} from "../actions/checkoutContact";
import {
  CHECKOUT_CLEAR_COURSE_CLASS_LIST,
  CHECKOUT_GET_COURSE_CLASS_LIST_FULFILLED,
  CHECKOUT_GET_ITEM_MEMBERSHIP_FULFILLED,
  CHECKOUT_GET_ITEM_PRODUCT_FULFILLED,
  CHECKOUT_GET_ITEM_VOUCHER_FULFILLED,
  CLEAR_CHECKOUT_ITEM_RECORD
} from "../actions/chekoutItem";
import {
  CHECKOUT_CLEAR_CC_IFRAME_URL,
  CHECKOUT_GET_ACTIVE_PAYMENT_TYPES_FULFILLED,
  CHECKOUT_PROCESS_CC_PAYMENT_FULFILLED,
  CHECKOUT_SET_PAYMENT_SET_STATUS,
  CHECKOUT_SET_PAYMENT_PROCESSING,
  CHECKOUT_SET_PAYMENT_SUCCESS,
  CHECKOUT_SET_PAYMENT_TYPE,
  CHECKOUT_CLEAR_PAYMENT_STATUS,
  CHECKOUT_SET_PAYMENT_STATUS,
  CHECKOUT_SET_PAYMENT_DETAILS_FETCHING,
  CHECKOUT_PROCESS_CC_PAYMENT,
  CHECKOUT_GET_PAYMENT_STATUS_DETAILS,
  CHECKOUT_SET_PAYMENT_STATUS_DETAILS,
  CHECKOUT_GET_SAVED_CARD_FULFILLED
} from "../actions/checkoutPayment";
import {
  CHECKOUT_SET_PROMO,
  CHECKOUT_SET_PREVIOUS_CREDIT,
  CHECKOUT_SET_PREVIOUS_OWING,
  CHECKOUT_REMOVE_VOUCHER_PROMO,
  CHECKOUT_REMOVE_PROMOTIONAL_CODE,
  CHECKOUT_TOGGLE_PREVIOUS_INVOICES,
  CHECKOUT_UNCHECK_ALL_PREVIOUS_INVOICES,
  CHECKOUT_SET_DEFAULT_PAYER,
  CHECKOUT_CHANGE_SUMMARY_ITEM_QUANTITY,
  CHECKOUT_CHANGE_SUMMARY_ITEM_FIELD,
  CHECKOUT_UPDATE_SUMMARY_ITEM,
  CHECKOUT_UPDATE_SUMMARY_FIELD,
  CHECKOUT_UPDATE_SUMMARY_PRICES_FULFILLED,
  CHECKOUT_UNCHECK_SUMMARY_ITEMS,
  CHECKOUT_UPDATE_SUMMARY_ITEMS,
  CHECKOUT_UPDATE_PROMO, CHECKOUT_UPDATE_SUMMARY_LIST_ITEMS, CHECKOUT_SET_DISABLE_DISCOUNTS
} from "../actions/checkoutSummary";

const initial: CheckoutState = {
  step: 0,
  contacts: [],
  items: [],
  summary: {
    list: [],
    finalTotal: 0,
    payNowTotal: 0,
    discounts: [],
    vouchers: [],
    allowAutoPay: false,
    previousCredit: {
      invoices: [],
      invoiceTotal: 0,
      unCheckAll: false
    },
    previousOwing: {
      invoices: [],
      invoiceTotal: 0,
      unCheckAll: false
    },
    voucherItems: [],
    invoiceDueDate: null
  },
  contactEditRecord: null,
  itemEditRecord: null,
  payment: {
    invoice: null,
    paymentId: null,
    availablePaymentTypes: [],
    selectedPaymentType: null,
    isProcessing: false,
    isFetchingDetails: false,
    isSuccess: false,
    wcIframeUrl: "",
    xPaymentSessionId: "",
    merchantReference: "",
    process: {
      status: null,
      statusCode: null,
      statusText: null,
      data: null
    },
    savedCreditCard: null
  },
  relatedContacts: [],
  courseClasses: [],
  checkCourseClassEmpty: false,
  hasErrors: false,
  disableDiscounts: false,
  salesRelations: []
};

export const checkoutReducer = (state: CheckoutState = initial, action: IAction): CheckoutState => {
  switch (action.type) {
    case CHECKOUT_UPDATE_SUMMARY_PRICES_FULFILLED: {
      const { invoice } = action.payload;

      return {
        ...state,
        hasErrors: false,
        summary: {
          ...state.summary,
          voucherItems: getUpdatedSummaryVouchers(state.summary.voucherItems, invoice),
          finalTotal: invoice ? invoice.amountOwing : 0,
          list: state.summary.list.map(li => getUpdatedSummaryItem(li, state.items, invoice))
        }
      };
    }

    case CHECKOUT_ADD_CONTACT: {
      const { contact, isPayer, checkItems = true } = action.payload;
      const contacts = [...state.contacts, { ...contact }];

      const items = [...state.items].filter(i => !(contact.isCompany && i.type === "course") && i.type !== "voucher");

      if (!checkItems) {
        items.forEach(i => {
          i.checked = false;
        });
      }

      const hasEmail = Boolean(contact.email);

      const summary = {
          ...state.summary,
          list: setSummaryListWithDefaultPayer([...state.summary.list, {
            contact,
            items,
            itemTotal: 0,
            sendInvoice: hasEmail && state.summary.list.length === 0,
            sendEmail: hasEmail,
            payer: false
          }], isPayer ? state.summary.list.length : 0)
        };

      return {
        ...state,
        summary,
        contacts
      };
    }

    case CHECKOUT_REMOVE_CONTACT: {
      const contacts = [...state.contacts];
      contacts.splice(action.payload.contactIndex, 1);

      const summaryList = [...state.summary.list];
      const isDefaultPayer = summaryList[action.payload.contactIndex].payer;
      summaryList.splice(action.payload.contactIndex, 1);

      const summary = {
        ...state.summary,
        list: isDefaultPayer ? setSummaryListWithDefaultPayer([...summaryList]) : [...summaryList]
      };

      return {
        ...state,
        summary,
        contacts,
        relatedContacts: contacts.length > 0 ? [...state.relatedContacts] : []
      };
    }

    case CHECKOUT_UPDATE_CONTACT: {
      const contacts = [...state.contacts];
      const findIndex = contacts.findIndex(c => c.id === action.payload.id);
      const contact = action.payload.contact;
      contacts[findIndex] = contact;

      const list = [...state.summary.list];
      const findSummaryContactIndex = list.findIndex(c => c.contact.id === action.payload.id);
      list[findSummaryContactIndex].contact = contact;

      return {
        ...state,
        summary: {
          ...state.summary,
          list
        },
        contacts
      };
    }

    case CHECKOUT_ADD_ITEM: {
      const { item } = action.payload;
      const items = [...state.items, { ...item }];
      let list = state.summary.list;
      let voucherItems = [...state.summary.voucherItems];

      if (list.length > 0 && item.type !== "voucher") {
        list = list.map(li => {
          if (item.type === "course" && li.contact.isCompany) {
            return li;
          }
          return {
            ...li,
            items: [...li.items, item]
          };
        });
      }

      if (item.type === "voucher") {
        voucherItems = [...voucherItems, { ...item, price: item.price === null ? 100 : item.price }];
      }

      const summary = {
        ...state.summary,
        list,
        voucherItems
      };

      return {
        ...state,
        summary,
        items
      };
    }

    case CHECKOUT_UPDATE_CLASS_ITEM: {
      const items = [...state.items];
      const findIndex = items.findIndex(i => i.id === action.payload.item.id);

      const changedItem = { ...action.payload.item, class: { ...action.payload.item.class } };

      if (findIndex !== -1) {
        items[findIndex] = changedItem;
      }

      return {
        ...state,
        summary: {
          ...state.summary,
          list: state.summary.list.map(li => ({
            ...li,
            items: li.items.map(i =>
              (i.id === changedItem.id
                ? { ...changedItem }
                : i))
          }))
        },
        items
      };
    }

    case CHECKOUT_UPDATE_SUMMARY_FIELD: {
      const { field, value } = action.payload;

      return {
        ...state,
        summary: {
          ...state.summary,
          [field]: value
        }
      };
    }

    case CHECKOUT_UNCHECK_SUMMARY_ITEMS: {
      const items = action.payload;

      return {
        ...state,
        summary: {
          ...state.summary,
          list: state.summary.list.map(li => ({
            ...li,
            items: li.items.map(item => ({
              ...item,
              checked: items.some(pi => pi.nodeId === li.contact.id
                && (pi.itemId === item.id
                  || (item.class && item.class.id === pi.itemId)))
                ? false
                : item.checked
            }))
          }))
        },
      };
    }

    case CHECKOUT_UPDATE_SUMMARY_LIST_ITEMS: {
      const { items } = action.payload;

      return {
        ...state,
        summary: {
          ...state.summary,
          list: state.summary.list.map((li, listIndex) => {
            const changed = items.find(it => it.listIndex === listIndex);

            if (changed) {
              return changed.item;
            }
            return li;
          })
        }
      };
    }

    case CHECKOUT_UPDATE_SUMMARY_ITEMS: {
      const { items } = action.payload;

      return {
        ...state,
        summary: {
          ...state.summary,
          list: state.summary.list.map((li, listIndex) => ({
            ...li,
            items: li.items.map((l, itemIndex) => {
              const changedItem = items.find(it => it.listIndex === listIndex && it.itemIndex === itemIndex);
              if (changedItem && l.id === changedItem.item.id) {
                return changedItem.item;
              }
              return l;
            })
          }))
        }
      };
    }

    case CHECKOUT_UPDATE_SUMMARY_ITEM: {
      const { listIndex, item } = action.payload;
      let { voucherItems } = state.summary;
      let disableDiscounts = state.disableDiscounts;

      const changedItem: CheckoutItem = { ...item };

      if (changedItem.type === "course") {
        changedItem.class = { ...action.payload.item.class };

        if (changedItem.discount && disableDiscounts) {
          disableDiscounts = false;
        }
      }

      if (changedItem.type === "voucher" && voucherItems.length) {
        voucherItems = voucherItems.map(v => {
          if (v.id === changedItem.id) {
            return changedItem;
          }

          return v;
        });
      }

      let list = [...state.summary.list];

      if (list.length > 0) {
        list = list.map((li, lIndex) => ({
          ...li,
          items: li.items.map(l => {
            if ((typeof listIndex !== "number" || lIndex === listIndex) && l.id === changedItem.id) {
              return changedItem;
            }
            return l;
          })
        }));
      }

      const summary = {
        ...state.summary,
        list,
        voucherItems
      };

      return {
        ...state,
        disableDiscounts,
        summary
      };
    }

    case CHECKOUT_REMOVE_ITEM: {
      const items = state.items.filter(i => !(i.id === action.payload.itemId && i.type === action.payload.itemType));
      let list = state.summary.list;
      let vouchers = state.summary.vouchers;
      let voucherItems = state.summary.voucherItems;

      if (list.length > 0 && action.payload.itemType !== "voucher") {
        list = list.map(l => ({
          ...l,
          items: l.items.filter(item => !(item.id === action.payload.itemId && item.type === action.payload.itemType))
        }));
      }

      const prices: any = {};

      if (!items.length) {
        prices.finalTotal = 0;
        list.forEach(l => {
          l.itemTotal = 0;
        });
      }

      if (action.payload.itemType === "voucher") {
        voucherItems = voucherItems.filter(vi => !(vi.id === action.payload.itemId && vi.type === action.payload.itemType));
      }

      if (action.payload.itemType === "course") {
        vouchers = getUpdatedVoucherDiscounts(action.payload.itemId, list, vouchers);
      }

      const summary = {
        ...state.summary,
        ...prices,
        voucherItems,
        vouchers,
        list
      };

      return {
        ...state,
        summary,
        items
      };
    }

    case CHECKOUT_CHANGE_STEP: {
      return {
        ...state,
        step: action.payload.step
      };
    }

    case CHECKOUT_TOGGLE_SUMMARY_ITEM: {
      const list = modifySummaryLisItem(state.summary.list, action.payload, true);
      const modifiedId = list[action.payload.listIndex].items[action.payload.itemIndex].id;

      const summary = {
        ...state.summary,
        vouchers: getUpdatedVoucherDiscounts(modifiedId, state.summary.list, state.summary.vouchers),
        list
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_GET_CONTACT_FULFILLED: {
      const { editRecord } = action.payload;
      return {
        ...state,
        contactEditRecord: editRecord
      };
    }

    case CHECKOUT_CLEAR_CONTACT_EDIT_RECORD: {
      return {
        ...state,
        contactEditRecord: null
      };
    }

    case CHECKOUT_GET_ITEM_VOUCHER_FULFILLED:
    case CHECKOUT_GET_ITEM_PRODUCT_FULFILLED:
    case CHECKOUT_GET_ITEM_MEMBERSHIP_FULFILLED: {
      const { editRecord } = action.payload;
      return {
        ...state,
        itemEditRecord: editRecord
      };
    }

    case CLEAR_CHECKOUT_ITEM_RECORD: {
      return {
        ...state,
        itemEditRecord: []
      };
    }

    case CHECKOUT_GET_SAVED_CARD_FULFILLED: {
      const { savedCreditCard } = action.payload;

      return {
        ...state,
        payment: {
          ...state.payment,
          savedCreditCard
        }
      };
    }

    case CHECKOUT_GET_ACTIVE_PAYMENT_TYPES_FULFILLED: {
      return {
        ...state,
        payment: {
          ...state.payment,
          availablePaymentTypes: action.payload.paymentTypes
        }
      };
    }

    case CHECKOUT_SET_PAYMENT_TYPE: {
      return {
        ...state,
        payment: {
          ...state.payment,
          selectedPaymentType: action.payload.selectedType
        }
      };
    }

    case CHECKOUT_SET_PAYMENT_PROCESSING: {
      return {
        ...state,
        payment: {
          ...state.payment,
          isProcessing: action.payload.isProcessing
        }
      };
    }

    case CHECKOUT_SET_PAYMENT_DETAILS_FETCHING: {
      return {
        ...state,
        payment: {
          ...state.payment,
          isFetchingDetails: action.payload
        }
      };
    }
    case CHECKOUT_SET_PAYMENT_SUCCESS: {
      return {
        ...state,
        payment: {
          ...state.payment,
          isSuccess: action.payload.isSuccess
        }
      };
    }

    case CHECKOUT_UPDATE_PROMO: {
      const { discountItem, vouchersItem } = action.payload;

      let discounts = [...state.summary.discounts];
      let vouchers = [...state.summary.vouchers];

      if (discountItem) {
        discounts = state.summary.discounts.map(d => ({
          ...d.id === discountItem.id ? discountItem : d
        }));
      }

      if (vouchersItem) {
        vouchers = state.summary.vouchers.map(d => ({
          ...d.id === vouchersItem.id ? vouchersItem : d
        }));
      }

      const summary = {
        ...state.summary,
        discounts: [...discounts],
        vouchers: [...vouchers]
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_SET_PROMO: {
      const { discountItem, vouchersItem }: { discountItem?: CheckoutDiscount, vouchersItem?: CheckoutDiscount } = action.payload;

      let discounts = [...state.summary.discounts];
      let vouchers = [...state.summary.vouchers];

      if (discountItem) {
        discounts = [...state.summary.discounts, { ...discountItem }];
      }

      let list = [...state.summary.list];

      if (vouchersItem) {
        if (vouchersItem.courseIds && vouchersItem.courseIds.length) {
          let enrolments = [];

          list.forEach(l => {
            l.items.forEach(li => {
              if (li.checked && li.type === "course" && !li.voucherId && vouchersItem.courseIds.includes(li.courseId)) {
                enrolments.push(li);
              }
            });
          });

          let counter = vouchersItem.maxCoursesRedemption - vouchersItem.redeemedCourses;

          enrolments.sort((a, b) => (a.price > b.price ? -1 : 1));

          enrolments = enrolments.reduce((p, c) => {
            if (counter > 0) {
              counter--;
              vouchersItem.appliedValue = decimalPlus(vouchersItem.appliedValue, c.price);
              p[c.class.id] = true;
            }
            return p;
          }, {});

          list = list.map(l => ({
            ...l,
            items: l.items.map(li => ({
              ...li,
              ...li.type === "course" ? { voucherId: enrolments[li.class.id] ? vouchersItem.id : li.voucherId } : {}
            }))
          }));
        }

        vouchers = [...state.summary.vouchers, { ...vouchersItem }];
      }

      const summary = {
        ...state.summary,
        discounts: [...discounts],
        vouchers: [...vouchers],
        list
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_REMOVE_VOUCHER_PROMO: {
      const vouchers = [...state.summary.vouchers];
      const { voucherIndex } = action.payload;

      const removedVoucher = vouchers[voucherIndex];

      vouchers.splice(voucherIndex, 1);

      let list = state.summary.list;

      if (removedVoucher.courseIds.length) {
        list = list.map(l => ({
          ...l,
          items: l.items.map(li => ({
            ...li,
            ...li.type === "course" && li.voucherId === removedVoucher.id ? { voucherId: null } : {}
          }))
        }));
      }

      const summary = {
        ...state.summary,
        vouchers,
        list
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_REMOVE_PROMOTIONAL_CODE: {
      const { discountIndex } = action.payload;
      const discounts = [...state.summary.discounts];

      discounts.splice(discountIndex, 1);

      const summary = {
        ...state.summary,
        discounts: [...discounts]
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_SET_PREVIOUS_CREDIT: {
      let invoiceTotal = 0;
      const invoices = [...action.payload.items];

      const payer = state.summary.list.find(i => i.payer);

      invoices.forEach(item => {
        item.checked = !payer.contact.defaultSelectedOwing;
        if (item.checked) {
          invoiceTotal = decimalPlus(invoiceTotal, parseFloat(item.amountOwing));
        }
      });

      const summary = {
        ...state.summary,
        previousCredit: { invoices, invoiceTotal }
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_SET_PREVIOUS_OWING: {
      let invoiceTotal = 0;
      const invoices = [...action.payload.items];

      const payer = state.summary.list.find(i => i.payer);

      const today = new Date();

      invoices.forEach(item => {
        item.checked = payer.contact.defaultSelectedOwing
          ? payer.contact.defaultSelectedOwing === item.id
          : !(item.dateDue && (new Date(item.dateDue) > today));

        if (item.checked) {
          invoiceTotal = decimalPlus(invoiceTotal, parseFloat(item.amountOwing));
        }
      });

      const summary = {
        ...state.summary,
        previousOwing: { invoices, invoiceTotal }
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_TOGGLE_PREVIOUS_INVOICES: {
      const { itemIndex, type } = action.payload;

      const previousInvoiceList = listPreviousInvoices({
        summary: state.summary, itemIndex, type, isUnCheckAll: false
      });

      const summary = {
        ...state.summary,
        ...previousInvoiceList
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_UNCHECK_ALL_PREVIOUS_INVOICES: {
      const { type, value } = action.payload;

      const previousInvoiceList = listPreviousInvoices({
        summary: { ...state.summary, [type]: { ...state.summary[type], unCheckAll: value } }, type, isUnCheckAll: true, itemIndex: undefined
      });

      const summary = {
        ...state.summary,
        ...previousInvoiceList
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_SET_DEFAULT_PAYER: {
      const { payerIndex } = action.payload;

      const list = setSummaryListWithDefaultPayer([...state.summary.list], payerIndex);

      const summary = {
        ...state.summary,
        list
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_GET_PAYMENT_STATUS_DETAILS: {
      return {
        ...state,
        payment: {
          ...state.payment,
          isFetchingDetails: true
        }
      };
    }

    case CHECKOUT_PROCESS_CC_PAYMENT: {
      return {
        ...state,
        payment: {
          ...state.payment,
          isProcessing: true
        }
      };
    }

    case CHECKOUT_PROCESS_CC_PAYMENT_FULFILLED: {
      const {
        sessionId, ccFormUrl, merchantReference, invoice, paymentId
      } = action.payload;

      return {
        ...state,
        payment: {
          ...state.payment,
          xPaymentSessionId: sessionId,
          wcIframeUrl: ccFormUrl,
          merchantReference,
          invoice,
          paymentId
        }
      };
    }

    case CHECKOUT_CLEAR_CC_IFRAME_URL: {
      return {
        ...state,
        payment: {
          ...state.payment,
          wcIframeUrl: ""
        }
      };
    }

    case CHECKOUT_SET_PAYMENT_SET_STATUS: {
      const {
        status, statusCode, statusText, data
      } = action.payload;

      return {
        ...state,
        payment: {
          ...state.payment,
          process: {
            statusCode, status, statusText, data
          }
        }
      };
    }

    case CHECKOUT_CLEAR_PAYMENT_STATUS: {
      return {
        ...state,
        payment: {
          ...state.payment,
          process: {
            ...state.payment.process,
            status: null,
            data: null
          }
        }
      };
    }

    case CHECKOUT_SET_PAYMENT_STATUS: {
      const { status } = action.payload;

      return {
        ...state,
        payment: {
          ...state.payment,
          process: {
            ...state.payment.process,
            status
          }
        }
      };
    }

    case CHECKOUT_SET_PAYMENT_STATUS_DETAILS: {
      const { data } = action.payload;

      return {
        ...state,
        payment: {
          ...state.payment,
          process: {
            ...state.payment.process,
            data
          }
        }
      };
    }

    case CHECKOUT_CHANGE_SUMMARY_ITEM_QUANTITY: {
      const itemData = {
        listIndex: action.payload.listIndex,
        itemIndex: action.payload.itemIndex,
        value: { quantity: action.payload.quantity }
      };

      const list = modifySummaryLisItem(state.summary.list, itemData);

      const summary = {
        ...state.summary,
        list
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_CHANGE_SUMMARY_ITEM_FIELD: {
      const itemData = {
        listIndex: action.payload.listIndex,
        itemIndex: action.payload.itemIndex,
        value: { [action.payload.field]: action.payload.value }
      };

      const list = modifySummaryLisItem(state.summary.list, itemData);

      const summary = {
        ...state.summary,
        list
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_UPDATE_CONTACT_RELATIONS: {
      const contacts = [...state.contacts];
      const findIndex = contacts.findIndex(c => c.id === action.payload.id);
      const contactStringified = JSON.stringify(contacts[findIndex]);
      let contact = contactStringified ? JSON.parse(contactStringified) : undefined;

      if (contact) {
        contact = { ...contact, relations: [...action.payload.relations] };
        contacts[findIndex] = contact;
      }

      return {
        ...state,
        contacts
      };
    }

    case CHECKOUT_GET_RELATED_CONTACT_FULFILLED: {
      let relatedContacts = [...action.payload.relatedContacts];
      const relationTypes = [...action.payload.relationTypes];

      if (relatedContacts.length) {
        relatedContacts = relatedContacts.map(rc => {
          state.contacts.forEach(c => {
            const conRel = c.relations && c.relations.find(r => r.relatedContactId === rc.id);
            if (conRel) {
              const isReverseRelation = String(conRel.relationId).includes("r");
              const filteredRelations = relationTypes.filter(r => r.isReverseRelation === !isReverseRelation);
              const relation = filteredRelations.find(r => r.value === String(parseInt(conRel.relationId, 10)));
              const relationName = relation && relation.label;
              const relatedContactName = getContactName(c);
              rc.relationString = relationName && relatedContactName ? `${relationName} of ${relatedContactName}` : "";
            }
          });
          return rc;
        });
      }

      return {
        ...state,
        relatedContacts
      };
    }

    case CHECKOUT_GET_COURSE_CLASS_LIST_FULFILLED: {
      const courseClasses = [...action.payload.courseClasses];
      return {
        ...state,
        courseClasses,
        checkCourseClassEmpty: courseClasses.length <= 0
      };
    }

    case CHECKOUT_CLEAR_COURSE_CLASS_LIST: {
      return {
        ...state,
        courseClasses: [],
        checkCourseClassEmpty: false
      };
    }

    case CHECKOUT_TOGGLE_SUMMARY_VOUCHER_ITEM: {
      const voucherItems = [...state.summary.voucherItems];

      voucherItems.forEach((vi, i) => {
        if (i === action.payload.itemIndex) {
          vi.checked = !vi.checked;
        }
      });

      const summary = {
        ...state.summary,
        voucherItems
      };

      return {
        ...state,
        summary
      };
    }

    case CHECKOUT_SUMMARY_SEND_CONTEXT: {
      const { listIndex, type } = action.payload;
      const list = state.summary.list.map((l, i) => {
        if (i === listIndex) {
          if (type === "invoice") l.sendInvoice = !l.sendInvoice;
          else if (type === "email") l.sendEmail = !l.sendEmail;
        }
        return l;
      });

      return {
        ...state,
        summary: {
          ...state.summary,
          list
        }
      };
    }

    case CHECKOUT_ADD_ENROLMENTS: {
      const enrolments = action.payload;

      const addedIds = {};

      const items = [];

      enrolments.forEach(e => {
        if (e.courseClass && !addedIds[e.courseClass.id]) {
          items.push(e.courseClass);
          addedIds[e.courseClass.id] = true;
        }
      });

      return {
        ...state,
        items,
        summary: {
          ...state.summary,
          list: state.summary.list.map(li => ({
            ...li,
            items: items.map(item => ({
              ...item,
              checked: Boolean(enrolments.find(en => en.contactId === li.contact.id && en.courseClass && en.courseClass.id === item.id))
            }))
          }))
        }
      };
    }

    case CHECKOUT_SET_HAS_ERRORS: {
      return {
        ...state,
        hasErrors: action.payload
      };
    }

    case CHECKOUT_SET_DISABLE_DISCOUNTS: {
      const disableDiscounts = action.payload;

      let list = state.summary.list;

      if (disableDiscounts) {
        list = list.map(l => ({
          ...l,
          items: l.items.map(li => ({
            ...li,
            ...li.type === "course" ? { discount: null } : {}
          }))
        }));
      }

      return {
        ...state,
        summary: {
          ...state.summary,
          list
        },
        disableDiscounts
      };
    }

    case CHECKOUT_CLEAR_STATE: {
      return { ...initial };
    }

    case CHECKOUT_UPDATE_RELATED_ITEMS: {
      const { cartItems, suggestItems } = action.payload;

      if (!cartItems.length && !suggestItems.length) {
        return state;
      }

      const items = [...state.items];
      const voucherItems = [...state.summary.voucherItems];

      cartItems.forEach(c => {
        items.push(c.toItem.cartItem);
        if (c.toItem.type === "voucher") {
          voucherItems.push(c.toItem.cartItem);
        }
      });

      return {
        ...state,
        items,
        summary: {
          ...state.summary,
          voucherItems,
          list: state.summary.list.map(li => ({
            ...li,
            items: items.filter(fi => fi.type !== "voucher").map(it => {
              if (it.type === "course" && li.contact.isCompany) {
                return null;
              }
              const cartItem = cartItems.find(ci => ci.contactIds.includes(li.contact.id));
              return { ...it, checked: cartItem ? true : it.checked };
            }).filter(it => it)
          }))
        },
        salesRelations: suggestItems
      };
    }

    default:
      return state;
  }
};
