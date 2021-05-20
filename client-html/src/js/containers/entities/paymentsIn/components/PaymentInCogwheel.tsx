import React, { memo, useEffect } from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../reducers/state";
import { getCustomValues, reverse } from "../actions";
import { CogwhelAdornmentProps } from "../../../../model/common/ListView";

interface PaymentInCustomValues {
  id: number;
  status: string;
  paymentMethodType: string;
  gatewayReference: number;
  payerTotalOwing: number;
  reconciled: boolean;
  reversalOf: number;
  reversedBy: number;
  bankingId: number;
}

const isDisabledForReverse = (customValues: PaymentInCustomValues) => (
    !customValues
    || customValues.status !== "Success"
    || ["Zero", "Reverse", "Voucher"].includes(customValues.paymentMethodType)
    || customValues.reversalOf !== null
    || customValues.reversedBy !== null
    || (customValues.paymentMethodType.toLowerCase() !== "credit card" && customValues.bankingId !== null)
  );

interface Props extends CogwhelAdornmentProps {
  reverse: any;
  customValues: any;
  getCustomValues: any;
  currencySymbol: string;
}

const PaymentInCogwheel = memo<Props>(props => {
  const {
    selection,
    menuItemClass,
    closeMenu,
    showConfirm,
    reverse,
    customValues,
    getCustomValues,
    currencySymbol
  } = props;

  useEffect(
    () => {
      if (selection && selection.length === 1 && selection[0] !== "NEW") {
        getCustomValues(selection[0]);
      }
    },
    [selection]
  );

  const isCreditCard = customValues && customValues.paymentMethodType.toLowerCase() === "credit card";

  const onClick = () => {
    if (isCreditCard && customValues.reconciled) {
      showConfirm(
        {
          confirmMessage: "Can not reverse reconciled payment",
          cancelButtonText: "OK",
          title: null
        }
      );
      return;
    }

    if (isCreditCard) {
      showConfirm({
        onConfirm: () => {
          reverse(selection[0]);
          closeMenu();
        },
        confirmMessage: <span>
          You are about to make a payment of
          {' '}
          <span className="money">{formatCurrency(customValues.amount, currencySymbol)}</span>
          {' '}
          to
          {' '}
          {customValues.name}
          {' '}
          as a refund against their credit card.
        </span>,
        cancelButtonText: "Continue"
      });
      return;
    }

    showConfirm({
      onConfirm: () => {
        reverse(selection[0]);
        closeMenu();
      },
      confirmMessage: "You are about to reverse payment",
      cancelButtonText: "Continue"
  });
  };

  const hasNoSelection = !(selection && selection.length === 1 && selection[0] !== "NEW");

  return (
    <>
      <MenuItem
        disabled={hasNoSelection || isDisabledForReverse(customValues)}
        className={menuItemClass}
        onClick={onClick}
      >
        Reverse Payment
      </MenuItem>
    </>
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  reverse: (id: number) => dispatch(reverse(id)),
  getCustomValues: (id: number) => dispatch(getCustomValues(id))
});

const mapStateToProps = (state: State) => ({
  customValues: state.paymentsIn.customValues,
  currencySymbol: state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PaymentInCogwheel);
