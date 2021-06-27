/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { FormControlLabel } from "@material-ui/core";
import { format } from "date-fns-tz";
import React from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { DiscountType } from "@api/model";
import { Dispatch } from "redux";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import Uneditable from "../../../../../common/components/form/Uneditable";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import { III_DD_MMM_YYYY } from "../../../../../common/utils/dates/format";
import { decimalMinus, decimalPlus } from "../../../../../common/utils/numbers/decimalCalculation";
import { CheckoutDiscount, CheckoutSummary } from "../../../../../model/checkout";
import { State } from "../../../../../reducers/state";
import { formatCurrency } from "../../../../../common/utils/numbers/numbersNormalizing";
import { checkoutChangeSummaryItemField, checkoutUpdatePromo } from "../../../actions/checkoutSummary";
import { StyledCourseItemRenderer } from "../../items/components/SelectedItemRenderer";
import CheckoutAppBar from "../../CheckoutAppBar";

const styles = () => createStyles({
    history: {
      paddingBottom: "5px",
      width: "100%"
    },
    historyItem: {
      maxWidth: "400px",
      width: "100%",
      justifyContent: "space-between"
    },
    customPadding: {
      paddingLeft: "20px"
    }
  });

const getDiscountLabel = (selectedDiscount, currencySymbol) => {
  switch (selectedDiscount.discountType as DiscountType) {
    case "Dollar": {
      return `${formatCurrency(parseFloat(selectedDiscount.discountValue), currencySymbol)} discount`;
    }
    case "Fee override": {
      return `${formatCurrency(parseFloat(selectedDiscount.discountValue), currencySymbol)} fee override discount`;
    }
    case "Percent": {
      return `${parseFloat(selectedDiscount.discount.discountPercent) * 100}% discount`;
    }
    default:
      return "";
  }
};

interface Props {
  classes?: any;
  selectedDiscount?: CheckoutDiscount;
  currencySymbol?: string;
  summary?: CheckoutSummary;
  dispatch?: Dispatch;
}

const VoucherView: React.FC<Props> = props => {
  const {
   classes, selectedDiscount, currencySymbol, summary, dispatch
  } = props;

  const isClassVoucher = typeof selectedDiscount.maxCoursesRedemption === "number";

  const currentEnrolments = [];

  summary.list.forEach((l, listIndex) => {
    l.items.forEach((i, index) => {
      if (i.type === "course" && i.checked && selectedDiscount.courseIds.includes(i.courseId)) {
        currentEnrolments.push({
          contact: `${l.contact.firstName} ${l.contact.lastName}`,
          item: i,
          redeemed: i.voucherId && i.voucherId === selectedDiscount.id,
          disabled: i.voucherId && i.voucherId !== selectedDiscount.id,
          listIndex,
          index,
        });
      }
    });
  });

  const redeemedEnrolments = currentEnrolments.filter(en => en.redeemed);

  const enrolmentsLeft = isClassVoucher
    ? selectedDiscount.maxCoursesRedemption - selectedDiscount.redeemedCourses - redeemedEnrolments.length
    : 0;

  const onRedeemVoucher = (checked, enrolment) => {
    dispatch(checkoutChangeSummaryItemField(enrolment.listIndex, enrolment.index, checked ? selectedDiscount.id : null, "voucherId"));
    if (isClassVoucher) {
      dispatch(checkoutUpdatePromo({
        vouchersItem: {
          ...selectedDiscount,
          appliedValue: checked
            ? decimalPlus(selectedDiscount.appliedValue, enrolment.item.price)
            : decimalMinus(selectedDiscount.appliedValue, enrolment.item.price)
        }
      }));
    }
  };

  return (
    <>
      {isClassVoucher
        ? (
          <Grid container>
            <Grid item sm={12} className="mb-2">
              <div className="heading">Apply to</div>
              <Typography variant="caption">
                Can be used for up to
                {' '}
                {enrolmentsLeft}
                {' '}
                enrolments
              </Typography>
            </Grid>

            {currentEnrolments.map((en, i) => (
              <Grid item container alignItems="baseline" sm={12} className={classes.history} key={i}>
                <Grid item xs={2}>
                  <FormControlLabel
                    classes={{ root: "checkbox mt-2" }}
                    control={(
                      <StyledCheckbox
                        checked={en.redeemed}
                        color="secondary"
                        disabled={(!en.redeemed && enrolmentsLeft === 0) || en.disabled}
                        onChange={(e, checked) => onRedeemVoucher(checked, en)}
                      />
                    )}
                    label={en.contact}
                  />
                </Grid>
                <Grid item xs={10}>
                  <StyledCourseItemRenderer item={en.item} />
                </Grid>
              </Grid>
              ))}
          </Grid>
        )
        : (
          <Grid container>
            <Grid item sm={2}>
              <Uneditable value={selectedDiscount.appliedValue} label="Apply now" money />
            </Grid>
            <Grid item sm={4}>
              <Uneditable value={selectedDiscount.availableValue} label="Value remaining" money />
            </Grid>
          </Grid>
      )}

      <Grid container className="pt-2">
        <Grid item sm={4}>
          <Uneditable
            value={format(new Date(selectedDiscount.expiryDate), III_DD_MMM_YYYY)}
            label="Expires on"
          />
        </Grid>
      </Grid>

      <Grid container className="pt-2">
        <div className="heading">History</div>
        <Grid container className="pt-2">
          <Grid item sm={12} className={classes.history}>
            <div className={clsx("centeredFlex", classes.historyItem)}>
              <Grid item xs={4}>
                <Typography variant="body2">Purchase</Typography>
              </Grid>
              <Grid item xs={4}>
                <Typography variant="body2">{format(new Date(selectedDiscount.purchaseDate), III_DD_MMM_YYYY)}</Typography>
              </Grid>
              <span className={classes.customPadding} />
              <Grid item xs={4}>
                <Typography variant="body2" className="money">{formatCurrency(selectedDiscount.purchaseValue, currencySymbol)}</Typography>
              </Grid>
            </div>
          </Grid>

          {selectedDiscount.history.map((h, i) => (
            <Grid item sm={12} className={classes.history} key={i}>
              <div className={clsx("centeredFlex", classes.historyItem)}>
                <Grid item xs={4}>
                  <Typography variant="body2">Redeemed</Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2">{format(new Date(h.createdOn), III_DD_MMM_YYYY)}</Typography>
                </Grid>
                <span className={classes.customPadding} />
                <Grid item xs={4}>
                  <Typography variant="body2" className="money">{formatCurrency(h.amount, currencySymbol)}</Typography>
                </Grid>
              </div>
            </Grid>
          ))}
        </Grid>
      </Grid>
    </>
  );
};

const DiscountPromoView: React.FC<Props> = props => {
  const { selectedDiscount, currencySymbol, summary } = props;

  const appliesToClasses = [];

  summary.list.forEach(l => {
    l.items.forEach(i => {
      if (i.type === "course" && i.discount && i.discount.id === selectedDiscount.id) {
        appliesToClasses.push({
          contact: `${l.contact.firstName} ${l.contact.lastName}`,
          classs: i.class.name,
          discountExTax: i.discountExTax
        });
      }
    });
  });

  return (
    <Grid container>
      <Grid item sm={12} className="mb-2">
        <div className="heading mb-2">Promotion</div>
        <Typography variant="body1" className={clsx(selectedDiscount.discountType !== "Percent" && "money")}>
          {getDiscountLabel(selectedDiscount, currencySymbol)}
        </Typography>
      </Grid>

      {Boolean(appliesToClasses.length) && (
        <Grid container item sm={12}>
          <Grid item xs={12}>
            <div className="heading mb-2 mt-2">Applies to</div>
          </Grid>
          {appliesToClasses.map(c => (
            <Grid container item sm={12} lg={7}>
              <Grid item xs={4}>
                {c.contact}
              </Grid>
              <Grid item xs={6}>
                {c.classs}
              </Grid>
              <Grid item xs={2} className="money text-end">
                {formatCurrency(c.discountExTax, currencySymbol)}
              </Grid>
            </Grid>
          ))}

          <Grid container item sm={12} lg={7} className="mt-2">
            <Grid item xs={10} />
            <Grid item xs={2} className="money text-end summaryTopBorder pt-1">
              {formatCurrency(appliesToClasses.reduce((p, c) => decimalPlus(p, c.discountExTax), 0), currencySymbol)}
            </Grid>
          </Grid>
        </Grid>
      )}
    </Grid>
  );
};

const CheckoutDiscountEditView = React.memo<any>(props => {
  const { selectedDiscount, type } = props;

  return (
    <div className="appFrame flex-fill root">
      <CustomAppBar>
        <CheckoutAppBar title={`${selectedDiscount.name} (${selectedDiscount.code})`} />
      </CustomAppBar>
      <div className="appBarContainer w-100 p-3">
        {type === "discount" && <DiscountPromoView {...props} />}
        {type === "voucher" && <VoucherView {...props} />}
      </div>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  summary: state.checkout.summary
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  dispatch
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(CheckoutDiscountEditView));
