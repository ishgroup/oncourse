/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Collapse } from '@mui/material';
import FormControlLabel from '@mui/material/FormControlLabel';
import clsx from 'clsx';
import React from 'react';
import { connect } from 'react-redux';
import { DecoratedFormProps, getFormSyncErrors, getFormValues, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../common/components/layout/AppBarContainer';
import { CheckoutFundingInvoice } from '../../../../model/checkout/fundingInvoice';
import { State } from '../../../../reducers/state';
import { formatFundingSourceId } from '../../../entities/common/utils';
import CheckoutAppBar from '../CheckoutAppBar';
import CheckoutFundingInvoiceSummaryList, {
  CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM
} from './CheckoutFundingInvoiceSummaryList';

const styles = (() => ({
  fundingInvoiceSourceId: {
    marginTop: -12,
    flex: 0.5
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

const validateFundingInvoices = (fundingInvoices: CheckoutFundingInvoice[]) => {
  let errors;
  fundingInvoices?.forEach((fi, index) => {
    if (fi.trackAmountOwing && !fi.fundingProviderId) {
      if (!Array.isArray(errors)) {
        errors = [];
      }
      errors[index] = { fundingProviderId: "Funding provider is required" };
    }
  });
  return errors;
};

const CheckoutFundingInvoiceFormBase = React.memo<Props>(props => {
  const {
    classes,
    syncErrors,
    dispatch,
    form,
    activeField,
    titles,
    contracts,
    fundingInvoices = []
  } = props;

  const selectedItemIndex = fundingInvoices?.findIndex(i => i.active);
  
  return (
    <AppBarContainer
      hideHelpMenu
      hideSubmitButton
      disableInteraction
      title={(
        <CheckoutAppBar title={titles[activeField]} />
      )}
    >
      <form autoComplete="off">
        <FormField type="stub" name="fundingInvoices" validate={validateFundingInvoices} />
        {selectedItemIndex !== -1 && (
        <div>
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
                items={contracts}
                format={formatFundingSourceId}
                inline
              />
            </div>
              )}
          </div>
          <Collapse in={fundingInvoices[selectedItemIndex]?.trackAmountOwing} mountOnEnter unmountOnExit>
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
    </AppBarContainer>
  );
});

const mapStateToProps = (state: State) => ({
  fundingInvoices: (getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state) as any)?.fundingInvoices,
  syncErrors: getFormSyncErrors(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state),
  contracts: state.export.contracts,
});

export default reduxForm<{ fundingInvoices?: CheckoutFundingInvoice[] }, any>({
  form: CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM,
  initialValues: {
    fundingInvoices: []
  }
})(withStyles(
  connect<any, any, any>(mapStateToProps, null)(CheckoutFundingInvoiceFormBase),
  styles
));
