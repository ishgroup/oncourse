/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import { change } from "redux-form";
import FormControlLabel from "@mui/material/FormControlLabel";
import Chip from "@mui/material/Chip";
import Tooltip from "@mui/material/Tooltip";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import Grid from "@mui/material/Grid";
import AccordionDetails from "@mui/material/AccordionDetails";
import Typography from "@mui/material/Typography";
import { StyledCheckbox } from "../../../../common/components/form/formFields/CheckboxField";
import FormField from "../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../common/components/form/Uneditable";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { greaterThanNullValidation } from "../../../../common/utils/validation";
import { CheckoutItem } from "../../../../model/checkout";
import { changeSummaryItemQuantity, checkoutUpdateSummaryItem } from "../../actions/checkoutSummary";
import SummaryExpandableClassRow from "./components/SummaryExpandableClassRow";
import { CHECKOUT_SUMMARY_FORM as summmaryForm } from "./CheckoutSummaryList";

export const SummaryItemRow = React.memo<any>(props => {
  const {
   classes, item, header, index, listIndex, itemOriginalPrice, toggleSummaryItem, currencySymbol, dispatch, updatePrices
  } = props;

  const [expanded, setExpanded] = React.useState(false);

  const handleChange = React.useCallback((event, expanded) => {
    setExpanded(expanded);
  }, []);

  React.useEffect(() => {
    dispatch(change(summmaryForm, `${item.id}_${listIndex}_itemQuantity`, item.quantity));

    if (item.validTo) {
      dispatch(change(summmaryForm, `${item.id}_${listIndex}_validTo`, item.validTo));
    }

    if (item.type === "voucher") {
      dispatch(change(summmaryForm, `${item.id}_${listIndex}_restrictToPayer`, item.restrictToPayer));
      dispatch(change(summmaryForm, `${item.id}_${listIndex}_price`, item.price));
    }
  }, [listIndex]);

  const handleQuantityChange = React.useCallback(e => {
    if (e.target.value >= 1) {
      dispatch(changeSummaryItemQuantity(listIndex, index, e.target.value));
      updatePrices();
    }
  }, [listIndex, index]);

  const handleValidToChange = React.useCallback(validTo => {
    dispatch(checkoutUpdateSummaryItem(listIndex, { ...item, validTo }));
  }, [listIndex, index, item]);

  const handleRestrictToPayerChange = React.useCallback(restrictToPayer => {
    dispatch(checkoutUpdateSummaryItem(listIndex, { ...item, restrictToPayer }));
  }, [listIndex, index, item]);

  const handlePriceChange = React.useCallback(price => {
    dispatch(checkoutUpdateSummaryItem(listIndex, { ...item, price }));
    updatePrices();
  }, [listIndex, index, item]);

  const handleToggle = React.useCallback(e => {
    toggleSummaryItem(e);
    updatePrices();
  }, []);

  const isProduct = item.type === "product";

  const headerContent = (
    <Grid item xs={12} container alignItems="center" direction="row" className={clsx(isProduct && classes.tableTab)}>
      <Grid item xs={9}>
        <div className={clsx("centeredFlex", classes.itemTitle)}>
          <StyledCheckbox
            onClick={e => e.stopPropagation()}
            onFocus={e => e.stopPropagation()}
            checked={item.checked}
            onChange={handleToggle}
          />
          {item.type === "product" && (
          <div className={clsx("mr-1 text-end", !item.checked && "disabled")}>
            <FormField
              type="number"
              min="1"
              name={`${item.id}_${listIndex}_itemQuantity`}
              inline
              onChange={handleQuantityChange}
              validate={greaterThanNullValidation}
              defaultValue={item.quantity}
              className={classes.itemPriceInput}
            />
          </div>
          )}
          <Typography variant="body1">
            {item.name}
            {" "}
            {item.voucherCode && `(${item.voucherCode})`}
          </Typography>
        </div>
      </Grid>
      <Grid
        item
        xs={3}
        className={clsx("money text-end", !item.checked && "disabled", isProduct && classes.summaryItemPrice)}
      >

        {item.type === "voucher" && itemOriginalPrice === null
          ? (
            <div
              onClick={e => e.stopPropagation()}
              onFocus={e => e.stopPropagation()}
            >
              <FormField
                type="money"
                name={`${item.id}_${listIndex}_price`}
                validate={greaterThanNullValidation}
                onChange={handlePriceChange}
                
                rightAligned
              />
            </div>
            )
          : formatCurrency(item.price ? item.price : "0.00", currencySymbol)}
      </Grid>
    </Grid>
);

  const bodyContent = React.useMemo(() => {
    if (item.type === "voucher") {
      return (
        <>
          <Grid item xs={4}>
            <FormField
              type="date"
              name={`${item.id}_${listIndex}_validTo`}
              label="Expires on"
              onChange={handleValidToChange}
              required
            />
          </Grid>
          <Grid item xs={8}>
            <FormControlLabel
              classes={{
              root: "checkbox"
            }}
              control={(
                <FormField
                  type="checkbox"
                  name={`${item.id}_${listIndex}_restrictToPayer`}
                  onChange={handleRestrictToPayerChange}
                  color="secondary"
                />
              )}
              label={`Notify ${header} when voucher is redeemed`}
            />
          </Grid>
        </>
      );
    }
    if (item.type === "membership") {
      return (
        <Grid item xs={12}>
          {item.expiryType === "Never (Lifetime)" ? (
            <Uneditable value="lifetime" label="Valid to" />
          ) : (
            <FormField
              name={`${item.id}_${listIndex}_validTo`}
              label="Valid to"
              type="date"
              onChange={handleValidToChange}
              disabled={item.expiryType === "Never (Lifetime)"}
              required
            />
          )}
        </Grid>
      );
    }

    return null;
  }, [item, listIndex, header]);

  return isProduct ? headerContent : (
    <Accordion
      expanded={expanded}
      onChange={handleChange}
      classes={{ root: classes.summaryPanelRoot, expanded: classes.panelExpanded }}
      className={clsx(classes.panel, classes.tableTab)}

    >
      <AccordionSummary
        classes={{
          root: classes.expansionSummaryRoot,
          content: classes.expansionSummaryContent,
          expandIconWrapper: classes.expandIcon
        }}
        expandIcon={<ExpandMoreIcon />}
      >
        {headerContent}
      </AccordionSummary>
      <AccordionDetails>
        <Grid container columnSpacing={3}>
          {bodyContent}
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
});

interface CheckoutSummaryExpandableProps {
  header: any;
  classes?: any;
  items?: CheckoutItem[];
  originalItems?: CheckoutItem[];
  contact?: any;
  listIndex?: any;
  itemTotal?: number;
  toggleSummaryItem?: (itemIndex: number) => void;
  currencySymbol?: string;
  dispatch?: any;
  isPayer?: boolean;
  onPayerChange?: any;
  voucherItems?: any[];
  voucherItemsTotal?: any;
  toggleVoucherItem?: any;
  sendInvoice?: boolean;
  sendEmail?: boolean;
  onToggleSendContext?: (type: string) => void;
  updatePrices?: any;
  hasVoucherLinkedToPayer?: boolean;
}

const CheckoutSummaryExpandableItemRenderer = React.memo<CheckoutSummaryExpandableProps>(props => {
  const {
    dispatch,
    classes,
    header,
    items,
    itemTotal,
    contact,
    toggleSummaryItem,
    currencySymbol,
    listIndex,
    isPayer,
    onPayerChange,
    voucherItems,
    toggleVoucherItem,
    onToggleSendContext,
    sendInvoice,
    sendEmail,
    originalItems,
    updatePrices,
    hasVoucherLinkedToPayer
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
        <AccordionSummary className={classes.summaryExpansionPanel} expandIcon={<ExpandMoreIcon />}>
          <Grid container className="centeredFlex">
            <div className="centeredFlex flex-fill">
              <div className="heading mr-2">{header}</div>
              {isPayer ? (
                <>
                  <Chip
                    size="small"
                    label="Payer"
                    color="primary"
                    clickable={false}
                    className={classes.payerChip}
                    classes={{ label: classes.payerChipLabel }}
                  />

                  <Tooltip
                    title="No email address"
                    disableFocusListener={contact.email}
                    disableHoverListener={contact.email}
                    disableTouchListener={contact.email}
                  >
                    <FormControlLabel
                      control={(
                        <StyledCheckbox
                          checked={sendInvoice}
                          value="primary"
                          onChange={() => onToggleSendContext("invoice")}
                          disabled={!contact.email}
                        />
                      )}
                      onClick={e => e.stopPropagation()}
                      onFocus={e => e.stopPropagation()}
                      label="Send invoice"
                    />
                  </Tooltip>

                </>
                  ) : (
                    <Tooltip
                      disableHoverListener={!hasVoucherLinkedToPayer}
                      disableFocusListener={!hasVoucherLinkedToPayer}
                      disableTouchListener={!hasVoucherLinkedToPayer}
                      title="Payer is locked due to voucher being redeemed"
                    >
                      <div>
                        <Chip
                          variant="outlined"
                          size="small"
                          label="Set as payer"
                          color="primary"
                          className={classes.setPayerChip}
                          classes={{ label: classes.payerChipLabel }}
                          onClick={onPayerChange}
                          disabled={hasVoucherLinkedToPayer}
                          onFocus={e => e.stopPropagation()}
                        />
                      </div>
                    </Tooltip>

                  )}
              <Tooltip
                title="No email address"
                disableFocusListener={contact.email}
                disableHoverListener={contact.email}
                disableTouchListener={contact.email}
              >
                <FormControlLabel
                  control={(
                    <StyledCheckbox
                      checked={sendEmail}
                      value="primary"
                      onChange={() => onToggleSendContext("email")}
                      disabled={!contact.email}
                    />
                  )}
                  onClick={e => e.stopPropagation()}
                  onFocus={e => e.stopPropagation()}
                  label="Send confirmation email"
                />
              </Tooltip>
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
            {items.map((item, index) => {
                  const itemOriginalPrice = originalItems.find(i => i.id === item.id).price;

                  if (item.type === "course") {
                    if (!item.class) {
                      return null;
                    }

                    return (
                      <SummaryExpandableClassRow
                        key={index}
                        item={item}
                        itemOriginalPrice={itemOriginalPrice}
                        classes={classes}
                        toggleSummaryItem={() => toggleSummaryItem(index)}
                        listIndex={listIndex}
                        index={index}
                        currencySymbol={currencySymbol}
                        dispatch={dispatch}
                        updatePrices={updatePrices}
                      />
                    );
                  }
                  return (
                    <SummaryItemRow
                      key={index}
                      header={header}
                      classes={classes}
                      item={item}
                      listIndex={listIndex}
                      index={index}
                      itemOriginalPrice={itemOriginalPrice}
                      toggleSummaryItem={() => toggleSummaryItem(index)}
                      currencySymbol={currencySymbol}
                      dispatch={dispatch}
                      updatePrices={updatePrices}
                    />
                  );
                })}
            {isPayer && voucherItems.length > 0 && voucherItems.map((voucherItem, index) => {
                  const itemOriginalPrice = originalItems.find(i => i.id === voucherItem.id).price;

                  return (
                    <SummaryItemRow
                      key={index}
                      header={header}
                      itemOriginalPrice={itemOriginalPrice}
                      classes={classes}
                      item={voucherItem}
                      listIndex={0}
                      index={index}
                      toggleSummaryItem={() => toggleVoucherItem(index)}
                      currencySymbol={currencySymbol}
                      dispatch={dispatch}
                      updatePrices={updatePrices}
                    />
                  );
                })}
            <Grid item xs={12} container direction="row" className="mt-1">
              <Grid item xs={8} />
              <Grid
                item
                xs={4}
                container
                direction="row-reverse"
                className={clsx("money pt-1 summaryTopBorder", classes.summaryItemPrice)}
              >
                <Typography variant="body2">{formatCurrency(itemTotal, currencySymbol)}</Typography>
              </Grid>
            </Grid>
          </Grid>
        </AccordionDetails>
      </Accordion>
    </div>
  );
});

export default CheckoutSummaryExpandableItemRenderer;
