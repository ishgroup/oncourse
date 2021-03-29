import React from "react";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {IshState} from "../../../services/IshState";
import {PaymentComp} from "./components/PaymentComp";
import PaymentForm from "./components/PaymentForm";
import {addCode, toggleRedeemVoucher, updatePayNow} from "../../actions/Actions";

const PropsBy = (state: IshState): any => {
  return {
    contacts: Object.values(state.checkout.contacts.entities.contact),
    amount: state.checkout.amount,
    currentTab: state.checkout.payment.currentTab,
    redeemVouchers: state.checkout.redeemVouchers,
    paymentForm: <PaymentForm/>,
    promotions:  Object.values(state.cart.promotions.entities),
  };
};

export const ActionsBy = (dispatch: Dispatch<any>): any => {
  return {
    onAddCode: (code: string): void => {
      dispatch(addCode(code));
    },
    onUpdatePayNow: (val: number, validate: boolean) => {
      dispatch(updatePayNow(val, validate));
    },
    onToggleVoucher: (voucher, enabled) => {
      dispatch(toggleRedeemVoucher(voucher, enabled));
    },
  };
};

export const Payment = connect<any,any,any>(PropsBy, ActionsBy)(PaymentComp);

