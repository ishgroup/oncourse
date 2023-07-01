/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import Button from "@mui/material/Button";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import {
 change, clearFields, DecoratedComponentClass, getFormValues, reduxForm 
} from "redux-form";
import Grid from "@mui/material/Grid";
import { Dispatch } from "redux";
import { Account, ProductItemCancel, Tax } from "@api/model";
import { FormControlLabel } from "@mui/material";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { BooleanArgFunction } from "../../../../../model/common/CommonFunctions";
import { State } from "../../../../../reducers/state";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { accountLabelCondition } from "../../../accounts/utils";
import { cancelSale } from "../../actions";

interface Props {
  opened: boolean;
  setDialogOpened: BooleanArgFunction;
  values?: ProductItemCancel;
  cancelSale?: (productItemCancel: ProductItemCancel) => void;
  handleSubmit?: any;
  classes?: any;
  invalid?: boolean;
  dispatch?: any;
  reset?: any;
  taxes?: Tax[];
  accounts?: Account[];
}

const initialValues: ProductItemCancel = {
  createCrediNote: true
};

const CancelSaleDialog = React.memo<Props>(props => {
  const {
    opened,
    handleSubmit,
    setDialogOpened,
    cancelSale,
    values,
    taxes,
    accounts,
    invalid,
    reset,
    dispatch
  } = props;

  const onClose = useCallback(() => {
    setDialogOpened(false);
    reset();
  }, []);

  const onSubmit = useCallback(values => {
    cancelSale(values);
    onClose();
  }, []);

  const clearFeeValues = useCallback(() => {
    dispatch(clearFields("CancelSaleForm", false, false, ...["feeAmount", "feeTaxId", "retainAccountId"]));
  }, []);

  const onRetainAdministrativeFeeChange = useCallback(val => {
    if (!val) {
      clearFeeValues();
    }
  }, []);

  const onCreateCrediNoteChange = useCallback(val => {
    if (!val) {
      dispatch(change("CancelSaleForm", "retainAdministrativeFee", false));
      clearFeeValues();
    }
  }, []);

  const incomeAccounts = useMemo(() => accounts.filter(a => a.type === "income"), [accounts.length]);

  const feeValuesDisabled = useMemo(() => !values.createCrediNote || !values.retainAdministrativeFee, [
    values.createCrediNote,
    values.retainAdministrativeFee
  ]);

  return (
    <Dialog open={opened} onClose={onClose} scroll="body" maxWidth="md">
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <DialogTitle>You are about to cancel this sale</DialogTitle>
        <DialogContent className="overflow-hidden">
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item xs={12}>
              <FormControlLabel
                classes={{
                  root: "checkbox"
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name="createCrediNote"
                    color="primary"
                    onChange={onCreateCrediNoteChange}
                  />
                )}
                label="Create credit note to reverse the sale fee"
              />
              <FormControlLabel
                classes={{
                  root: "checkbox"
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name="retainAdministrativeFee"
                    color="primary"
                    onChange={onRetainAdministrativeFeeChange}
                    disabled={!values.createCrediNote}
                  />
                )}
                label="Retain administrative fee"
              />
            </Grid>

            <Grid item xs={6}>
              <FormField
                type="money"
                name="feeAmount"
                label="Fee amount"
                validate={!feeValuesDisabled ? validateSingleMandatoryField : undefined}
                disabled={feeValuesDisabled}
              />
            </Grid>
            <Grid item xs={6}>
              <FormField
                type="select"
                name="feeTaxId"
                label="Tax type"
                selectValueMark="id"
                selectLabelMark="code"
                items={taxes || []}
                validate={!feeValuesDisabled ? validateSingleMandatoryField : undefined}
                disabled={feeValuesDisabled}
              />
            </Grid>
            <Grid item xs={12}>
              <FormField
                type="select"
                name="retainAccountId"
                label="Account"
                items={incomeAccounts || []}
                selectValueMark="id"
                selectLabelCondition={accountLabelCondition}
                validate={!feeValuesDisabled ? validateSingleMandatoryField : undefined}
                disabled={feeValuesDisabled}
              />
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions className="p-3">
          <Button color="primary" onClick={onClose}>
            Cancel
          </Button>

          <Button variant="contained" color="primary" type="submit" disabled={invalid}>
            Proceed
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
});

const mapStateToProps = (state: State) => ({
  values: getFormValues("CancelSaleForm")(state),
  accounts: state.plainSearchRecords.Account.items,
  taxes: state.taxes.items
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  cancelSale: (productItemCancel: ProductItemCancel) => dispatch(cancelSale(productItemCancel))
});

export default reduxForm({
  form: "CancelSaleForm",
  initialValues
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CancelSaleDialog)) as DecoratedComponentClass<
  any,
  Props
>;
