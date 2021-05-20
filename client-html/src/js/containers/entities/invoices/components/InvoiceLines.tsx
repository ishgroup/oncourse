/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from "@api/model";
import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import Typography from "@material-ui/core/Typography/Typography";
import Grid from "@material-ui/core/Grid/Grid";
import { change } from "redux-form";
import { Dispatch } from "redux";
import { Decimal } from "decimal.js-light";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { formatCurrency, normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../reducers/state";
import {
  getInvoiceLineCourse,
  getInvoiceLineEnrolments,
  setInvoiceLineCourse,
  setInvoiceLineEnrolments
} from "../actions";
import { usePrevious } from "../../../../common/utils/hooks";
import { accountLabelCondition } from "../../accounts/utils";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { decimalMul, decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { getDiscountAmountExTax } from "../../discounts/utils";

const calculateInvoiceLineTotal = (
  priceEachExTax: number,
  discountEachExTax: number,
  taxEach: number,
  quantity: number
) => new Decimal(priceEachExTax || 0)
    .minus(discountEachExTax || 0)
    .plus(taxEach || 0)
    .mul(quantity || 1)
    .toDecimalPlaces(2)
    .toNumber();

const calculateInvoiceLineTaxEach = (priceEachExTax: number, discountEachExTax: number, taxRate: number) => new Decimal(priceEachExTax || 0)
    .minus(discountEachExTax || 0)
    .mul(taxRate)
    .toDecimalPlaces(2)
    .toNumber();

const calculateInvoiceLineTaxAndPrice = (
  total: number,
  taxRate: number,
  discountEachExTax: number,
  quantity: number
) => {
  const taxEach = new Decimal(total || 0).div(quantity || 1).minus(
    new Decimal(total || 0)
      .div(quantity || 1)
      .div(new Decimal(taxRate).plus(1))
      .toDecimalPlaces(2)
  );

  return [
    taxEach.toNumber(),
    new Decimal(total || 0)
      .div(quantity || 1)
      .plus(discountEachExTax || 0)
      .minus(taxEach)
      .toDecimalPlaces(2)
      .toNumber()
  ];
};

const HeaderContentBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row, currency } = props;

  const total = useMemo(
    () => formatCurrency(
        row.total || calculateInvoiceLineTotal(row.priceEachExTax, row.discountEachExTax, row.taxEach, row.quantity),
        currency.shortCurrencySymbol
      ),
    [
      row.id,
      row.priceEachExTax,
      row.discountEachExTax,
      row.taxEach,
      row.total,
      row.quantity,
      currency.shortCurrencySymbol
    ]
  );

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.title}
        </Typography>
      </div>

      <Typography variant="body2" color="textSecondary" className="centeredFlex pl-1 money">
        {total}
      </Typography>
    </div>
  );
});

export const HeaderContent = HeaderContentBase;

let ignoreChange: boolean = false;

const InvoiceLineBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const {
    row,
    rows,
    item,
    isNew,
    currency,
    twoColumn,
    dispatch,
    form,
    taxes,
    selectedLineCourse,
    getInvoiceLineCourse,
    clearInvoiceLineCourse,
    selectedLineEnrolments,
    getInvoiceLineEnrolments,
    clearInvoiceLineEnrolments,
    incomeAndCosAccounts,
    courseClasses,
    selectedContact
  } = props;

  const [postDiscounts, setPostDiscounts] = useState(false);

  const [courseClassEnrolments, setCourseClassEnrolments] = useState([]);

  const prevCourseClassId = usePrevious(row.courseClassId);

  useEffect(() => {
    if (!postDiscounts && row.cosAccountId) {
      setPostDiscounts(true);
    }
  }, [postDiscounts, row.id, row.cosAccountId]);

  useEffect(() => {
    if (selectedLineEnrolments) {
      setCourseClassEnrolments(selectedLineEnrolments);
    }
  }, [selectedLineEnrolments]);

  useEffect(() => {
    if (!row.courseId) {
      clearInvoiceLineCourse();
    }
    if (row.courseId && (!selectedLineCourse || selectedLineCourse.id !== row.courseId)) {
      getInvoiceLineCourse(row.courseId);
    }
  }, [row.courseId]);

  useEffect(() => {
    if (!row.courseClassId) {
      clearInvoiceLineEnrolments();
      setCourseClassEnrolments([]);
    }
    if (row.courseClassId && (!selectedLineEnrolments || row.courseClassId !== prevCourseClassId)) {
      getInvoiceLineEnrolments(row.courseClassId);
    }
  }, [row.courseClassId, prevCourseClassId]);

  useEffect(() => {
    if (isNew) {
      const averageTotal = rows.reduce((pr, cur) => decimalPlus(pr, cur.total), 0);

      if (!isNaN(averageTotal)) {
        dispatch(change(form, "total", averageTotal));
        dispatch(change(form, "paymentPlans[0].amount", averageTotal));
      }
    }
  }, [form, item, rows, isNew, row.total]);

  const taxRate = useMemo(() => {
    const tax = taxes.find(t => t.id === row.taxId);

    return tax ? tax.rate : 0;
  }, [taxes, row.taxId]);

  const total = useMemo(() => formatCurrency(
      calculateInvoiceLineTotal(row.priceEachExTax, row.discountEachExTax, row.taxEach, row.quantity),
      currency.shortCurrencySymbol
    ), [row.id, row.priceEachExTax, row.discountEachExTax, row.taxEach, row.quantity, currency.shortCurrencySymbol]);

  const taxDisplayedAmount = useMemo(
    () => formatCurrency(new Decimal(row.taxEach).mul(row.quantity || 1).toDecimalPlaces(2), currency.shortCurrencySymbol),
    [row.taxEach, row.quantity, currency.shortCurrencySymbol]
  );

  const courseLinkHandler = useCallback(() => {
    openCourseLink(row.courseId);
  }, [row.courseId]);

  const onCourseNameChange = useCallback(
    (value: Course) => {
      dispatch(change(form, `${item}.courseCode`, value ? value.code : null));
      dispatch(change(form, `${item}.courseId`, value ? value.id : null));
      dispatch(change(form, `${item}.courseClassId`, null));
      dispatch(change(form, `${item}.classCode`, null));
      dispatch(change(form, `${item}.enrolledStudent`, null));
      dispatch(change(form, `${item}.enrolmentId`, null));
    },
    [form, item]
  );

  const onClassCodeChange = useCallback(() => {
    dispatch(change(form, `${item}.enrolledStudent`, null));
  }, [form, item]);

  const onEnrolmentIdChange = useCallback(
    id => {
      const student = courseClassEnrolments.find(i => i.id === id);

      dispatch(change(form, `${item}.enrolledStudent`, student ? student.label : null));
    },
    [form, item, courseClassEnrolments]
  );

  const onQuantityChange = useCallback(
    value => {
      dispatch(
        change(
          form,
          `${item}.total`,
          calculateInvoiceLineTotal(row.priceEachExTax, row.discountEachExTax, row.taxEach, value.target.value)
        )
      );
    },
    [form, item, taxRate, row.priceEachExTax, row.discountEachExTax, row.priceEachExTax, row.taxEach]
  );

  const onPriceEachExTaxChange = useCallback(
    value => {
      if (ignoreChange) {
        ignoreChange = false;
        return;
      }

      const taxEach = calculateInvoiceLineTaxEach(value, row.discountEachExTax, taxRate);

      dispatch(change(form, `${item}.taxEach`, taxEach));
      dispatch(
        change(form, `${item}.total`, calculateInvoiceLineTotal(value, row.discountEachExTax, taxEach, row.quantity))
      );
    },
    [form, item, taxRate, row.discountEachExTax, row.quantity]
  );

  const onDiscountEachExTaxChange = useCallback(
    value => {
      dispatch(change(form, `${item}.taxEach`, calculateInvoiceLineTaxEach(row.priceEachExTax, value, taxRate)));
    },
    [form, item, taxRate, row.priceEachExTax, row.quantity, row.taxEach]
  );

  const onTaxIdChange = useCallback(
    value => {
      const taxEach = calculateInvoiceLineTaxEach(
        row.priceEachExTax,
        row.discountEachExTax,
        taxes.find(t => t.id === value).rate
      );
      dispatch(change(form, `${item}.taxEach`, taxEach));
      dispatch(
        change(
          form,
          `${item}.total`,
          calculateInvoiceLineTotal(row.priceEachExTax, row.discountEachExTax, taxEach, row.quantity)
        )
      );
    },
    [form, item, row.priceEachExTax, row.discountEachExTax, row.quantity]
  );

  const onTotalBlur = useCallback(() => {
    const priceAndTax = calculateInvoiceLineTaxAndPrice(row.total, taxRate, row.discountEachExTax, row.quantity);

    ignoreChange = true;

    dispatch(change(form, `${item}.taxEach`, priceAndTax[0]));

    dispatch(change(form, `${item}.priceEachExTax`, priceAndTax[1]));
  }, [form, item, taxRate, row.total, row.discountEachExTax, row.quantity]);

  const onIncomeAccountChange = v => {
    if (selectedContact && selectedContact["taxOverride.id"]) return;
    const selectedAccount = incomeAndCosAccounts[0].find(item => item.id === v);
    const selectedAccountTaxId = selectedAccount && selectedAccount.taxId;

    dispatch(change(form, `${item}.taxId`, Number(selectedAccountTaxId)));
  };

  const onDiscountIdChange = discount => {
    if (!discount) {
      dispatch(change(form, `${item}.discountName`, null));
      dispatch(change(
        form,
        `${item}.discountEachExTax`,
        null
      ));
      return;
    }

    const {
     name, discountType, discountDollar, discountPercent, rounding
    } = discount;

    dispatch(change(form, `${item}.discountName`, name));
    dispatch(change(
      form,
      `${item}.discountEachExTax`,
      getDiscountAmountExTax(
        {
          name,
          discountType,
          rounding,
          discountPercent,
          discountValue: parseFloat(discountDollar)
        },
        taxes.find(t => t.id === row.taxId),
        (row && row.total) || 1
      )
    ));
  };

  return (
    <Grid container>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name={`${item}.title`}
          label="Title"
          disabled={!isNew}
          required
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 6}>
        <FormField
          type="number"
          name={`${item}.quantity`}
          label="Quantity"
          onChange={onQuantityChange}
          normalize={normalizeNumber}
          disabled={!isNew}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 6}>
        <FormField type="text" name={`${item}.unit`} label="Unit (e.g.,kg)" disabled={!isNew} />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name={`${item}.incomeAccountId`}
          label="Income account"
          disabled={!isNew}
          items={incomeAndCosAccounts[0] || []}
          selectValueMark="id"
          selectLabelCondition={accountLabelCondition}
          autoWidth={false}
          fullWidth
          required
          onChange={onIncomeAccountChange}
        />
      </Grid>

      <Grid item xs={12} className="pt-2">
        <FormField
          type="multilineText"
          name={`${item}.description`}
          label="Description"
          disabled={!isNew}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <Grid container>
          <Grid item xs={12} className="pt-1 pb-1">
            <div className="heading">Assign To Budget</div>
          </Grid>

          <Grid item xs={12}>
            <FormField
              type="remoteDataSearchSelect"
              entity="Course"
              name={`${item}.courseName`}
              label="Course"
              selectValueMark="name"
              selectLabelMark="name"
              selectFilterCondition={courseFilterCondition}
              defaultDisplayValue={row.courseName}
              labelAdornment={
                <LinkAdornment link={row.courseId} linkHandler={courseLinkHandler} disabled={!row.courseId} />
              }
              onInnerValueChange={onCourseNameChange}
              disabled={!isNew}
              itemRenderer={CourseItemRenderer}
              rowHeight={55}
              allowEmpty
            />
          </Grid>

          <Grid item xs={6}>
            <FormField
              type="select"
              name={`${item}.courseClassId`}
              label="Class code"
              defaultValue={row.classCode}
              selectValueMark="id"
              selectLabelMark="code"
              disabled={!isNew || !row.courseId}
              onChange={onClassCodeChange}
              items={courseClasses}
              allowEmpty
            />
          </Grid>

          <Grid item xs={6}>
            <FormField
              type="select"
              name={`${item}.enrolmentId`}
              label="Enrolled student"
              selectValueMark="id"
              defaultValue={row.enrolledStudent}
              disabled={!isNew || !row.courseClassId}
              onChange={onEnrolmentIdChange}
              items={courseClassEnrolments}
              allowEmpty
            />
          </Grid>
        </Grid>
      </Grid>

      <Grid item container xs={twoColumn ? 6 : 12} className={twoColumn ? undefined : "pt-2"}>
        <Grid item xs={12}>
          <div className="heading pb-1">Amount</div>
        </Grid>

        <Grid item xs={12}>
          <FormField
            type="remoteDataSearchSelect"
            entity="Discount"
            aqlColumns="name,discountType,discountDollar,discountPercent,rounding"
            aqlFilter="((validTo >= today) or (validTo == null)) and ((validFrom <= today) or (validFrom == null))"
            label="Discount"
            selectValueMark="id"
            selectLabelMark="name"
            name={`${item}.discountId`}
            defaultDisplayValue={row.discountName}
            onInnerValueChange={onDiscountIdChange}
            disabled={!isNew}
            allowEmpty
          />
        </Grid>

        <Grid item xs={6}>
          <FormField
            type="money"
            name={`${item}.priceEachExTax`}
            label="Price each ExTax"
            onChange={onPriceEachExTaxChange}
            disabled={!isNew}
          />
        </Grid>

        <Grid item xs={6}>
          <FormField
            type="money"
            name={`${item}.discountEachExTax`}
            label="Discount each ExTax"
            onChange={onDiscountEachExTaxChange}
            disabled={!isNew}
          />
        </Grid>

        <Grid item xs={6}>
          <FormField
            type="select"
            name={`${item}.taxId`}
            label="Tax type"
            selectValueMark="id"
            selectLabelMark="code"
            onChange={onTaxIdChange}
            disabled={!isNew}
            items={taxes || []}
            autoWidth={false}
            required
          />
        </Grid>

        <Grid item xs={6} className="textField">
          <div>
            <Typography variant="caption" color="textSecondary">
              Tax amount
            </Typography>
            <Typography className="money">{taxDisplayedAmount}</Typography>
          </div>
        </Grid>
      </Grid>

      <Grid item xs={12}>
        <div className="centeredFlex justify-content-end pr-0-5 mb-1">
          <Typography variant="subtitle2" noWrap>
            Total
          </Typography>
          <Typography variant="body2" color="textSecondary" className="pl-1" component="div">
            <FormField
              type="money"
              name={`${item}.total`}
              formatting="inline"
              disabled={!isNew}
              defaultValue={total}
              onBlur={onTotalBlur}
              hidePlaceholderInEditMode
            />
          </Typography>
        </div>
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  courseClasses: state.invoices.selectedLineCourseClasses,
  selectedLineCourse: state.invoices.selectedLineCourse,
  selectedLineEnrolments: state.invoices.selectedLineEnrolments,
  selectedContact: state.invoices.selectedContact
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getInvoiceLineCourse: (id: number) => dispatch(getInvoiceLineCourse(id)),
  getInvoiceLineEnrolments: (courseClassId: number) => dispatch(getInvoiceLineEnrolments(courseClassId)),
  clearInvoiceLineCourse: () => dispatch(setInvoiceLineCourse(null, [])),
  clearInvoiceLineEnrolments: () => dispatch(setInvoiceLineEnrolments(null))
});

export const InvoiceLines = connect<any, any, any>(mapStateToProps, mapDispatchToProps)(InvoiceLineBase);
