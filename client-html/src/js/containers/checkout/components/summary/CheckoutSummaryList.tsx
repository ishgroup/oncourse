/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Grid } from '@mui/material';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { formatCurrency } from 'ish-ui';
import React, { useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { InjectedFormProps, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../common/components/layout/AppBarContainer';
import { CheckoutSummary } from '../../../../model/checkout';
import { State } from '../../../../reducers/state';
import { getContactFullName } from '../../../entities/contacts/utils';
import { toggleSendContext, toggleSummaryItem, toggleVoucherItem } from '../../actions';
import { checkoutSetDefaultPayer } from '../../actions/checkoutSummary';
import { summaryListStyles } from '../../styles/summaryListStyles';
import CheckoutAppBar from '../CheckoutAppBar';
import { CheckoutSummaryCogwheel } from './CheckoutSummaryCogwheel';
import CheckoutSummaryExpandableItemRenderer from './CheckoutSummaryExpandableItemRenderer';

export const CHECKOUT_SUMMARY_FORM = "CHECKOUT_SUMMARY_FORM";

interface Props {
  classes?: any;
  selectedContacts?: any[];
  selectedItems?: any[];
  onChangeStep?: (step: number) => void;
  summary?: CheckoutSummary;
  finalTotal?: number;
  toggleSummaryItem?: (listIndex: number, itemIndex: number) => void;
  handleSubmit?: any;
  currencySymbol?: string;
  dispatch?: any;
  voucherItems?: any[];
  toggleVoucherItem?: (itemIndex: number) => void;
  onToggleSendContext?: (listIndex: number, type: string) => void;
  updatePrices?: any;
  disableDiscounts?: boolean;
}

const CheckoutSummaryListForm: React.FC<Props & InjectedFormProps> = props => {
  const {
    dispatch,
    classes,
    summary,
    toggleSummaryItem,
    finalTotal,
    currencySymbol,
    voucherItems,
    toggleVoucherItem,
    onToggleSendContext,
    selectedItems,
    updatePrices,
    disableDiscounts
  } = props;

  const handlePayerChange = React.useCallback((e, index) => {
    e.stopPropagation();
    setTimeout(() => {
      dispatch(checkoutSetDefaultPayer(index));
    }, 300);
  }, []);

  const onPayerChange = (e, i) => {
    handlePayerChange(e, i);
    updatePrices();
  };

  const hasVoucherLinkedToPayer = useMemo(() => summary.vouchers.some(v => v.redeemableById), [summary.vouchers]);

  const invoiceLinesCount = useMemo(() =>
    summary.list.filter(li => li.items.some(i => i.checked)).length
    + summary.voucherItems.filter(v => v.checked).length,
  [summary.list]);

  return (
    <form autoComplete="off" className="root">
      <AppBarContainer
        hideHelpMenu
        hideSubmitButton
        disableInteraction
        title={(
          <CheckoutAppBar title={`${invoiceLinesCount} invoice line${invoiceLinesCount !== 1 ? "s" : ""}`} />
        )}
        actions={(
          <CheckoutSummaryCogwheel dispatch={dispatch} summary={summary} disableDiscounts={disableDiscounts} />
        )}
      >
        <Grid container>
          <Grid item xs={12}>
            {summary.list.map((list, i) => (
              <CheckoutSummaryExpandableItemRenderer
                key={i}
                header={getContactFullName(list.contact as any)}
                items={list.items}
                originalItems={selectedItems}
                listIndex={i}
                itemTotal={list.itemTotal}
                toggleSummaryItem={itemIndex => toggleSummaryItem(i, itemIndex)}
                currencySymbol={currencySymbol}
                classes={classes}
                dispatch={dispatch}
                contact={list.contact}
                isPayer={list.payer}
                sendInvoice={list.sendInvoice}
                sendEmail={list.sendEmail}
                onPayerChange={e => onPayerChange(e, i)}
                voucherItems={voucherItems}
                toggleVoucherItem={toggleVoucherItem}
                onToggleSendContext={type => onToggleSendContext(i, type)}
                updatePrices={updatePrices}
                hasVoucherLinkedToPayer={hasVoucherLinkedToPayer}
              />
            ))}
          </Grid>

          <Grid item container xs={12} className="mt-3">
            <Grid item xs={false} sm={8} />
            <Grid
              item
              xs={12}
              sm={4}
              className={clsx("money pt-2 summaryTopBorder", classes.itemTotal)}
            >
              <Typography variant="body2">
                <strong>{formatCurrency(finalTotal, currencySymbol)}</strong>
              </Typography>
            </Grid>
          </Grid>

          <Grid item xs={6}>
            <FormField type="text" name="invoiceCustomerReference" label={$t('customer_reference')} />
          </Grid>

          <Grid item xs={6}>
            <FormField type="multilineText" name="invoicePublicNotes" label={$t('public_notes')} multiline  />
          </Grid>
        </Grid>
      </AppBarContainer>
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  selectedContacts: state.checkout.contacts,
  selectedItems: state.checkout.items,
  summary: state.checkout.summary,
  finalTotal: state.checkout.summary.finalTotal,
  currencySymbol: state.location.currency && state.location.currency.shortCurrencySymbol,
  voucherItems: state.checkout.summary.voucherItems,
  disableDiscounts: state.checkout.disableDiscounts
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  toggleSummaryItem: (listIndex: number, itemIndex: number) => dispatch(toggleSummaryItem(listIndex, itemIndex)),
  toggleVoucherItem: (itemIndex: number) => dispatch(toggleVoucherItem(itemIndex)),
  onToggleSendContext: (listIndex: number, type: string) => dispatch(toggleSendContext(listIndex, type))
});

export default reduxForm<any, Props>({
  form: CHECKOUT_SUMMARY_FORM,
  initialValues: {},
  destroyOnUnmount: false
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(CheckoutSummaryListForm, summaryListStyles)));
