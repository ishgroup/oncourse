/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import {
  FieldArray, getFormValues
} from "redux-form";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import FormField from "../../../../common/components/form/form-fields/FormField";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { validateSingleMandatoryField, validateVetPurchasingContractIdentifier } from "../../../../common/utils/validation";
import { CheckoutFundingInvoiceItem } from "../../../../model/checkout/fundingInvoice";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { AppTheme } from "../../../../model/common/Theme";
import { State } from "../../../../reducers/state";
import ContactSelectItemRenderer from "../../../entities/contacts/components/ContactSelectItemRenderer";
import { contactLabelCondition, getContactName, openContactLink } from "../../../entities/contacts/utils";
import { summaryListStyles } from "../../styles/summaryListStyles";
import CheckoutFundingInvoicePaymentPlans from "./CheckoutFundingInvoicePaymentPlans";
import CheckoutFundingInvoiceSummaryExpandableItemRenderer from "./CheckoutFundingInvoiceSummaryExpandableItemRenderer";

export const CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM = "checkout_funding_invoice_summary_list_form";

const styles = (theme: AppTheme) => createStyles({
  tableTabRow: {
    padding: theme.spacing(0, 2.625, 0, 2)
  }
});

const trainingPlansColumns: NestedTableColumn[] = [
  {
    name: "date",
    title: "Date",
    type: "date",
    width: 125
  },
  {
    name: "unitsCommenced",
    title: "Units commenced"
  },
  {
    name: "unitsCompleted",
    title: "Units completed"
  }
];

interface Props {
  classes?: any;
  formValues?: CheckoutFundingInvoiceItem;
  dispatch?: any;
  syncErrors?: any;
  item?: CheckoutFundingInvoiceItem;
  currency?: any;
  selectedCompanies?: any[];
  addSelectedCompany?: (company: any) => void;
  form?: string;
}

const CheckoutFundingInvoiceSummaryList = React.memo<Props>(props => {
  const {
    classes,
    formValues,
    dispatch,
    syncErrors,
    currency,
    item,
    selectedCompanies,
    addSelectedCompany,
    form
  } = props;

  const onChangeCompany = React.useCallback(value => {
    addSelectedCompany(value);
  }, []);

  return (
    <Grid container className="align-content-between">
      <Grid item xs={6}>
        <FormField
          type="remoteDataSearchSelect"
          entity="FundingInvoiceCompany"
          name="fundingProviderId"
          label="Funding provider"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={selectedCompanies.length && getContactName(selectedCompanies[0])}
          itemRenderer={ContactSelectItemRenderer}
          onInnerValueChange={onChangeCompany}
          rowHeight={55}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openContactLink}
              link={formValues && formValues.fundingProviderId}
              disabled={!formValues || !formValues.fundingProviderId}
            />
          )}
          validate={validateSingleMandatoryField}
        />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="text"
          name="vetPurchasingContractID"
          label="Purchasing contract identifier (NSW Commitment ID)"
          validate={validateVetPurchasingContractIdentifier}
          fullWidth
        />
      </Grid>
      <Grid item xs={12} className="pb-3">
        <CheckoutFundingInvoiceSummaryExpandableItemRenderer
          classes={classes}
          header={getContactName(item.enrolment.contact)}
          items={item.enrolment.items}
          itemTotal={formValues ? formValues.total : 0}
          currencySymbol={currency && currency.shortCurrencySymbol}
          dispatch={dispatch}
          paymentPlans={formValues ? formValues.paymentPlans : []}
          form={form}
        />
      </Grid>
      <Grid container>
        {formValues && formValues.paymentPlans && (
          <Grid item sm={6} className="pr-2">
            <CheckoutFundingInvoicePaymentPlans
              name="paymentPlans"
              currency={currency}
              syncErrors={syncErrors}
              form={CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM}
              dispatch={dispatch}
              total={formValues ? formValues.total : 0}
            />
          </Grid>
        )}
        {formValues && formValues.trainingPlans && formValues.trainingPlans.length > 0 && (
          <Grid item sm={6}>
            <div className="centeredFlex">
              <Typography className="heading pt-1 pb-1">
                Training Plan
              </Typography>
            </div>
            <FieldArray
              name="trainingPlans"
              className="saveButtonTableOffset"
              component={NestedTable}
              columns={trainingPlansColumns}
              hideHeader
            />
          </Grid>
        )}
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  currency: state.currency,
  formValues: getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state),
  selectedCompanies: state.checkout.fundingInvoice.companies
});

export default connect<any, any, any>(
  mapStateToProps
)(withStyles((theme: AppTheme) => ({ ...summaryListStyles(theme), ...styles(theme) }))(CheckoutFundingInvoiceSummaryList));
