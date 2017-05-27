import * as React from "react";
import {IshState} from "../../../services/IshState";
import {connect, Dispatch} from "react-redux";

import {Props as ContactProps} from "./components/ContactComp";
import {Props as EnrolmentProps} from "./components/EnrolmentComp";
import SummaryComp from "./components/SummaryComp";
import {Enrolment} from "../../../model/checkout/Enrolment";
import {selectItem} from "./actions/Actions";
import {openContactAdd} from "../contact-add/actions/Actions";
import {openPayment} from "../payment/actions/Actions";
import {Voucher} from "../../../model/checkout/Voucher";
import {Article} from "../../../model/checkout/Article";
import {Membership} from "../../../model/checkout/Membership";


export const EnrolmentPropsBy = (enrolment: Enrolment, state: IshState): EnrolmentProps => {
  return {
    contact: state.checkout.payer.entity,
    courseClass: state.courses.entities[enrolment.classId],
    enrolment: enrolment
  }
};

export const ContactPropsBy = (contactId: string, state: IshState): ContactProps => {
  const ids = state.checkout.summary.entities.contacts[contactId].enrolments;
  const enrolments = state.checkout.summary.entities.enrolments;
  return {
    contact: state.checkout.payer.entity,
    enrolments: ids.map((id: string): EnrolmentProps => EnrolmentPropsBy(enrolments[id], state)),
  };
};

export const SummaryPropsBy = (state: IshState): any => {
  try {
    const contacts: ContactProps[] = state.checkout.summary.result.map((id) => {
      return ContactPropsBy(id, state)
    });
    return {
      amount: state.checkout.amount,
      contacts: contacts
    };
  } catch (e) {
    console.log(e);
    return {
      amount: {},
      contacts: []
    }
  }
};

export const SummaryFunctionsBy = (dispatch: Dispatch<any>) => {
  return {
    onSelect: (item: Enrolment | Membership | Article | Voucher, selected: boolean): void => {
      dispatch(selectItem(item, selected))
    },
    onAddContact: (): void => {
      dispatch(openContactAdd())
    },
    onProceedToPayment: (): void => {
      dispatch(openPayment())
    }
  };
};

const Container = connect(SummaryPropsBy, SummaryFunctionsBy)(SummaryComp);

export default Container