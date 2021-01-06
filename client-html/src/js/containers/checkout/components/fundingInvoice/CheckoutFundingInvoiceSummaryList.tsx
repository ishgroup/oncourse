/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import {
  change,
  FieldArray
} from "redux-form";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import FormField from "../../../../common/components/form/form-fields/FormField";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { validateVetPurchasingContractIdentifier } from "../../../../common/utils/validation";
import { CheckoutFundingInvoice } from "../../../../model/checkout/fundingInvoice";
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
  dispatch?: any;
  syncErrors?: any;
  fundingInvoice?: CheckoutFundingInvoice;
  currency?: any;
  form?: string;
  selectedItemIndex?: number;
}

const CheckoutFundingInvoiceSummaryList = React.memo<Props>(props => {
  const {
    classes,
    dispatch,
    syncErrors,
    currency,
    fundingInvoice,
    selectedItemIndex,
    form
  } = props;

  const onChangeCompany = company => {
    dispatch(change(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM, `fundingInvoices[${selectedItemIndex}].company`, company));
  };

  return (
    <Grid container className="align-content-between">
      <Grid item xs={6}>
        <FormField
          type="remoteDataSearchSelect"
          name={`fundingInvoices[${selectedItemIndex}].fundingProviderId`}
          entity="Contact"
          aqlFilter="isCompany is true"
          label="Funding provider"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={fundingInvoice.company && getContactName(fundingInvoice.company)}
          itemRenderer={ContactSelectItemRenderer}
          onInnerValueChange={onChangeCompany}
          rowHeight={55}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openContactLink}
              link={fundingInvoice && fundingInvoice.fundingProviderId}
              disabled={!fundingInvoice || !fundingInvoice.fundingProviderId}
            />
          )}
        />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="text"
          name={`fundingInvoices[${selectedItemIndex}].vetPurchasingContractID`}
          label="Purchasing contract identifier (NSW Commitment ID)"
          validate={validateVetPurchasingContractIdentifier}
          fullWidth
        />
      </Grid>
      <Grid item xs={12} className="pb-3">
        <CheckoutFundingInvoiceSummaryExpandableItemRenderer
          classes={classes}
          header={getContactName(fundingInvoice.item.enrolment.contact)}
          items={fundingInvoice.item.enrolment.items}
          itemTotal={fundingInvoice ? fundingInvoice.total : 0}
          currencySymbol={currency && currency.shortCurrencySymbol}
          dispatch={dispatch}
          paymentPlans={fundingInvoice ? fundingInvoice.paymentPlans : []}
          selectedItemIndex={selectedItemIndex}
          form={form}
        />
      </Grid>
      <Grid container>
        {fundingInvoice && fundingInvoice.paymentPlans && (
          <Grid item sm={6} className="pr-2">
            <CheckoutFundingInvoicePaymentPlans
              name={`fundingInvoices[${selectedItemIndex}].paymentPlans`}
              currency={currency}
              syncErrors={syncErrors}
              form={CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM}
              dispatch={dispatch}
              total={fundingInvoice ? fundingInvoice.total : 0}
            />
          </Grid>
        )}
        {fundingInvoice && fundingInvoice.trainingPlans && fundingInvoice.trainingPlans.length > 0 && (
          <Grid item sm={6}>
            <div className="centeredFlex">
              <Typography className="heading pt-1 pb-1">
                Training Plan
              </Typography>
            </div>
            <FieldArray
              name={`fundingInvoices[${selectedItemIndex}].trainingPlans`}
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
  currency: state.currency
});

export default connect<any, any, any>(
  mapStateToProps
)(withStyles((theme: AppTheme) => ({ ...summaryListStyles(theme), ...styles(theme) }))(CheckoutFundingInvoiceSummaryList));
