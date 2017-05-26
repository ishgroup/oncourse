import * as React from "react";
import {Props as ContactProps} from "./components/ContactComp";
import {Props as EnrolmentProps} from "./components/EnrolmentComp";
import {IshState} from "../../../services/IshState";
import {connect, Dispatch} from "react-redux";

import SummaryComp from "./components/SummaryComp";
import {PurchaseItems} from "../../../model/checkout/PurchaseItems";
import {Enrolment} from "../../../model/checkout/Enrolment";

const EnrolmentPropsBy = (enrolment: Enrolment, state: IshState): EnrolmentProps => {
  return {
    contact: state.checkout.payer.entity,
    courseClass: state.courses.entities[enrolment.classId],
    enrolment: enrolment
  }
};

const ContactPropsBy = (items: PurchaseItems, state: IshState): ContactProps => {
  return {
    contact: state.checkout.payer.entity,
    enrolments: items.enrolments.map((e: Enrolment): EnrolmentProps => EnrolmentPropsBy(e, state)),
  };
};

const mapStateToProps = (state: IshState) => {
  try {
    const contacts: ContactProps[] = state.checkout.purchaseItems.map((i: PurchaseItems) => {
      return ContactPropsBy(i, state)
    });
    return {
      contacts: contacts
    };
  } catch (e) {
    console.log(e);
    return {contacts: []}
  }

};

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  const onSelect = (contact, item, selected): void => {
  };
  return {
    onSelect: onSelect
  }
};

const Container = connect(mapStateToProps, mapDispatchToProps)(SummaryComp);

export default Container