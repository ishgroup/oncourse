import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { submit } from 'redux-form';
import { normalize } from 'normalizr';
import { IshState } from '../../../services/IshState';

import {
  Application, Article, Enrolment, Membership, PurchaseItem, Voucher
} from '../../../model';
import {
  ApplicationProps,
  ArticleProps,
  ContactProps,
  EnrolmentProps,
  MembershipProps,
  VoucherProps,
} from './components/Index';

import { SummaryComp } from './components/SummaryComp';
import {
  proceedToPayment, replaceItem, selectItem, updateEnrolmentFields, updateItem
} from './actions/Actions';
import { submitPaymentForWaitingCourses } from '../payment/actions/Actions';
import {
  changePhase, addCode, getCheckoutModelFromBackend, toggleRedeemVoucher, updatePayNow, updateContactAddProcess,
  epicRemoveContact, toggleRedeemVoucherProduct,
} from '../../actions/Actions';
import { updateConcessionContact, getContactConcessions, getConcessionTypes } from '../concession/actions/Actions';
import { Phase } from '../../reducers/State';
import { SummaryService } from './services/SummaryService';
import CheckoutService from '../../services/CheckoutService';
import { WaitingList } from '../../../model/checkout/WaitingList';
import { ClassesListSchema } from '../../../NormalizeSchema';
import { mapPayload } from '../../../common/epics/EpicUtils';
import { Actions, requestSuggestion } from '../../../web/actions/Actions';

export const EnrolmentPropsBy = (e: Enrolment, state: IshState): EnrolmentProps => ({
  contact: state.checkout.contacts.entities.contact[e.contactId],
  courseClass: state.courses.entities[e.classId]
      || (e.relatedClassId || e.relatedProductId)
    ? e.classId
      ? state.courses.entities[e.classId]
      : state.inactiveCourses.entities[e.courseId] && { course: state.inactiveCourses.entities[e.courseId] }
    : null,
  enrolment: e,
});

export const ApplicationPropsBy = (a: Application, state: IshState): ApplicationProps => ({
  contact: state.checkout.contacts.entities.contact[a.contactId],
  courseClass: state.cart.courses.entities[a.classId],
  application: a,
});

export const VoucherPropsBy = (v: Voucher, state: IshState): VoucherProps => ({
  contact: state.checkout.contacts.entities.contact[v.contactId],
  product: state.cart.products.entities[v.productId]
      || (v.relatedClassId || v.relatedProductId) ? state.products.entities[v.productId] : null,
  voucher: v,
});

export const MembershipPropsBy = (m: Membership, state: IshState): MembershipProps => ({
  contact: state.checkout.contacts.entities.contact[m.contactId],
  product: state.cart.products.entities[m.productId]
      || (m.relatedClassId || m.relatedProductId) ? state.products.entities[m.productId] : null,
  membership: m,
});

export const ArticlePropsBy = (a: Article, state: IshState): ArticleProps => ({
  contact: state.checkout.contacts.entities.contact[a.contactId],
  product: state.cart.products.entities[a.productId]
      || (a.relatedClassId || a.relatedProductId) ? state.products.entities[a.productId] : null,
  article: a,
});

export const WaitingListPropsBy = (w: WaitingList, state: IshState): any => ({
  contact: state.checkout.contacts.entities.contact[w.contactId],
  product: state.cart.waitingCourses.entities[w.courseId],
  waitingList: w,
});

export const ContactPropsBy = (contactId: string, state: IshState): ContactProps => {
  const enrolmentIds = state.checkout.summary.entities.contactNodes[contactId].enrolments || [];
  const applicationIds = state.checkout.summary.entities.contactNodes[contactId].applications || [];
  const voucherIds = state.checkout.summary.entities.contactNodes[contactId].vouchers || [];
  const membershipIds = state.checkout.summary.entities.contactNodes[contactId].memberships || [];
  const articleIds = state.checkout.summary.entities.contactNodes[contactId].articles || [];
  const waitingListIds = state.checkout.summary.entities.contactNodes[contactId].waitingLists || [];

  const enrolments = state.checkout.summary.entities.enrolments || [];
  const vouchers = state.checkout.summary.entities.vouchers || [];
  const applications = state.checkout.summary.entities.applications || [];
  const memberships = state.checkout.summary.entities.memberships || [];
  const articles = state.checkout.summary.entities.articles || [];
  const waitingLists = state.checkout.summary.entities.waitingLists || [];

  return {
    contact: state.checkout.contacts.entities.contact[contactId],
    isPayer: state.checkout.payerId === contactId,
    enrolments: enrolmentIds.filter((id) => enrolments[id]).map((id: string): EnrolmentProps => EnrolmentPropsBy(enrolments[id], state)),
    applications: applicationIds.filter((id) => applications[id]).map((id: string): ApplicationProps => ApplicationPropsBy(applications[id], state)),
    vouchers: voucherIds.filter((id) => vouchers[id]).map((id: string): VoucherProps => VoucherPropsBy(vouchers[id], state)),
    memberships: membershipIds.filter((id) => memberships[id]).map((id: string): MembershipProps => MembershipPropsBy(memberships[id], state)),
    articles: articleIds.filter((id) => articles[id]).map((id: string): ArticleProps => ArticlePropsBy(articles[id], state)),
    waitingLists: waitingListIds.filter((id) => waitingLists[id]).map((id: string): any => WaitingListPropsBy(waitingLists[id], state)),
  };
};

export const SummaryPropsBy = (state: IshState): any => {
  try {
    const contactsArray: ContactProps[] = state.checkout.summary.result.map((id) => ContactPropsBy(id, state));

    return {
      amount: state.checkout.amount,
      contacts: contactsArray,
      promotions: Object.values(state.cart.promotions.entities),
      redeemVouchers: state.checkout.redeemVouchers,
      redeemedVoucherProducts: state.checkout.redeemedVoucherProducts,
      hasSelected: SummaryService.hasSelected(state.checkout.summary),
      concessions: state.checkout.concession.concessions,
      memberships: state.checkout.concession.memberships,
      concessionTypes: state.checkout.concession.types,
      needParent: !!CheckoutService.getAllSingleChildIds(state.checkout).length,
      fetching: state.checkout.summary.fetching,
      forms: state.form,
      isOnlyWaitingLists: CheckoutService.isOnlyWaitingCourseSelected(state.checkout.summary),
      successLink: state.config.paymentSuccessURL
    };
  } catch (e) {
    console.log(e);
    return {
      amount: {},
      contacts: [],
    };
  }
};

export const SummaryActionsBy = (dispatch: Dispatch<any>): any => ({
  onSelect: (item: PurchaseItem, selected: boolean): void => {
    dispatch(selectItem(Object.assign(item, { selected })));
  },
  onAddContact: (): void => {
    dispatch(changePhase(Phase.AddContact));
    dispatch(requestSuggestion());
  },
  onAddParent: (): void => {
    dispatch(changePhase(Phase.AddParent));
  },
  onRemoveContact: (contactId): void => {
    dispatch(epicRemoveContact(contactId));
  },
  onChangeParent: (contactId): void => {
    dispatch(updateContactAddProcess({}, Phase.AddContact, contactId));
    dispatch(changePhase(Phase.ChangeParent));
  },
  onChangeClass: (item1, item2, classes): void => {
    dispatch(replaceItem(item1, item2));
    dispatch(getCheckoutModelFromBackend());
    if (classes.length) {
      dispatch(mapPayload(Actions.REQUEST_COURSE_CLASS)(normalize(classes, ClassesListSchema)));
    }
  },
  onProceedToPayment: (forms): void => {
    forms && Object.keys(forms).map((form) => dispatch(submit(form)));

    const errors = Object.values<any>(forms).filter((f) => f.syncErrors);
    if (errors && errors.length) return;

    dispatch(proceedToPayment());
  },
  onProceedToJoin: (forms): void => {
    forms && Object.keys(forms).map((form) => dispatch(submit(form)));

    const errors = Object.values<any>(forms).filter((f) => f.syncErrors);
    if (errors && errors.length) return;

    dispatch(submitPaymentForWaitingCourses());
  },
  onAddCode: (code: string): void => {
    dispatch(addCode(code));
  },
  onUpdateWaitingCourse: (waitingCourse, prop) => {
    dispatch(updateItem(Object.assign(waitingCourse, prop)));
    dispatch(getCheckoutModelFromBackend());
  },
  onPriceValueChange: (productItem: Voucher, val: number): void => {
    const item = Object.assign(productItem, { value: val, price: val, total: (val * productItem.quantity).toFixed(2) });
    dispatch(updateItem(item));
    dispatch(getCheckoutModelFromBackend());
  },
  onQuantityValueChange: (productItem: Voucher | Article, val: number): void => {
    const item = Object.assign(productItem, { quantity: val, total: (val * productItem.price).toFixed(2) });
    dispatch(updateItem(item));
    dispatch(getCheckoutModelFromBackend());
  },
  onAddConcession: (contactId): void => {
    dispatch(updateConcessionContact(contactId));
    dispatch(changePhase(Phase.AddConcession));
  },
  onInit: () => {
    dispatch(getContactConcessions());
    dispatch(getConcessionTypes());
  },
  onUpdatePayNow: (val, validate): void => {
    dispatch(updatePayNow(val, validate));
  },
  onToggleVoucher: (redeemVoucher, enabled) => {
    dispatch(toggleRedeemVoucher(redeemVoucher, enabled));
  },
  onToggleVoucherProduct: (redeemVoucher, enabled) => {
    dispatch(toggleRedeemVoucherProduct(redeemVoucher, enabled));
  },
  onChangeCustomFields: (form, type) => {
    // added min delay until redux-form will changed
    // TODO: figure out correct way to dispatch redux-dom change action before form-blur
    setTimeout(() => dispatch(updateEnrolmentFields(form, type)), 30);
  },
});

const Container = connect<any, any, any>(SummaryPropsBy, SummaryActionsBy)(SummaryComp);

export default Container;
