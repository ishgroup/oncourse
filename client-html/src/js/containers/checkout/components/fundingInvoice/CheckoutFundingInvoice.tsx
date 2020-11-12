/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Collapse } from "@material-ui/core";
import { format } from "date-fns-tz";
import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  change, getFormSyncErrors, reduxForm
} from "redux-form";
import clsx from "clsx";
import { createStyles, withStyles } from "@material-ui/core/styles";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { Switch } from "../../../../common/components/form/form-fields/Switch";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { CheckoutFundingInvoiceItem } from "../../../../model/checkout/fundingInvoice";
import { AnyArgFunction, NoArgFunction, NumberArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import { formatFundingSourceId } from "../../../entities/common/utils";
import {
  checkoutFundingInvoiceAddCompany,
  checkoutFundingInvoiceGetTrainingPlans,
  checkoutFundingInvoiceToggleTrackAmountOwing
} from "../../actions/checkoutFundingInvoice";
import { CHECKOUT_CONTACT_COLUMNS } from "../../constants";
import { AppBarTitle, } from "../CheckoutSelection";
import CheckoutFundingInvoiceSummaryList, { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "./CheckoutFundingInvoiceSummaryList";

const styles = createStyles(() => ({
  fundingInvoiceSourceId: {
    marginTop: -1
  }
}));

interface Props {
  classes?: any;
  syncErrors?: any;
  dispatch?: Dispatch;
  item?: CheckoutFundingInvoiceItem;
  activeField?: string;
  titles?: any;
  trackAmountOwing?: boolean;
  toggleTrackAmountOwing?: NoArgFunction;
  getTrainingPlans?: NumberArgFunction;
  contracts?: any;
  addSelectedCompany?: AnyArgFunction;
  form?: string;
}

const CheckoutFundingInvoice = React.memo<Props>(props => {
  const {
    classes,
    syncErrors,
    dispatch,
    form,
    item,
    activeField,
    titles,
    trackAmountOwing,
    toggleTrackAmountOwing,
    getTrainingPlans,
    contracts,
    addSelectedCompany
  } = props;

  React.useEffect(() => {
    if (item) {
      const itemClass = item.enrolment.items[0].class;
      getTrainingPlans(itemClass.id);
      dispatch(change(form, "relatedFundingSourceId", Number(itemClass.relatedFundingSourceId)));
      dispatch(change(form, "fundingProviderId", itemClass.fundingProviderId));
      dispatch(change(form, "vetPurchasingContractID", itemClass.vetPurchasingContractID));

      if (itemClass.fundingProviderId) {
        EntityService.getPlainRecords(
          "Contact",
          CHECKOUT_CONTACT_COLUMNS,
          `id is ${itemClass.fundingProviderId}`
        ).then(res => {
          const contacts = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS));
          if (contacts[0]) {
            addSelectedCompany(contacts[0]);
          }
        });
      }
    }
  }, [item]);

  return (
    <div className="appFrame flex-fill root">
      <CustomAppBar>
        <AppBarTitle title={titles[activeField]} />
      </CustomAppBar>
      <div className="appBarContainer w-100">
        <form autoComplete="off">
          <div className="p-3">
            <div className="centeredFlex">
              <FormControlLabel
                classes={{
                  root: "pb-2 mr-0"
                }}
                control={(
                  <Switch
                    checked={trackAmountOwing}
                    onChange={toggleTrackAmountOwing}
                    color="primary"
                  />
                )}
                label={
                  `Track the amount owing against funding contract ${
                    !contracts ? "provider" : ""
                  }`
                }
              />
              {contracts && (
                <div className={clsx("ml-0-5", classes.fundingInvoiceSourceId)}>
                  <FormField
                    type="select"
                    selectValueMark="id"
                    selectLabelMark="name"
                    name="relatedFundingSourceId"
                    label="Default funding contract"
                    items={contracts}
                    format={formatFundingSourceId}
                    hideLabel
                  />
                </div>
              )}
            </div>
            <Collapse in={trackAmountOwing} mountOnEnter unmountOnExit>
              {item && (
                <CheckoutFundingInvoiceSummaryList
                  dispatch={dispatch}
                  syncErrors={syncErrors}
                  addSelectedCompany={addSelectedCompany}
                  item={item}
                  form={form}
                />
              )}
            </Collapse>
          </div>
        </form>
      </div>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  trackAmountOwing: state.checkout.fundingInvoice.trackAmountOwing,
  selectedCompanies: state.checkout.fundingInvoice.companies,
  syncErrors: getFormSyncErrors(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state),
  contracts: state.export.contracts,
  item: state.checkout.fundingInvoice.item
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  toggleTrackAmountOwing: () => dispatch(checkoutFundingInvoiceToggleTrackAmountOwing()),
  getTrainingPlans: id => dispatch(checkoutFundingInvoiceGetTrainingPlans(id)),
  addSelectedCompany: company => dispatch(checkoutFundingInvoiceAddCompany(company))
});

export default reduxForm<any, any>({
  form: CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM,
  initialValues: {
    paymentPlans: [
      {
        amount: 0,
        date: format(new Date(), YYYY_MM_DD_MINUSED),
        entityName: "Invoice",
        id: null,
        successful: true,
        type: "Invoice office"
      },
      {
        amount: 0,
        date: format(new Date(), YYYY_MM_DD_MINUSED),
        entityName: "InvoiceDueDate",
        id: null,
        successful: false,
        type: "Payment due"
      }
    ]
  }
})(withStyles(styles)(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CheckoutFundingInvoice)));
