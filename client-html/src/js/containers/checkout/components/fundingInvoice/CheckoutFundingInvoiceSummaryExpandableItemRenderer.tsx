/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { change } from "redux-form";
import debounce from "lodash.debounce";
import clsx from "clsx";
import Accordion from "@material-ui/core/Accordion";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { formatCurrency, normalizeNumberToZero } from "../../../../common/utils/numbers/numbersNormalizing";

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
    form
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
                classes={classes}
                dispatch={dispatch}
                paymentPlans={paymentPlans}
                form={form}
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
    classes, dispatch, listIndex, index, item, paymentPlans, form
  } = props;

  const handlePriceChange = React.useCallback<any>(debounce((e, price) => {
    dispatch(change(form, "paymentPlans[0].amount", price));
    if (paymentPlans && paymentPlans.length) {
      dispatch(change(form,
        `paymentPlans[${paymentPlans.length - 1}].amount`,
        price));
    }
  }, 500), [listIndex, index, paymentPlans]);

  React.useEffect(() => {
    dispatch(change(form, "total", 0));
  }, [item.id]);

  return (
    <Grid item xs={12} container alignItems="center" direction="row" className={clsx(classes.tableTabRow, classes.tableTab)}>
      <Grid item xs={9}>
        <div className="centeredFlex">
          <Typography variant="body1">{item.class.name}</Typography>
        </div>
      </Grid>
      <Grid item container xs={3} justify="flex-end">
        <FormField
          type="money"
          name="total"
          label="Price"
          className="pl-2 text-end"
          onChange={handlePriceChange}
          normalize={normalizeNumberToZero}
          rightAligned
        />
      </Grid>
    </Grid>
  );
});

export default CheckoutFundingInvoiceSummaryExpandableItemRenderer;
