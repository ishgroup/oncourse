/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Chip, FormControlLabel, Grid, Paper, Typography } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import {
  BooleanArgFunction,
  D_MMM_YYYY,
  formatCurrency,
  LinkAdornment,
  openInternalLink,
  StyledCheckbox
} from 'ish-ui';
import React from 'react';
import { connect } from 'react-redux';
import { withStyles } from 'tss-react/mui';
import AppBarContainer from '../../../../common/components/layout/AppBarContainer';
import { CheckoutPreviousInvoice, PreviousInvoiceState } from '../../../../model/checkout';
import { State } from '../../../../reducers/state';
import {
  checkoutSetPreviousOwingPayDue,
  checkoutTogglePreviousInvoice,
  checkoutUncheckAllPreviousInvoice
} from '../../actions/checkoutSummary';
import { summaryListStyles } from '../../styles/summaryListStyles';
import CheckoutAppBar from '../CheckoutAppBar';

export const CheckoutPreviousInvoiceListFormRole: string = "CheckoutPreviousInvoiceListform";

interface InvoiceItemRowProps {
  classes: any;
  item: CheckoutPreviousInvoice;
  toggleInvoiceItem: any;
  currencySymbol: string;
  payDueAmounts: boolean;
}

const InvoiceItemRow: React.FC<InvoiceItemRowProps> = (
  {
    classes,
    item,
    toggleInvoiceItem,
    currencySymbol,
    payDueAmounts
  }
) => (
  <Grid item xs={12} container alignItems="center" direction="row" className={classes.tableTab}>
    <div className={clsx("centeredFlex flex-fill", classes.itemTitle)}>
      <StyledCheckbox name={`previousInvoiceCheckbox[${item.id}]`} checked={item.checked} onChange={toggleInvoiceItem} />
      <Typography variant="body1" className={clsx("mr-1", !item.checked && "disabled")}>
        {`${item.invoiceDate && format(new Date(item.invoiceDate), D_MMM_YYYY)} invoice ${item.invoiceNumber}`}
      </Typography>
      <LinkAdornment
        linkHandler={() => openInternalLink(`/invoice/${item.id}`)}
        link={item.id}
        className="appHeaderFontSize"
      />
      <Typography variant="body1" className={clsx("ml-auto", !item.checked && "disabled")}>
        {payDueAmounts
          ? item.overdue
            ? <span className="errorColor">{$t('overdue2')}</span>
            : (item.dateDue && `due ${format(new Date(item.dateDue), D_MMM_YYYY)}`)
          : ""}
      </Typography>
      <div className={clsx("money text-end ml-3", classes.summaryItemPrice, !item.checked && "disabled")}>
        {formatCurrency(
          payDueAmounts
            ? Math.abs(item.nextDue)
            : Math.abs(item.amountOwing),
          currencySymbol
        )}
      </div>
    </div>
  </Grid>
);

interface Props {
  classes?: any;
  activeField?: any;
  titles?: any;
  previousInvoices?: PreviousInvoiceState;
  toggleInvoiceItem?: (index: number, type: string) => void;
  uncheckAllPreviousInvoice?: (type: string, value?: boolean) => void;
  currencySymbol?: string;
  setPayDue?: BooleanArgFunction;
}

const CheckoutPreviousInvoiceList: React.FC<Props> = props => {
  const {
    classes,
    activeField,
    titles,
    previousInvoices,
    toggleInvoiceItem,
    currencySymbol,
    uncheckAllPreviousInvoice,
    setPayDue
  } = props;

  return (
    <form autoComplete="off" className="appFrame flex-fill root" role={CheckoutPreviousInvoiceListFormRole}>
      <AppBarContainer
        hideHelpMenu
        hideSubmitButton
        disableInteraction
        title={(
          <CheckoutAppBar title={activeField && titles[activeField]} />
        )}
        containerClass="p-3"
      >
        <div className="w-100">
          <FormControlLabel
            classes={{
              root: "checkbox"
            }}
            control={(
              <StyledCheckbox
                name="isPayDueAmounts"
                checked={previousInvoices.payDueAmounts}
                onChange={e => setPayDue(e.target.checked)}
              />
            )}
            label={$t('pay_due_amounts')}
            className="mb-1 pl-1"
          />
          <Paper elevation={0} className="p-3">
            <div className="d-flex">
              <Chip
                size="small"
                className="mb-1"
                onClick={() => uncheckAllPreviousInvoice(activeField, !previousInvoices.unCheckAll)}
                label={previousInvoices.unCheckAll ? "Check All" : "Uncheck All"}
              />
              <div className="flex-fill" />
              <Typography variant="body2" className={classes.topRightlabel}>
                {previousInvoices.payDueAmounts ? "Next due" : "Total owing"}
              </Typography>
            </div>

            <Grid container>
              {previousInvoices.invoices.map((item, index) => (
                <InvoiceItemRow
                  key={index}
                  classes={classes}
                  item={item}
                  toggleInvoiceItem={() => toggleInvoiceItem(index, activeField)}
                  currencySymbol={currencySymbol}
                  payDueAmounts={previousInvoices.payDueAmounts}
                />
              ))}
              <Grid item xs={12} container direction="row" className={classes.tableTab}>
                <Grid item xs={8} />
                <Grid
                  item
                  xs={4}
                  container
                  direction="row-reverse"
                  className={clsx("pt-1", "summaryTopBorder", classes.summaryItemPrice)}
                >
                  <Typography variant="body2" className="money">
                    {formatCurrency(
                      previousInvoices.invoiceTotal < 0 ? -previousInvoices.invoiceTotal : previousInvoices.invoiceTotal,
                      currencySymbol
                    )}
                  </Typography>
                </Grid>
              </Grid>
            </Grid>
          </Paper>
        </div>
      </AppBarContainer>
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  currencySymbol: state.location.currency && state.location.currency.shortCurrencySymbol
});

const mapDispatchToProps = dispatch => ({
  toggleInvoiceItem: (index, type) => dispatch(checkoutTogglePreviousInvoice(index, type)),
  uncheckAllPreviousInvoice: (type, value) => dispatch(checkoutUncheckAllPreviousInvoice(type, value)),
  setPayDue: val => dispatch(checkoutSetPreviousOwingPayDue(val))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(CheckoutPreviousInvoiceList, summaryListStyles));
