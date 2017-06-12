import * as React from "react";
import {IshState} from "../../../services/IshState";
import {connect, Dispatch} from "react-redux";

import {Props as ContactProps} from "./components/ContactComp";
import {Props as EnrolmentProps} from "./components/EnrolmentComp";
import {Props as ApplicationProps} from "./components/ApplicationComp";
import {Props as VoucherProps} from "./components/VoucherComp";
import {SummaryComp} from "./components/SummaryComp";
import {Enrolment} from "../../../model/checkout/Enrolment";
import {proceedToPayment, selectItem} from "./actions/Actions";
import {openAddContactForm} from "../contact-add/actions/Actions";
import {Voucher} from "../../../model/checkout/Voucher";
import {Article} from "../../../model/checkout/Article";
import {Membership} from "../../../model/checkout/Membership";
import {SummaryService} from "./services/SummaryService";
import {Application} from "../../../model/checkout/Application";


export const EnrolmentPropsBy = (enrolment: Enrolment, state: IshState): EnrolmentProps => {
  return {
    contact: state.checkout.payer.entity,
    courseClass: state.courses.entities[enrolment.classId],
    enrolment: enrolment
  }
};

export const ApplicationPropsBy = (application: Application, state: IshState): ApplicationProps => {
  return {
    contact: state.checkout.payer.entity,
    courseClass: state.courses.entities[application.classId],
    application: application
  }
};

export const VoucherPropsBy = (voucher: Voucher, state: IshState): VoucherProps => {
  return {
    contact: state.checkout.payer.entity,
    productClass: state.products.entities[voucher.productId],
    voucher: voucher
  }
};

export const ContactPropsBy = (contactId: string, state: IshState): ContactProps => {
  const enrolmentIds = state.checkout.summary.entities.contactNodes[contactId].enrolments || [];
  const applicationIds = state.checkout.summary.entities.contactNodes[contactId].applications || [];

  const voucherIds = state.checkout.summary.entities.contactNodes[contactId].vouchers || [];
  const enrolments = state.checkout.summary.entities.enrolments || [];
  const applications = state.checkout.summary.entities.applications || [];

  const vouchers = state.checkout.summary.entities.vouchers || [];
  return {
    contact: state.checkout.payer.entity,
    enrolments: enrolmentIds.map((id: string): EnrolmentProps => EnrolmentPropsBy(enrolments[id], state)),
    applications: applicationIds.map((id: string): ApplicationProps => ApplicationPropsBy(applications[id], state)),
    vouchers: voucherIds.map((id: string): VoucherProps => VoucherPropsBy(vouchers[id], state)),
  };
};

export const SummaryPropsBy = (state: IshState): any => {
  try {
    const contacts: ContactProps[] = state.checkout.summary.result.map((id) => {
      return ContactPropsBy(id, state)
    });
    return {
      amount: state.checkout.amount,
      contacts: contacts,
      hasSelected: SummaryService.hasSelected(state.checkout.summary)
    };
  } catch (e) {
    console.log(e);
    return {
      amount: {},
      contacts: []
    }
  }
};

export const SummaryActionsBy = (dispatch: Dispatch<any>): any => {
  return {
    onSelect: (item: Enrolment | Application | Membership | Article | Voucher, selected: boolean): void => {
      dispatch(selectItem(item, selected))
    },
    onAddContact: (): void => {
      dispatch(openAddContactForm())
    },
    onProceedToPayment: (): void => {
      dispatch(proceedToPayment())
    }
  };
};

const Container = connect(SummaryPropsBy, SummaryActionsBy)(SummaryComp);

export default Container