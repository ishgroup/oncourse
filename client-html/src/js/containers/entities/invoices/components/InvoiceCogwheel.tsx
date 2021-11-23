/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { memo, NamedExoticComponent, useCallback, useEffect, useMemo, useState } from "react";
import MenuItem from "@mui/material/MenuItem";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import { change, isDirty, reset } from "redux-form";
import { PaymentOut } from "@api/model";
import { format } from "date-fns";
import { State } from "../../../../reducers/state";
import {
  duplicateAndReverseInvoice,
  duplicateQuote,
  getAmountOwing,
  setContraInvoices
} from "../actions";
import ContraInvoiceModal from "./ContraInvoiceModal";
import { getAddPaymentOutContact, postPaymentOut } from "../../paymentsOut/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import {
  getRecords,
  setListCreatingNew,
  setListNestedEditRecord,
  setListSelection,
} from "../../../../common/components/list-view/actions";
import { PaymentOutModel } from "../../paymentsOut/reducers/state";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import history from "../../../../constants/History";
import { CogwhelAdornmentProps } from "../../../../model/common/ListView";
import { isInvoiceType } from "../utils";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";

interface Props extends CogwhelAdornmentProps {
  dispatch: any;
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
  listRecords: any;
  duplicateQuote: any;
  history: any;
  match: any;
  setListCreatingNew: any;
  updateSelection: any;
  location?: any;
}

const InvoiceCogwheel: NamedExoticComponent = memo<Props>(props => {
  const {
    dispatch,
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
    hasQePermissions,
    listRecords,
    duplicateQuote,
    setListCreatingNew,
    updateSelection,
    match,
    location,
  } = props;

  const [dialogOpened, setDialogOpened] = useState(false);
  const oneSelectedAndNotNew = useMemo(() => selection.length === 1 && selection[0] !== "NEW", [selection]);

  const disableActionForQuote = useMemo(() => {
    if (selection.length !== 1) return true;

    return isInvoiceType(selection[0], listRecords);
  }, [selection]);

  const updateHistory = (pathname, search, type?) => {
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search,
        state: { type },
      });
    }
  };

  const setCreateNew = () => {
    const { params, url } = match;

    updateHistory(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new", window.location.search);

    setListCreatingNew(true);
    updateSelection(["new"]);
  };

  const duplicateCallback = useCallback(type => {
    setCreateNew();
    if (type === "Invoice") {
      duplicateAndReverseInvoice(selection[0]);
    } else {
      duplicateQuote(selection[0]);
    }

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
      administrationCenterId,
      selectedPaymentMethod
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

    if (selectedPaymentMethod === "Cheque") {
      paymentOut.chequeSummary = chequeSummary;
    }

    if (selectedPaymentMethod === "Credit card") {
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
    dispatch(getRecords("AbstractInvoice"));
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
              setTimeout(() => duplicateCallback("Invoice"), 100);
            }
          });
        } else {
          duplicateCallback("Invoice");
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
      case "DuplicateQuote": {
        if (isFormDirty) {
          showConfirm({
            onConfirm: () => {
              resetEditView();
              setTimeout(() => duplicateCallback("Quote"), 100);
            }
          });
        } else {
          duplicateCallback("Quote");
        }
        break;
      }
      case "ConvertingQuote": {
        const { params, url } = match;
        updateHistory(params.id ? url.replace(`/${params.id}`, `/${selection[0]}`) : url + `/${selection[0]}`, location.search, "Invoice");

        if (listRecords.layout === "Three column") {
          dispatch(change(LIST_EDIT_VIEW_FORM_NAME, "type", "Invoice"));
        }

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
        disabled={!oneSelectedAndNotNew || !contraInvoices || !disableActionForQuote}
        className={menuItemClass}
        role="Contra"
        onClick={onClick}
      >
        Contra invoice...
      </MenuItem>
      <MenuItem disabled={!oneSelectedAndNotNew || !disableActionForQuote} className={menuItemClass} role="Duplicate" onClick={onClick}>
        Duplicate and reverse invoice
      </MenuItem>

      <MenuItem disabled={!applyPaymentInAllowed || !disableActionForQuote} className={menuItemClass} role="PaymentIn" onClick={onClick}>
        Apply payment in
      </MenuItem>

      <MenuItem disabled={!applyPaymentOutAllowed || !disableActionForQuote} className={menuItemClass} role="PaymentOut" onClick={onClick}>
        Apply payment out
      </MenuItem>

      <MenuItem disabled={disableActionForQuote} className={menuItemClass} role="DuplicateQuote" onClick={onClick}>
        Duplicate quote
      </MenuItem>

      <MenuItem disabled={disableActionForQuote} className={menuItemClass} role="ConvertingQuote" onClick={onClick}>
        Convert quote to invoice
      </MenuItem>

      <BulkEditCogwheelOption {...props} />

    </>
  );
});

const mapStateToProps = (state: State) => ({
  listRecords: state.list.records,
  contraInvoices: state.invoices.contraInvoices,
  selectedInvoiceAmountOwing: state.invoices.selectedInvoiceAmountOwing,
  isFormDirty: isDirty(LIST_EDIT_VIEW_FORM_NAME)(state),
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  dispatch,
  resetEditView: () => dispatch(reset(LIST_EDIT_VIEW_FORM_NAME)),
  getAmountOwing: (id: number) => dispatch(getAmountOwing(id)),
  getAddPaymentOutContact: (id: number) => dispatch(getAddPaymentOutContact(id)),
  clearContraInvoices: () => dispatch(setContraInvoices(null)),
  duplicateAndReverseInvoice: (id: number) => dispatch(duplicateAndReverseInvoice(id)),
  duplicateQuote: (id: number) => dispatch(duplicateQuote(id)),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  openAddPaymentOutEditView: (entity: string, record: any, customOnSave?: any) =>
    dispatch(setListNestedEditRecord(entity, record, customOnSave))
});

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(InvoiceCogwheel));
