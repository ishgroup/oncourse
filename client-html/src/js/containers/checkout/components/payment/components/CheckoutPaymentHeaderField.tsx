/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import {CheckoutPaymentPlan, PaymentMethod} from "@api/model";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Tooltip from "@material-ui/core/Tooltip";
import Typography from "@material-ui/core/Typography";
import DoneAllIcon from "@material-ui/icons/DoneAll";
import clsx from "clsx";
import { addDays, compareAsc, isSameDay } from "date-fns";
import { format } from "date-fns-tz";
import debounce from "lodash.debounce";
import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { checkPermissions } from "../../../../../common/actions";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { D_MMM_YYYY, YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import { decimalMinus, decimalPlus } from "../../../../../common/utils/numbers/decimalCalculation";
import { formatCurrency } from "../../../../../common/utils/numbers/numbersNormalizing";
import { BooleanArgFunction, NumberArgFunction, StringArgFunction } from "../../../../../model/common/CommonFunctions";
import { State } from "../../../../../reducers/state";
import { CheckoutItem, CheckoutPayment, CheckoutSummary } from "../../../../../model/checkout";
import { getAccountTransactionLockedDate } from "../../../../preferences/actions";
import {
  checkoutGetSavedCard,
  checkoutSetPaymentMethod,
  checkoutSetPaymentPlans,
  clearCcIframeUrl
} from "../../../actions/checkoutPayment";
import {
  checkoutUncheckAllPreviousInvoice,
  checkoutGetVoucherPromo,
  checkoutRemoveVoucher,
  checkoutUpdatePromo,
  checkoutUpdateSummaryField
} from "../../../actions/checkoutSummary";
import { CheckoutPage } from "../../CheckoutSelection";
import HeaderField, { HeaderFieldTypo } from "../../HeaderField";
import SelectedPromoCodesRenderer from "../../summary/promocode/SelectedPromoCodesRenderer";
import CheckoutPaymentPlans from "./payment-plans/CheckoutPaymentPlans";

const styles = () => createStyles({
  success: {
    color: "green",
    border: "2px solid",
    padding: "10px 16px",
    borderRadius: "50px"
  },
  icon: {
    height: "20px",
    width: "20px",
    marginLeft: "5px"
  }
});

interface PaymentHeaderFieldProps {
  activeField: string;
  setActiveField: any;
  classes?: any;
  availablePaymentTypes?: PaymentMethod[];
  selectedPaymentType?: string;
  checkoutSummary?: CheckoutSummary;
  checkoutItems?: CheckoutItem[];
  setPaymentMethod?: (selectedType: string) => void;
  setPaymentPlans?: (plans: CheckoutPaymentPlan[]) => void;
  currencySymbol?: any;
  clearCcIframeUrl?: () => void;
  form?: string;
  dispatch?: any;
  defaultTerms?: string;
  paymentProcessStatus?: any;
  paymentMethod?: string;
  formInvalid?: boolean;
  getVoucher?: StringArgFunction;
  removeVoucher?: NumberArgFunction;
  setDisablePayment?: BooleanArgFunction;
  selectedDiscount?: CheckoutItem;
  setSelectedDiscount?: (item: CheckoutItem) => void;
  lockedDate?: any;
  getLockedDate?: any;
  getPaymentDateChangePermissions?: any;
  canChangePaymentDate?: boolean;
  savedCreditCard?: CheckoutPayment["savedCreditCard"];
  uncheckAllPreviousInvoice?: (type: string, value?: boolean) => void;
  checkoutGetSavedCard?: (payerId: number, paymentMethodId: number) => void;
}

const noPaymentItems = [{ value: "No payment", label: "No payment" }];

const CheckoutPaymentHeaderFieldForm: React.FC<PaymentHeaderFieldProps> = props => {
  const {
    activeField,
    setActiveField,
    form,
    dispatch,
    classes,
    availablePaymentTypes,
    selectedPaymentType,
    checkoutSummary,
    checkoutItems,
    setPaymentMethod,
    currencySymbol,
    clearCcIframeUrl,
    defaultTerms,
    paymentProcessStatus,
    paymentMethod,
    formInvalid,
    lockedDate,
    getLockedDate,
    getPaymentDateChangePermissions,
    canChangePaymentDate,
    uncheckAllPreviousInvoice,
    getVoucher,
    selectedDiscount,
    setSelectedDiscount,
    removeVoucher,
    checkoutGetSavedCard,
    savedCreditCard,
    setDisablePayment,
    setPaymentPlans
  } = props;

  const payerContact = useMemo(() => checkoutSummary.list.find(l => l.payer).contact, [checkoutSummary.list]);

  useEffect(() => {
    getPaymentDateChangePermissions();
  }, []);

  useEffect(() => {
    if (canChangePaymentDate) {
      getLockedDate();
    }
  }, [canChangePaymentDate]);

  useEffect(() => {
    const cardPayment = availablePaymentTypes.find(t => t.type === "Credit card");

    if (payerContact && cardPayment) {
      checkoutGetSavedCard(payerContact.id, cardPayment.id);
    }
  }, [availablePaymentTypes, payerContact]);

  const [isZeroPayment, setIsZeroPayment] = useState(checkoutSummary.finalTotal === 0);

  const paymentTypes = useMemo(() => availablePaymentTypes
    .map(p => ({ value: p.name, label: p.name }))
    .concat(savedCreditCard ? [{ value: "Saved credit card", label: "Saved credit card" }] : []), [availablePaymentTypes, savedCreditCard]);

  const selectedPaymentMethod = useMemo(() => availablePaymentTypes.find(t => t.name === selectedPaymentType),
    [availablePaymentTypes, selectedPaymentType]);

  const paymentPlans = useMemo(() => {
    const plansTotal: { date: Date, amount: number }[] = [];

    const mergeDuplicate = p => {
      const duplicateIndex = plansTotal.findIndex(pt => (!pt.date && !p.date) || isSameDay(pt.date, p.date));
      if (duplicateIndex !== -1) {
        plansTotal[duplicateIndex].amount = decimalPlus(plansTotal[duplicateIndex].amount, p.amount);
      } else {
        plansTotal.push({ ...p });
      }
    };

    checkoutSummary.list.forEach(li => {
      li.items.forEach(item => {
        if (item.checked && item.type === "course" && !item.voucherId && item.paymentPlans && item.paymentPlans.length) {
          item.paymentPlans.forEach(mergeDuplicate);
        }
      });
    });

    checkoutSummary.previousOwing.invoices.forEach(inv => {
      if (inv.checked && inv.paymentPlans.length) {
        inv.paymentPlans.forEach(mergeDuplicate);
      }
    });

    plansTotal.sort((a, b) => (a.date > b.date ? 1 : -1));
    setPaymentPlans(plansTotal.filter(p => p.date).map(({ amount, date }) => ({ amount, date: format(date, YYYY_MM_DD_MINUSED) })));

    return plansTotal;
  }, [checkoutSummary]);

  const paymentPlansTotal = useMemo(() => {
    let total = 0;

    if (paymentPlans.length) {
      total = paymentPlans.reduce((p, c) => (c.date ? decimalPlus(p, c.amount) : p), 0);
    }
    return total;
  }, [paymentPlans]);

  const vouchersTotal = useMemo(() =>
    checkoutSummary.vouchers.reduce((p, c) => decimalPlus(p, typeof c.maxCoursesRedemption === "number" ? 0 : c.value), 0) || 0, [checkoutSummary.vouchers]);

  const classVouchersTotal = useMemo(() => {
    let total = 0;
    const classVouchers = checkoutSummary.vouchers.filter(v => typeof v.maxCoursesRedemption === "number");

    if (classVouchers.length) {
      total = classVouchers.reduce((p, c) => decimalPlus(p, c.appliedValue), 0);
    }

    return total;
  }, [checkoutSummary.vouchers, checkoutSummary.finalTotal]);

  const hendelMethodChange = v => {
    setPaymentMethod(v);
    setActiveField(CheckoutPage.default);
    if (availablePaymentTypes.find(t => t.name === v && t.type === "Credit card")) {
      clearCcIframeUrl();
      dispatch(checkoutUpdateSummaryField("paymentDate", null));
    }
  };

  const clearSelectedDiscount = () => {
    if (selectedDiscount) {
      setSelectedDiscount(null);
    }
  };

  const onClickPreviousCredit = () => {
    if (checkoutSummary.previousCredit.invoices.length > 0) {
      setActiveField(CheckoutPage.previousCredit);
      clearSelectedDiscount();
    }
  };

  const onClickPreviousOwing = () => {
    if (checkoutSummary.previousOwing.invoices.length > 0) {
      setActiveField(CheckoutPage.previousOwing);
      clearSelectedDiscount();
    }
  };

  const onPayNowChange = useCallback<any>( debounce(value => {
    dispatch(checkoutUpdateSummaryField("payNowTotal", value));
    if (value === 0) {
      if (!isZeroPayment) {
        setIsZeroPayment(true);
      }
      if (paymentMethod !== "No payment") {
        setPaymentMethod("No payment");
        dispatch(change(form, "payment_method", "No payment"));
      }
    } else {
      if (isZeroPayment) {
        setIsZeroPayment(false);
      }
      if (paymentMethod === "No payment") {
        setPaymentMethod(null);
        dispatch(change(form, "payment_method", null));
      }
    }
  }, 200), [isZeroPayment, paymentMethod]);

  const onPayNowFocus = () => {
    setDisablePayment(true);
    setActiveField(CheckoutPage.default);
    clearSelectedDiscount();
  };

  const onPayNowBlur = () => {
    setTimeout(() => {
      setDisablePayment(false);
    }, 1500);
  };

  const onDueDateChange = useCallback(value => {
    dispatch(checkoutUpdateSummaryField("invoiceDueDate", value));
  }, []);

  const onPayDateChange = useCallback(value => {
    dispatch(checkoutUpdateSummaryField("paymentDate", value));
  }, []);

  const validatePayNow = useCallback(value => {
    let maxAmount = decimalPlus(
      checkoutSummary.finalTotal,
      checkoutSummary.previousOwing.invoiceTotal,
      checkoutSummary.previousCredit.invoiceTotal
    );

    if (vouchersTotal) {
      maxAmount = decimalMinus(maxAmount, vouchersTotal);
    }

    if (classVouchersTotal) {
      maxAmount = decimalMinus(maxAmount, classVouchersTotal);
    }

    if (maxAmount < 0) {
      maxAmount = 0;
    }

    return value > maxAmount ? "Payment amount is more than the amount owing" : undefined;
  }, [checkoutSummary, vouchersTotal, classVouchersTotal, checkoutItems]);

  const validateLockedDate = useCallback(
    dateChanged => {
      if (!dateChanged || !lockedDate) {
        return undefined;
      }

      const date = new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth);

      return compareAsc(addDays(date, 1), new Date(dateChanged)) > 0
        ? `Date must be after ${format(date, D_MMM_YYYY)}`
        : undefined;
    },
    [lockedDate]
  );

  const onAllowAutoPayChange = useCallback((e, value) => {
    dispatch(checkoutUpdateSummaryField("allowAutoPay", value));
  }, []);

  const onGetPromoVoucher = useCallback<any>(debounce((name, value) => {
    if (value && value.length > 7) {
      getVoucher(value);
    }
  }, 800), []);

  const updateVouchersAppliedValue = totalExVouchers => {
    const priceVouchers = checkoutSummary.vouchers.filter(v => v.maxCoursesRedemption === null);

    priceVouchers.forEach(pv => {
      if (totalExVouchers > pv.value) {
        pv.appliedValue = pv.value;
        pv.availableValue = 0;
      } else {
        const updatedApplied = totalExVouchers > 0 ? totalExVouchers : 0;
        pv.appliedValue = updatedApplied;
        pv.availableValue = decimalMinus(pv.value, updatedApplied);
      }
      totalExVouchers = decimalMinus(totalExVouchers, pv.value);
      dispatch(checkoutUpdatePromo({ vouchersItem: pv }));
    });
  };

  useEffect(() => {
      let updated = decimalPlus(
        decimalMinus(checkoutSummary.finalTotal, classVouchersTotal),
        checkoutSummary.previousOwing.invoiceTotal,
        checkoutSummary.previousCredit.invoiceTotal
      );

      if (vouchersTotal) {
        updated = decimalMinus(updated, vouchersTotal);
      }

      if (paymentPlans.length) {
        updated -= paymentPlansTotal;
      }

      const payNow = updated > 0 ? updated : 0;

      updateVouchersAppliedValue(decimalPlus(updated + vouchersTotal));

      dispatch(checkoutUpdateSummaryField("payNowTotal", payNow));
      onPayNowChange(payNow);
    },
    [
      vouchersTotal,
      classVouchersTotal,
      checkoutSummary.finalTotal,
      checkoutSummary.previousOwing.invoices,
      checkoutSummary.previousCredit.invoices]);

  useEffect(() => {
    const planItems = [];
    let updatedPaymentPlans = [];

    const payNowExCredit = decimalMinus(
      decimalPlus(checkoutSummary.payNowTotal, classVouchersTotal, vouchersTotal) || 0,
      checkoutSummary.previousCredit.invoiceTotal
    );

    const totalIncOwing = decimalPlus(
      decimalMinus(checkoutSummary.finalTotal, paymentPlansTotal),
      checkoutSummary.previousOwing.invoiceTotal
    );

    if (payNowExCredit < totalIncOwing) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      const invoiceTerms = payerContact.invoiceTerms || defaultTerms;

      let amount = decimalMinus(totalIncOwing, payNowExCredit);

      if (amount > checkoutSummary.finalTotal) {
        amount = checkoutSummary.finalTotal;
      }

      if (amount > 0) {
        const paymentPlan = paymentPlans.length ? {
          date: today,
          amount
        } : {
          date: addDays(today, Number(invoiceTerms)),
          dateEditable: true,
          amount
        };

        planItems.push(paymentPlan);

        dispatch(checkoutUpdateSummaryField("invoiceDueDate", format(paymentPlan.date, YYYY_MM_DD_MINUSED)));
      }
    }

    if (paymentPlans.length) {
      updatedPaymentPlans.push(...paymentPlans.filter(p => p.date));
    }

    if (paymentPlans.length && payNowExCredit > totalIncOwing) {
      let diff = decimalMinus(payNowExCredit, totalIncOwing);

      updatedPaymentPlans = updatedPaymentPlans.map(p => {
        const updated = { ...p };

        if (updated.amount < diff) {
          diff = decimalMinus(diff, updated.amount);
          updated.amount = 0;
        } else if (diff > 0) {
          updated.amount = decimalMinus(updated.amount, diff);
          diff = 0;
        }

        return updated;
      });
    }

    let payLaterTotal = 0;
    let payLaterAdjustment = 0;

    checkoutSummary.previousOwing.invoices.forEach(i => {
      if (i.checked) {
        payLaterTotal = decimalPlus(payLaterTotal, i.paymentPlans.length ? 0 : parseFloat(i.amountOwing));
      } else {
        payLaterAdjustment = decimalPlus(payLaterAdjustment, parseFloat(i.amountOwing));
      }
    });

    payLaterTotal = decimalPlus(payLaterTotal > 0 ? decimalMinus(payLaterTotal, payNowExCredit) : 0, payLaterAdjustment);

    dispatch(change(form, "paymentPlans", [
      {
        amount: checkoutSummary.payNowTotal,
      },
      ...planItems,
      ...updatedPaymentPlans,
      {
        amount: payLaterTotal > 0 ? payLaterTotal : 0,
      }
    ]));
  }, [
    checkoutItems,
    paymentPlansTotal,
    vouchersTotal,
    classVouchersTotal,
    payerContact,
    checkoutSummary.payNowTotal,
    checkoutSummary.previousOwing.invoices,
    checkoutSummary.previousCredit.invoices
  ]);

  const checkedPreviousCredits = checkoutSummary[CheckoutPage.previousCredit].invoices.find(invoice => invoice.checked);
  const checkedPreviousOwings = checkoutSummary[CheckoutPage.previousOwing].invoices.find(invoice => invoice.checked);

  return (
    <>
      <div className="pl-2 pr-2">
        <HeaderFieldTypo
          title="This invoice"
          field="thisInvoice"
          amount={checkoutSummary.finalTotal}
          currencySymbol={currencySymbol}
          disabled={paymentProcessStatus === "success"}
          className="mb-1"
        />
        {Boolean(checkoutSummary.previousCredit.invoices.length) && (
          <HeaderFieldTypo
            title="Apply previous credit"
            activeField={activeField}
            field={CheckoutPage.previousCredit}
            onClick={onClickPreviousCredit}
            amount={
                checkoutSummary.previousCredit.invoiceTotal
                  ? checkoutSummary.previousCredit.invoiceTotal < 0
                  ? -checkoutSummary.previousCredit.invoiceTotal
                  : checkoutSummary.previousCredit.invoiceTotal
                  : 0
              }
            currencySymbol={currencySymbol}
            disabled={paymentProcessStatus === "success"}
            checkbox
            onCheckboxClick={e => uncheckAllPreviousInvoice(
              CheckoutPage.previousCredit,
              checkedPreviousCredits === undefined ? false : !e.target.checked
            )}
            checkboxChecked={!!checkedPreviousCredits}
          />
        )}
        {Boolean(checkoutSummary.previousOwing.invoices.length) && (
          <HeaderFieldTypo
            title="Pay previous owing"
            activeField={activeField}
            field={CheckoutPage.previousOwing}
            onClick={onClickPreviousOwing}
            amount={checkoutSummary.previousOwing.invoiceTotal}
            currencySymbol={currencySymbol}
            className="mt-1"
            disabled={paymentProcessStatus === "success"}
            checkbox
            onCheckboxClick={e => uncheckAllPreviousInvoice(
              CheckoutPage.previousOwing,
              checkedPreviousOwings === undefined ? false : !e.target.checked
            )}
            checkboxChecked={!!checkedPreviousOwings}
          />
        )}
        {Boolean(checkoutSummary.vouchers.length)
          && (
          <SelectedPromoCodesRenderer
            className="mt-1"
            type="voucher"
            selectedDiscount={selectedDiscount}
            openRow={item => {
              setSelectedDiscount(item);
              setActiveField("vouchers");
            }}
            onDelete={(e, index) => removeVoucher(index)}
            items={checkoutSummary.vouchers}
            disabled={paymentProcessStatus === "success"}
          />
        )}
      </div>

      <HeaderField
        name="vouchers"
        placeholder="Enter voucher code"
        onFocus={clearSelectedDiscount}
        form={form}
        dispatch={dispatch}
        onSearch={onGetPromoVoucher}
        disabled={paymentProcessStatus === "success"}
        showArrowButton
      />

      <div className="pl-2 pr-2">
        <CheckoutPaymentPlans
          name="paymentPlans"
          form={form}
          dispatch={dispatch}
          selected={paymentProcessStatus !== "success" && activeField === CheckoutPage.default}
          selectedPaymentType={availablePaymentTypes.find(p => p.name === paymentMethod)}
          onPayNowChange={onPayNowChange}
          onPayNowFocus={onPayNowFocus}
          onPayNowBlur={onPayNowBlur}
          onDueDateChange={onDueDateChange}
          onPayDateChange={onPayDateChange}
          validatePayNow={validatePayNow}
          validateLockedDate={validateLockedDate}
          disabledStep={paymentProcessStatus === "success"}
          canChangePaymentDate={canChangePaymentDate}
        />
        <div className="centeredFlex">
          <div className="secondaryHeading flex-fill">Payment method</div>
        </div>

        <FormField
          type="select"
          name="payment_method"
          placeholder="Payment method"
          items={isZeroPayment ? noPaymentItems : paymentTypes}
          onChange={hendelMethodChange}
          fullWidth
          disabledTab
          disabled={paymentProcessStatus === "success" || isZeroPayment || formInvalid}
        />
        {selectedPaymentMethod && selectedPaymentMethod.type === "Credit card" && (
          <Tooltip title="Retain a secure link to the bank which allows this card to be used for future billing or payment plans">
            <FormControlLabel
              classes={{
                root: "mt-1 mb-3"
              }}
              control={(
                <StyledCheckbox
                  checked={checkoutSummary.allowAutoPay}
                  onChange={onAllowAutoPayChange}
                />
              )}
              label="Store Card"
              disabled={paymentProcessStatus === "success"}
            />
          </Tooltip>
        )}
        {selectedPaymentType && paymentProcessStatus === "success" && (
          <div className="relative selectedItemArrow mb-2">
            <div className={clsx("centeredFlex", classes.success)}>
              <Typography variant="body2" className="flex-fill fontWeight600" component="span">
                Paid
              </Typography>
              <Typography variant="body2" component="span" className="fontWeight600 money">
                {formatCurrency(checkoutSummary.payNowTotal, currencySymbol)}
              </Typography>
              <DoneAllIcon className={classes.icon} color="inherit" />
            </div>
          </div>
        )}
      </div>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  availablePaymentTypes: state.checkout.payment.availablePaymentTypes,
  selectedPaymentType: state.checkout.payment.selectedPaymentType,
  checkoutSummary: state.checkout.summary,
  checkoutItems: state.checkout.items,
  savedCreditCard: state.checkout.payment.savedCreditCard,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  defaultTerms: state.invoices.defaultTerms,
  paymentProcessStatus: state.checkout.payment.process.status,
  paymentMethod: state.checkout.payment.selectedPaymentType,
  lockedDate: state.lockedDate,
  canChangePaymentDate: state.access["/a/v1/preference/lockedDate"] && state.access["/a/v1/preference/lockedDate"]["GET"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setPaymentMethod: (selectedType: string) => dispatch(checkoutSetPaymentMethod(selectedType)),
  setPaymentPlans: plans => dispatch(checkoutSetPaymentPlans(plans)),
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  getVoucher: code => dispatch(checkoutGetVoucherPromo(code)),
  removeVoucher: index => dispatch(checkoutRemoveVoucher(index)),
  getLockedDate: () => dispatch(getAccountTransactionLockedDate()),
  getPaymentDateChangePermissions: () => dispatch(
    checkPermissions({
      path: "/a/v1/preference/lockedDate",
      method: "GET"
    })
  ),
  uncheckAllPreviousInvoice: (type: string, value: boolean) => dispatch(checkoutUncheckAllPreviousInvoice(type, value)),
  checkoutGetSavedCard: (payerId: number, paymentMethodId: number) => dispatch(checkoutGetSavedCard(payerId, paymentMethodId))
});

const CheckoutPaymentHeaderField = connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CheckoutPaymentHeaderFieldForm);

export default withStyles(styles)(CheckoutPaymentHeaderField);
