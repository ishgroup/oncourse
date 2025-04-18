/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AccountType } from '@api/model';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import Hidden from '@mui/material/Hidden';
import Typography from '@mui/material/Typography';
import $t from '@t';
import isEmpty from 'lodash.isempty';
import * as React from 'react';
import { connect } from 'react-redux';
import { Form, getFormInitialValues, initialize, reduxForm } from 'redux-form';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { validateMultipleMandatoryFields } from '../../../../../common/utils/validation';
import { ACCOUNT_DEFAULT_INVOICELINE_ID } from '../../../../../constants/Config';
import * as Model from '../../../../../model/preferences/Financial';
import { FormModelSchema } from '../../../../../model/preferences/FormModelShema';
import { State } from '../../../../../reducers/state';
import { PREFERENCES_AUDITS_LINK } from '../../../constants';
import { getAccountsList } from '../../../utils';
import { currency, postPrepaidFees } from '../ListItems';

const manualUrl = getManualLink("setting-your-general-preferences#financial");

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

  componentDidUpdate(prevProps) {
    const { formData, dispatch, initialized, initialValues, defaultInvoiceLineAccount } = this.props;

    // Initializing form with values
    if (!isEmpty(formData) && !initialized) {
      dispatch(initialize("FinancialForm", formData));
    }

    if (initialValues && (initialValues.defaultInvoiceLineAccount !== defaultInvoiceLineAccount)) {
      dispatch(initialize("FinancialForm", { ...formData, defaultInvoiceLineAccount }));
    }
  }

  public render() {
    const {
      handleSubmit, onSave, accounts = [], dirty, data, invalid, form, formRoleName
    } = this.props;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)} role={formRoleName}>
        <RouteChangeConfirm form={form} when={dirty}/>

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={PREFERENCES_AUDITS_LINK}
          disabled={!dirty}
          invalid={invalid}
          title={$t('financial')}
          disableInteraction
          createdOn={values => values.created}
          modifiedOn={values => values.modified}
        >
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item sm={8} xs={12}>
              <FormField
                type="multilineText"
                name={this.formModel.PaymentInfo.uniqueKey}
                label={$t('invoice_remittance_instructions')}
                              />
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle1" className="heading mb-2 mt-1">
                {$t('default_accounts')}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountDebtors.uniqueKey}
                label={$t('debtors_asset')}
                items={getAccountsList(accounts, AccountType.asset)}
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountBank.uniqueKey}
                label={$t('bank_asset')}
                items={getAccountsList(accounts, AccountType.asset)}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountTax.uniqueKey}
                label={$t('tax_liability')}
                items={getAccountsList(accounts, AccountType.liability)}
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountStudentEnrolments.uniqueKey}
                label={$t('student_enrolments_income')}
                items={getAccountsList(accounts, AccountType.income)}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountPrepaidFees.uniqueKey}
                label={$t('prepaid_fees_account_liability')}
                items={getAccountsList(accounts, AccountType.liability)}
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountPrepaidFeesPostAt.uniqueKey}
                label={$t('post_prepaid_fees_when')}
                items={postPrepaidFees}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountVoucherLiability.uniqueKey}
                label={$t('voucher_liability_account_liability')}
                items={getAccountsList(accounts, AccountType.liability)}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.AccountVoucherUnderpayment.uniqueKey}
                label={$t('default_voucher_underpayment_account')}
                items={getAccountsList(accounts, AccountType.expense)}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item sm={6} md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name="defaultInvoiceLineAccount"
                label={$t('default_invoice_line_income_account')}
                items={getAccountsList(accounts, AccountType.income)}
                debounced={false}
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
                {$t('other')}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="number"
                name={this.formModel.AccountInvoiceTerms.uniqueKey}
                label={$t('default_invoice_terms_days')}
                              />
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  initialValues: getFormInitialValues("FinancialForm")(state),
  defaultInvoiceLineAccount: state.userPreferences[ACCOUNT_DEFAULT_INVOICELINE_ID]
});

const FinancialForm = reduxForm({
  form: "FinancialForm",
  validate: validateMultipleMandatoryFields,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(FinancialBaseForm));

export default FinancialForm;