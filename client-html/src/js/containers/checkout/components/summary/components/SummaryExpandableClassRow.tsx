/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
import IconButton from "@material-ui/core/IconButton";
import Lock from "@material-ui/icons/Lock";
import LockOpen from "@material-ui/icons/LockOpen";
import React from "react";
import clsx from "clsx";
import { Dispatch } from "redux";
import { change } from "redux-form";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import Accordion from "@material-ui/core/Accordion";
import { EnrolmentStudyReason } from "@api/model";
import debounce from "lodash.debounce";
import { showConfirm } from "../../../../../common/actions";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { mapSelectItems } from "../../../../../common/utils/common";
import {
  formatCurrency
} from "../../../../../common/utils/numbers/numbersNormalizing";
import { CheckoutCourse } from "../../../../../model/checkout";
import { checkoutUpdateSummaryItem, checkoutChangeSummaryItemField } from "../../../actions/checkoutSummary";
import { CHECKOUT_SUMMARY_FORM as summmaryForm } from "../CheckoutSummaryList";

const enrolmentStudyReasonItems = Object.keys(EnrolmentStudyReason).map(mapSelectItems);

interface Props {
  dispatch: Dispatch<any>;
  item: CheckoutCourse;
  itemOriginalPrice: number;
  index: number;
  listIndex: number;
  toggleSummaryItem: any;
  currencySymbol: string;
  classes?: any;
  values?: any;
  updatePrices?: any;
}

const SummaryExpandableClassRow = React.memo<Props>(props => {
  const {
    dispatch,
    classes,
    item,
    itemOriginalPrice,
    index,
    listIndex,
    toggleSummaryItem,
    currencySymbol,
    updatePrices
  } = props;

  const [expanded, setExpanded] = React.useState(false);
  const [priceLocked, setPriceLocked] = React.useState(!item.priceOverriden);

  React.useEffect(() => {
    dispatch(change(summmaryForm, `${item.id}_${listIndex}_itemQuantity`, item.quantity));
    dispatch(change(summmaryForm, `${item.id}_${listIndex}_studyReason`, item.studyReason));
    dispatch(change(summmaryForm, `${item.id}_${listIndex}_discount`, item.discount));
    dispatch(change(summmaryForm, `${item.id}_${listIndex}_price`, item.priceOverriden || item.price));
  }, []);

  const handleChange = React.useCallback((event, expanded) => {
    setExpanded(expanded);
  }, []);

  const handleToggle = React.useCallback(event => {
    toggleSummaryItem(event);
    updatePrices();
  }, []);

  const handleStudyReasonChange = React.useCallback(reason => {
    dispatch(checkoutChangeSummaryItemField(listIndex, index, reason, "studyReason"));
  }, [listIndex, index]);

  const onDiscountChange = discount => {
    if (!discount) {
      setPriceLocked(true);
      dispatch(change(summmaryForm, `${item.id}_${listIndex}_price`, null));
    }
    dispatch(checkoutUpdateSummaryItem(listIndex, { ...item, discount, priceOverriden: discount ? item.priceOverriden : null } as any));
    updatePrices(true);
  };

  const handlePriceChange = React.useCallback<any>(debounce((e, price) => {
    dispatch(checkoutChangeSummaryItemField(listIndex, index, price, "priceOverriden"));
    updatePrices(true);
  }, 500), [listIndex, index]);

  const onPriceLockClick = () => {
    if (priceLocked && !item.discount) {
      dispatch(showConfirm(
        null,
        "Please select discount before editing price",
        null,
        null,
        null,
        "OK"
      ));
      return;
    }

    if (!priceLocked) {
      dispatch(checkoutChangeSummaryItemField(listIndex, index, null, "priceOverriden"));
      updatePrices(true);
    }
    dispatch(change(summmaryForm, `${item.id}_${listIndex}_price`, item.price));
    setPriceLocked(!priceLocked);
  };

  const priceAdormnet = React.useMemo(
    () => (
      <span>
        <IconButton className="inputAdornmentButton" onClick={onPriceLockClick}>
          {priceLocked ? <Lock className="inputAdornmentIcon" /> : <LockOpen className="inputAdornmentIcon" />}
        </IconButton>
      </span>
    ),
    [priceLocked, item]
  );

  return (
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
          expandIcon: classes.expandIcon
        }}
        expandIcon={<ExpandMoreIcon />}
      >
        <Grid container className="centeredFlex">
          <Grid item xs={6}>
            <div className={clsx("centeredFlex", classes.itemTitle)}>
              <StyledCheckbox
                checked={item.checked}
                onChange={handleToggle}
                onClick={e => e.stopPropagation()}
                onFocus={e => e.stopPropagation()}
              />
              <Typography variant="body1">
                {item.class.name}
              </Typography>
            </div>
          </Grid>

          <Grid item xs={3} className="money text-end">
            {item.checked && !expanded && item.discount && (
              <span className={classes.originalPrice}>{formatCurrency(itemOriginalPrice, currencySymbol)}</span>
            )}
          </Grid>

          <Grid item xs={3} className={clsx("money text-end", !item.checked && "disabled")}>
            {!expanded && formatCurrency(item.price ? item.price : "0.00", currencySymbol)}
          </Grid>
        </Grid>
      </AccordionSummary>
      <AccordionDetails>
        <Grid container>
          {item.class.isVet && (
            <Grid item xs={12}>
              <FormField
                type="select"
                name={`${item.id}_${listIndex}_studyReason`}
                label="Study reason"
                items={enrolmentStudyReasonItems}
                defaultValue={item.studyReason || "Not stated"}
                onChange={handleStudyReasonChange}
              />
            </Grid>
          )}

          <Grid item xs={12} lg={4}>
            <FormField
              type="select"
              name={`${item.id}_${listIndex}_discount`}
              label="Discount"
              returnType="object"
              selectValueMark="id"
              selectLabelMark="name"
              items={item.discounts}
              disabled={!item.discounts.length}
              onChange={onDiscountChange}
              placeholder="No discount"
              disabledTab
              allowEmpty
              classes={{
                placeholderContent: classes.discountPlaceholder
              }}
            />
          </Grid>
          <Grid item container xs={12} lg={8} justify="flex-end">
            <Grid item container sm={3} justify="flex-end">
              <Uneditable label="Price (ex tax)" className="text-end" value={item.priceExTax} money />
            </Grid>
            <Grid item container sm={3} justify="flex-end">
              <Uneditable label="Discount (ex tax)" className="text-end" value={item.discountExTax} money />
            </Grid>
            <Grid item container sm={3} justify="flex-end">
              <Uneditable label="Tax" className="text-end" value={item.taxAmount} money />
            </Grid>
            <Grid item container sm={3} justify="flex-end">
              {priceLocked ? <Uneditable label="Total" className="text-end" labelAdornment={priceAdormnet} value={item.price} money />
              : (
                <FormField
                  type="money"
                  name={`${item.id}_${listIndex}_price`}
                  label="Total"
                  className="pl-2 text-end"
                  onChange={handlePriceChange}
                  labelAdornment={priceAdormnet}
                  rightAligned
                />
              )}
            </Grid>
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
});

export default SummaryExpandableClassRow;
