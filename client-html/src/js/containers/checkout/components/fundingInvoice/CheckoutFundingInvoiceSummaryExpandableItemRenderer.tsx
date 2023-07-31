/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { change } from "redux-form";
import debounce from "lodash.debounce";
import clsx from "clsx";
import Accordion from "@mui/material/Accordion";
import AccordionDetails from "@mui/material/AccordionDetails";
import AccordionSummary from "@mui/material/AccordionSummary";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import FormField from "../../../../common/components/form/formFields/FormField";
import { formatCurrency, normalizeNumberToZero, decimalPlus } from "ish-ui";

const CheckoutFundingInvoiceSummaryExpandableItemRenderer = React.memo<any>(props => {
  const {
    dispatch,
    classes,
    header,
    listIndex,
    items,
    itemTotal,
    currencySymbol,
    paymentPlans,
    form,
    selectedItemIndex
  } = props;

  const [expanded, setExpanded] = React.useState(true);

  const handleChange = React.useCallback((event, expanded) => {
    setExpanded(expanded);
  }, []);

  return (
    <div className={classes.root}>
      <Accordion
        expanded={expanded}
        onChange={handleChange}
        className={classes.panel}
      >
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Grid container className="centeredFlex">
            <div className="centeredFlex flex-fill">
              <div className="heading mr-2">{header}</div>
            </div>
            {!expanded && (
              <Typography variant="body1" className={clsx(classes.headerItem, "money")}>
                {formatCurrency(itemTotal, currencySymbol)}
              </Typography>
            )}
          </Grid>
        </AccordionSummary>
        <AccordionDetails>
          <Grid container>
            {items && items.map((item, index) => (
              <CheckoutFundingInvoiceSummaryRow
                key={index}
                index={index}
                listIndex={listIndex}
                item={item}
                items={items}
                classes={classes}
                dispatch={dispatch}
                paymentPlans={paymentPlans}
                form={form}
                selectedItemIndex={selectedItemIndex}
              />
            ))}
          </Grid>
        </AccordionDetails>
      </Accordion>
    </div>
  );
});

const CheckoutFundingInvoiceSummaryRow = React.memo<any>(props => {
  const {
    classes, dispatch, listIndex, index, item, items, paymentPlans, form, selectedItemIndex
  } = props;

  const handlePriceChange = React.useCallback<any>(debounce((e, price) => {
    const totalAmount = items.reduce((p, c, ind) => decimalPlus(p, ind === index ? price : c.totalFee || 0), 0);
    dispatch(change(form, `fundingInvoices[${selectedItemIndex}].paymentPlans[0].amount`, totalAmount));
    dispatch(change(form, `fundingInvoices[${selectedItemIndex}].total`, totalAmount));
    if (paymentPlans && paymentPlans.length) {
      dispatch(change(form,
        `fundingInvoices[${selectedItemIndex}].paymentPlans[${paymentPlans.length - 1}].amount`,
        totalAmount));
    }
  }, 500), [listIndex, index, paymentPlans, selectedItemIndex, items]);

  return (
    <Grid item xs={12} container alignItems="center" direction="row" className={clsx(classes.tableTabRow, classes.tableTab)}>
      <Grid item xs={9}>
        <div className="centeredFlex">
          <Typography variant="body1">{item.class.name}</Typography>
        </div>
      </Grid>
      <Grid item xs={3}>
        <FormField
          type="money"
          name={`fundingInvoices[${selectedItemIndex}].item.enrolment.items[${index}].totalFee`}
          label="Price"
          onChange={handlePriceChange}
          normalize={normalizeNumberToZero}
          debounced={false}
          rightAligned
        />
      </Grid>
    </Grid>
  );
});

export default CheckoutFundingInvoiceSummaryExpandableItemRenderer;
