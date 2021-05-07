/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useMemo, memo, useCallback, useEffect, useState
} from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { isDirty, reset } from "redux-form";
import { PaymentOut } from "@api/model";
import { format } from "date-fns";
import { State } from "../../../../reducers/state";
import { setContraInvoices, duplicateAndReverseInvoice, getAmountOwing } from "../actions";
import ContraInvoiceModal from "./ContraInvoiceModal";
import { getAddPaymentOutContact, postPaymentOut } from "../../paymentsOut/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getRecords, setListNestedEditRecord } from "../../../../common/components/list-view/actions";
import { PaymentOutModel } from "../../paymentsOut/reducers/state";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import history from "../../../../constants/History";
import { CogwhelAdornmentProps } from "../../../../model/common/ListView";

interface Props extends CogwhelAdornmentProps {
  clearContraInvoices: any;
  duplicateAndReverseInvoice: any;
  contraInvoices: any;
  selectedInvoiceAmountOwing: any;
  getAmountOwing: any;
  getAddPaymentOutContact: any;
  isFormDirty: boolean;
  resetEditView: any;
  openAddPaymentOutEditView: any;
  hasQePermissions: any;
}

const InvoiceCogwheel = memo<Props>(props => {
  const {
    selection,
    menuItemClass,
    clearContraInvoices,
    duplicateAndReverseInvoice,
    contraInvoices,
    selectedInvoiceAmountOwing,
    getAmountOwing,
    getAddPaymentOutContact,
    onCreate,
    closeMenu,
    showConfirm,
    isFormDirty,
    resetEditView,
    openAddPaymentOutEditView,
    hasQePermissions
  } = props;

  const [dialogOpened, setDialogOpened] = useState(false);
  const oneSelectedAndNotNew = useMemo(() => selection.length === 1 && selection[0] !== "NEW", [selection]);

  const duplicateCallback = useCallback(() => {
    onCreate();
    duplicateAndReverseInvoice(selection[0]);
    closeMenu();
  }, [selection]);

  useEffect(() => {
    if (oneSelectedAndNotNew) {
      getAmountOwing(selection[0]);
      getAddPaymentOutContact(selection[0]);
    }

    return () => clearContraInvoices();
  }, [oneSelectedAndNotNew]);

  const getPaymentOutFromModel = (model: PaymentOutModel) => {
    const {
      amount,
      chequeSummary,
      datePayed: unformattedDatePayed,
      invoices,
      payeeId,
      refundableId,
      paymentMethodId,
      privateNotes,
      administrationCenterId
    } = model;
    const datePayed = format(new Date(unformattedDatePayed), YYYY_MM_DD_MINUSED);
    const paymentOut: PaymentOut = {
      amount,
      datePayed,
      payeeId,
      paymentMethodId,
      privateNotes,
      administrationCenterId
    };

    if (paymentMethodId === 1) {
      paymentOut.chequeSummary = chequeSummary;
    }

    if (paymentMethodId === 2) {
      paymentOut.refundableId = refundableId;
    }

    paymentOut.invoices = invoices
      .map(i => ({
        id: i.id,
        amount: Math.round(i.outstanding * 100 - i.amountOwing * 100) / 100
      }))
      .filter(i => i.amount > 0);

    return paymentOut;
  };

  const handleAddPaymentOut = (record, dispatch: Dispatch<any>, formProps) => {
    const paymentOut = getPaymentOutFromModel(record);
    dispatch(postPaymentOut(paymentOut));
    formProps.toogleFullScreenEditView();
    dispatch(getRecords("Invoice"));
  };

  const onClick = useCallback(e => {
    const status = e.target.getAttribute("role");

    switch (status) {
      case "Contra": {
        setDialogOpened(true);
        break;
      }
      case "Duplicate": {
        if (isFormDirty) {
          showConfirm({
            onConfirm: () => {
              resetEditView();
              setTimeout(duplicateCallback, 100);
            }
          });
        } else {
          duplicateCallback();
        }
        break;
      }
      case "PaymentIn": {
        history.push(`/checkout?invoiceId=${selection[0]}`);
        closeMenu();
        break;
      }
      case "PaymentOut": {
        openAddPaymentOutEditView("PaymentOut", {}, handleAddPaymentOut);
        closeMenu();
        break;
      }
    }
  }, []);

  const applyPaymentInAllowed = useMemo(
    () =>
      hasQePermissions
      && oneSelectedAndNotNew
      && typeof selectedInvoiceAmountOwing === "number"
      && selectedInvoiceAmountOwing > 0,
    [oneSelectedAndNotNew, selectedInvoiceAmountOwing, hasQePermissions]
  );
  const applyPaymentOutAllowed = useMemo(
    () => oneSelectedAndNotNew && typeof selectedInvoiceAmountOwing === "number" && selectedInvoiceAmountOwing < 0,
    [oneSelectedAndNotNew, selectedInvoiceAmountOwing]
  );

  return (
    <>
      <ContraInvoiceModal opened={dialogOpened} setDialogOpened={setDialogOpened} />

      <MenuItem
        disabled={!oneSelectedAndNotNew || !contraInvoices}
        className={menuItemClass}
        role="Contra"
        onClick={onClick}
      >
        Contra invoice...
      </MenuItem>
      <MenuItem disabled={!oneSelectedAndNotNew} className={menuItemClass} role="Duplicate" onClick={onClick}>
        Duplicate and reverse invoice
      </MenuItem>

      <MenuItem disabled={!applyPaymentInAllowed} className={menuItemClass} role="PaymentIn" onClick={onClick}>
        Apply payment in
      </MenuItem>

      <MenuItem disabled={!applyPaymentOutAllowed} className={menuItemClass} role="PaymentOut" onClick={onClick}>
        Apply payment out
      </MenuItem>
    </>
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  resetEditView: () => dispatch(reset(LIST_EDIT_VIEW_FORM_NAME)),
  getAmountOwing: (id: number) => dispatch(getAmountOwing(id)),
  getAddPaymentOutContact: (id: number) => dispatch(getAddPaymentOutContact(id)),
  clearContraInvoices: () => dispatch(setContraInvoices(null)),
  duplicateAndReverseInvoice: (id: number) => dispatch(duplicateAndReverseInvoice(id)),
  openAddPaymentOutEditView: (entity: string, record: any, customOnSave?: any) =>
    dispatch(setListNestedEditRecord(entity, record, customOnSave))
});

const mapStateToProps = (state: State) => ({
  contraInvoices: state.invoices.contraInvoices,
  selectedInvoiceAmountOwing: state.invoices.selectedInvoiceAmountOwing,
  isFormDirty: isDirty(LIST_EDIT_VIEW_FORM_NAME)(state),
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(InvoiceCogwheel);
