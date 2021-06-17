/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { format } from "date-fns";
import clsx from "clsx";
import {
  Chip, Paper, Typography, Grid, withStyles, FormControlLabel
} from "@material-ui/core";
import { StyledCheckbox } from "../../../../common/components/form/form-fields/CheckboxField";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import { openInternalLink } from "../../../../common/utils/links";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { State } from "../../../../reducers/state";
import {
  checkoutSetPreviousOwingPayDue,
  checkoutTogglePreviousInvoice,
  checkoutUncheckAllPreviousInvoice
} from "../../actions/checkoutSummary";
import { summaryListStyles } from "../../styles/summaryListStyles";
import CheckoutAppBar from "../CheckoutAppBar";
import { CheckoutPreviousInvoice, PreviousInvoiceState } from "../../../../model/checkout";
import { BooleanArgFunction } from "../../../../model/common/CommonFunctions";

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
      <StyledCheckbox checked={item.checked} onChange={toggleInvoiceItem} />
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
            ? <span className="errorColor">overdue</span>
            : (item.dateDue && `due ${format(new Date(item.dateDue), D_MMM_YYYY)}`)
          : ""
        }
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
    <div className="appFrame flex-fill root">
      <CustomAppBar>
        <CheckoutAppBar title={activeField && titles[activeField]} />
      </CustomAppBar>
      <div className="appBarContainer w-100 p-3">
        <FormControlLabel
          classes={{
            root: "checkbox"
          }}
          control={(
            <StyledCheckbox
              checked={previousInvoices.payDueAmounts}
              onChange={e => setPayDue(e.target.checked)}
            />
          )}
          label="Pay due amounts"
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
                justify="flex-end"
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
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

const mapDispatchToProps = dispatch => ({
  toggleInvoiceItem: (index, type) => dispatch(checkoutTogglePreviousInvoice(index, type)),
  uncheckAllPreviousInvoice: (type, value) => dispatch(checkoutUncheckAllPreviousInvoice(type, value)),
  setPayDue: val => dispatch(checkoutSetPreviousOwingPayDue(val))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(summaryListStyles)(CheckoutPreviousInvoiceList));
