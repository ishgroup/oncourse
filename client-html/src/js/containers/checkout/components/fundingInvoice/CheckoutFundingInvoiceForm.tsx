/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Collapse } from "@material-ui/core";
import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  change,
  DecoratedFormProps, getFormSyncErrors, getFormValues, reduxForm
} from "redux-form";
import clsx from "clsx";
import { createStyles, withStyles } from "@material-ui/core/styles";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { Switch } from "../../../../common/components/form/form-fields/Switch";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import { CheckoutFundingInvoice } from "../../../../model/checkout/fundingInvoice";
import { NoArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import { formatFundingSourceId } from "../../../entities/common/utils";
import { AppBarTitle, } from "../CheckoutSelection";
import CheckoutFundingInvoiceSummaryList, { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "./CheckoutFundingInvoiceSummaryList";
import { usePrevious } from "../../../../common/utils/hooks";
import EntityService from "../../../../common/services/EntityService";
import { CHECKOUT_CONTACT_COLUMNS } from "../../constants";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const styles = createStyles(() => ({
  fundingInvoiceSourceId: {
    marginTop: -1
  }
}));

interface Props extends DecoratedFormProps {
  classes?: any;
  syncErrors?: any;
  activeField?: string;
  titles?: any;
  contracts?: any;
  fundingInvoices?: CheckoutFundingInvoice[];
}

const CheckoutFundingInvoiceFormBase = React.memo<Props>(props => {
  const {
    classes,
    syncErrors,
    dispatch,
    form,
    activeField,
    titles,
    contracts,
    fundingInvoices
  } = props;

  const selectedItemIndex = fundingInvoices.findIndex(i => i.active);
  const prevSelectedItem = usePrevious(selectedItemIndex);

  useEffect(() => {
    if (fundingInvoices.length && prevSelectedItem !== selectedItemIndex) {
      const itemClass = fundingInvoices[selectedItemIndex].item.enrolment.items[0].class;

      dispatch(change(form, `fundingInvoices[${selectedItemIndex}].relatedFundingSourceId`, Number(itemClass.relatedFundingSourceId)));
      dispatch(change(form, `fundingInvoices[${selectedItemIndex}].fundingProviderId`, itemClass.fundingProviderId));
      dispatch(change(form, `fundingInvoices[${selectedItemIndex}].vetPurchasingContractID`, itemClass.vetPurchasingContractID));

      if (itemClass.fundingProviderId) {
        EntityService.getPlainRecords(
          "Contact",
          CHECKOUT_CONTACT_COLUMNS,
          `id is ${itemClass.fundingProviderId}`
        ).then(res => {
          const contacts = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS));
          if (contacts[0]) {
            dispatch(change(form, `fundingInvoices[${selectedItemIndex}].company`, contacts[0]));
          }
        });
      }
    }
  }, [selectedItemIndex]);

  return (
    <div className="appFrame flex-fill root">
      <CustomAppBar>
        <AppBarTitle title={titles[activeField]} />
      </CustomAppBar>
      <div className="appBarContainer w-100">
        <form autoComplete="off">
          {selectedItemIndex !== -1 && (
            <div className="p-3">
              <div className="centeredFlex">
                <FormControlLabel
                  classes={{
                    root: "pb-2 mr-0"
                  }}
                  control={(
                    <FormField
                      type="switch"
                      name={`fundingInvoices[${selectedItemIndex}].trackAmountOwing`}
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
                      name={`fundingInvoices[${selectedItemIndex}].relatedFundingSourceId`}
                      label="Default funding contract"
                      items={contracts}
                      format={formatFundingSourceId}
                      hideLabel
                    />
                  </div>
              )}
              </div>
              <Collapse in={fundingInvoices[selectedItemIndex].trackAmountOwing} mountOnEnter unmountOnExit>
                <CheckoutFundingInvoiceSummaryList
                  dispatch={dispatch}
                  syncErrors={syncErrors}
                  fundingInvoice={fundingInvoices[selectedItemIndex]}
                  selectedItemIndex={selectedItemIndex}
                  form={form}
                />
              </Collapse>
            </div>
          )}
        </form>
      </div>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  fundingInvoices: (getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state) as any).fundingInvoices,
  syncErrors: getFormSyncErrors(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state),
  contracts: state.export.contracts,
});

export default reduxForm<any, any>({
  form: CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM,
  initialValues: {
    fundingInvoices: []
  }
})(withStyles(styles)(connect<any, any, any>(mapStateToProps, null)(CheckoutFundingInvoiceFormBase)));
