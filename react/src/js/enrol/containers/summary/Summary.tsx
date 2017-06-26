import * as React from "react";
import {IshState} from "../../../services/IshState";
import {connect, Dispatch} from "react-redux";
import {ItemToState} from "./reducers/State";

import {Application, Article, Enrolment, Membership, PurchaseItem, Voucher} from "../../../model/checkout/Index";
import {
  ApplicationProps,
  ArticleProps,
  ContactProps,
  EnrolmentProps,
  MembershipProps,
  VoucherProps,
} from "./components/Index";

import {SummaryComp} from "./components/SummaryComp";
import {proceedToPayment, selectItem, updateItem} from "./actions/Actions";
import {changePhase, addCode, getCheckoutModelFromBackend} from "../../actions/Actions";
import {Phase} from "../../reducers/State";
import {SummaryService} from "./services/SummaryService";

export const EnrolmentPropsBy = (e: Enrolment, state: IshState): EnrolmentProps => {
  return {
    contact: state.checkout.contacts.entities.contact[e.contactId],
    courseClass: state.courses.entities[e.classId],
    enrolment: e,
  };
};

export const ApplicationPropsBy = (a: Application, state: IshState): ApplicationProps => {
  return {
    contact: state.checkout.contacts.entities.contact[a.contactId],
    courseClass: state.courses.entities[a.classId],
    application: a,
  };
};

export const VoucherPropsBy = (v: Voucher, state: IshState): VoucherProps => {
  return {
    contact: state.checkout.contacts.entities.contact[v.contactId],
    product: state.products.entities[v.productId],
    voucher: v,
  };
};

export const MembershipPropsBy = (m: Membership, state: IshState): MembershipProps => {
  return {
    contact: state.checkout.contacts.entities.contact[m.contactId],
    product: state.products.entities[m.productId],
    membership: m,
  };
};

export const ArticlePropsBy = (a: Article, state: IshState): ArticleProps => {
  return {
    contact: state.checkout.contacts.entities.contact[a.contactId],
    product: state.products.entities[a.productId],
    article: a,
  };
};

export const ContactPropsBy = (contactId: string, state: IshState): ContactProps => {
  const enrolmentIds = state.checkout.summary.entities.contactNodes[contactId].enrolments || [];
  const applicationIds = state.checkout.summary.entities.contactNodes[contactId].applications || [];
  const voucherIds = state.checkout.summary.entities.contactNodes[contactId].vouchers || [];
  const membershipIds = state.checkout.summary.entities.contactNodes[contactId].memberships || [];
  const articleIds = state.checkout.summary.entities.contactNodes[contactId].articles || [];

  const enrolments = state.checkout.summary.entities.enrolments || [];
  const vouchers = state.checkout.summary.entities.vouchers || [];
  const applications = state.checkout.summary.entities.applications || [];
  const memberships = state.checkout.summary.entities.memberships || [];
  const articles = state.checkout.summary.entities.articles || [];

  return {
    contact: state.checkout.contacts.entities.contact[contactId],
    enrolments: enrolmentIds.map((id: string): EnrolmentProps => EnrolmentPropsBy(enrolments[id], state)),
    applications: applicationIds.map((id: string): ApplicationProps => ApplicationPropsBy(applications[id], state)),
    vouchers: voucherIds.map((id: string): VoucherProps => VoucherPropsBy(vouchers[id], state)),
    memberships: membershipIds.map((id: string): MembershipProps => MembershipPropsBy(memberships[id], state)),
    articles: articleIds.map((id: string): ArticleProps => ArticlePropsBy(articles[id], state)),
  };
};

export const SummaryPropsBy = (state: IshState): any => {
  try {
    const contactsArray: ContactProps[] = state.checkout.summary.result.map(id => {
      return ContactPropsBy(id, state);
    });
    return {
      amount: state.checkout.amount,
      contacts: contactsArray,
      promotions:  Object.values(state.cart.promotions.entities),
      hasSelected: SummaryService.hasSelected(state.checkout.summary),
    };
  } catch (e) {
    console.log(e);
    return {
      amount: {},
      contacts: [],
    };
  }
};

export const SummaryActionsBy = (dispatch: Dispatch<any>): any => {
  return {
    onSelect: (item: PurchaseItem, selected: boolean): void => {
      dispatch(selectItem(item, selected));
    },
    onAddContact: (): void => {
      dispatch(changePhase(Phase.AddContact));
    },
    onProceedToPayment: (): void => {
      dispatch(proceedToPayment());
    },
    onAddCode: (code: string): void => {
      dispatch(addCode(code));
    },
    onPriceValueChange: (productItem: PurchaseItem, val): void => {
      const product = Object.assign(productItem, {price: val, value: val});
      dispatch(updateItem(ItemToState(product)));
    },
  };
};

const Container = connect(SummaryPropsBy, SummaryActionsBy)(SummaryComp);

export default Container;
