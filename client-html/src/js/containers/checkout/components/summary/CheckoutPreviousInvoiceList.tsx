/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { format } from "date-fns";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Paper from "@material-ui/core/Paper";
import { StyledCheckbox } from "../../../../common/components/form/form-fields/CheckboxField";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import { openInternalLink } from "../../../../common/utils/links";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { State } from "../../../../reducers/state";
import { checkoutTogglePreviousInvoice, checkoutUncheckAllPreviousInvoice } from "../../actions/checkoutSummary";
import { summaryListStyles } from "../../styles/summaryListStyles";
import { AppBarTitle } from "../CheckoutSelection";

interface Props {
  classes?: any;
  activeField?: any;
  titles?: any;
  previousInvoices?: any;
  toggleInvoiceItem?: (index: number, type: string) => void;
  uncheckAllPreviousInvoice?: (type: string, value?: boolean) => void;
  currencySymbol?: string;
}

const CheckoutPreviousInvoiceList: React.FC<Props> = props => {
  const {
    classes, activeField, titles, previousInvoices, toggleInvoiceItem, currencySymbol, uncheckAllPreviousInvoice
  } = props;

  return (
    <div className="appFrame flex-fill root">
      <CustomAppBar>
        <AppBarTitle title={activeField && titles[activeField]} />
      </CustomAppBar>
      <div className="appBarContainer w-100 p-3">
        <Paper elevation={0} className="p-3">
          <FormControlLabel
            classes={{
              root: "checkbox"
            }}
            control={(
              <StyledCheckbox
                checked={previousInvoices.unCheckAll}
                onChange={e => uncheckAllPreviousInvoice(activeField, e.target.checked)}
                className="d-none"
              />
            )}
            label={previousInvoices.unCheckAll ? "Check All" : "Uncheck All"}
            className="mb-1 pl-1"
          />
          <Grid container>
            {previousInvoices.invoices.map((item, index) => (
              <InvoiceItemRow
                key={index}
                classes={classes}
                item={item}
                toggleInvoiceItem={() => toggleInvoiceItem(index, activeField)}
                currencySymbol={currencySymbol}
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

const InvoiceItemRow: React.FC<any> = props => {
  const {
    classes, item, toggleInvoiceItem, currencySymbol
  } = props;

  return (
    <Grid item xs={12} container alignItems="center" direction="row" className={classes.tableTab}>
      <Grid item xs={9}>
        <div className={clsx("centeredFlex", classes.itemTitle)}>
          <StyledCheckbox checked={item.checked} onChange={toggleInvoiceItem} />
          <Typography variant="body1" className="mr-1">
            {`${item.invoiceDate && format(new Date(item.invoiceDate), D_MMM_YYYY)} invoice ${item.invoiceNumber}`}
            {" "}
            {item.dateDue && `(due ${format(new Date(item.dateDue), D_MMM_YYYY)})`}
          </Typography>
          <LinkAdornment
            linkHandler={() => openInternalLink(`/invoice/${item.id}`)}
            link={item.id}
            className="appHeaderFontSize"
          />
        </div>
      </Grid>
      <Grid item xs={3} className={clsx("money text-end", classes.summaryItemPrice, !item.checked && "disabled")}>
        {formatCurrency(item.amountOwing ? item.amountOwing < 0 ? -item.amountOwing : item.amountOwing : "0.00", currencySymbol)}
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

const mapDispatchToProps = dispatch => ({
  toggleInvoiceItem: (index, type) => dispatch(checkoutTogglePreviousInvoice(index, type)),
  uncheckAllPreviousInvoice: (type: string, value: boolean) => dispatch(checkoutUncheckAllPreviousInvoice(type, value))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(summaryListStyles)(CheckoutPreviousInvoiceList));
