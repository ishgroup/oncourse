/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@mui/material/Grid";
import Hidden from "@mui/material/Hidden";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import { Form, getFormValues, initialize, reduxForm } from "redux-form";
import { connect } from "react-redux";
import isEmpty from "lodash.isempty";
import { AccountType } from "@api/model";
import Button from "@mui/material/Button";
import FormField from "../../../../../common/components/form/formFields/FormField";
import * as Model from "../../../../../model/preferences/Financial";
import { currency, postPrepaidFees } from "../ListItems";
import { validateMultipleMandatoryFields } from "../../../../../common/utils/validation";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { State } from "../../../../../reducers/state";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";
import { getAccountsList } from "../../../utils";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";

const manualUrl = getManualLink("generalPrefs_financial");

class FinancialBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    // Initializing form with values
    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("FinancialForm", props.formData));
    }

    this.formModel = props.formatModel(Model);
  }

  componentDidUpdate() {
    // Initializing form with values
    if (!isEmpty(this.props.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("FinancialForm", this.props.formData));
    }
  }

  public render() {
    const {
     handleSubmit, onSave, accounts = [], dirty, data, invalid, form
    } = this.props;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container columnSpacing={3}>
            <Grid item xs={12} className="centeredFlex">
              <Typography className="appHeaderFontSize" color="inherit" noWrap>
                Financial
              </Typography>

              <div className="flex-fill" />

              {data && (
                <AppBarHelpMenu
                  created={data.created}
                  modified={data.modified}
                  auditsUrl={PREFERENCES_AUDITS_LINK}
                  manualUrl={manualUrl}
                />
              )}

              <FormSubmitButton
                disabled={!dirty}
                invalid={invalid}
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <Grid container columnSpacing={3}>
          <Grid item sm={8} xs={12}>
            <FormField
              type="multilineText"
              name={this.formModel.PaymentInfo.uniqueKey}
              label="Invoice remittance instructions"
              fullWidth
            />
          </Grid>

          <Grid item xs={12}>
            <Typography variant="subtitle1" className="heading mb-2 mt-1">
              Default accounts
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountDebtors.uniqueKey}
              label="Debtors (Asset)"
              items={getAccountsList(accounts, AccountType.asset)}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountBank.uniqueKey}
              label="Bank (Asset)"
              items={getAccountsList(accounts, AccountType.asset)}
              fullWidth
            />
          </Grid>

          <Hidden smDown>
            <Grid item md={4} />
          </Hidden>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountTax.uniqueKey}
              label="Tax (Liability)"
              items={getAccountsList(accounts, AccountType.liability)}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountStudentEnrolments.uniqueKey}
              label="Student enrolments (Income)"
              items={getAccountsList(accounts, AccountType.income)}
              fullWidth
            />
          </Grid>

          <Hidden smDown>
            <Grid item md={4} />
          </Hidden>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountPrepaidFees.uniqueKey}
              label="Prepaid fees account (Liability)"
              items={getAccountsList(accounts, AccountType.liability)}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountPrepaidFeesPostAt.uniqueKey}
              label="Post prepaid fees (When)"
              items={postPrepaidFees}
              fullWidth
            />
          </Grid>

          <Hidden smDown>
            <Grid item md={4} />
          </Hidden>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountVoucherLiability.uniqueKey}
              label="Voucher liability account (Liability)"
              items={getAccountsList(accounts, AccountType.liability)}
              helperText=""
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountVoucherUnderpayment.uniqueKey}
              label="Default voucher underpayment account"
              items={getAccountsList(accounts, AccountType.expense)}
              fullWidth
            />
          </Grid>

          <Hidden smDown>
            <Grid item md={4} />
          </Hidden>

          <Grid item xs={12} sm={8} className="mb-2">
            <Divider />
          </Grid>

          <Grid item xs={12}>
            <Typography variant="subtitle1" className="heading mb-2 mt-1">
              Other
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="select"
              name={this.formModel.AccountDefaultCurrency.uniqueKey}
              label="Default currency"
              items={currency}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <FormField
              type="number"
              name={this.formModel.AccountInvoiceTerms.uniqueKey}
              label="Default invoice terms (days)"
              fullWidth
            />
          </Grid>
        </Grid>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("FinancialForm")(state)
});

const FinancialForm = reduxForm({
  form: "FinancialForm",
  validate: validateMultipleMandatoryFields,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(FinancialBaseForm));

export default FinancialForm;
