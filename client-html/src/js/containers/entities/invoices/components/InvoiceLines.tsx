/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Course } from "@api/model";
import React, {
  useCallback, useEffect, useMemo, useRef, useState
} from "react";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import { change } from "redux-form";
import { Dispatch } from "redux";
import { Decimal } from "decimal.js-light";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import { formatCurrency, normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../reducers/state";
import {
  getInvoiceLineCourse,
  getInvoiceLineEnrolments,
  setInvoiceLineCourse,
  setInvoiceLineEnrolments
} from "../actions";
import { useAppSelector, usePrevious } from "../../../../common/utils/hooks";
import { accountLabelCondition } from "../../accounts/utils";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { getDiscountAmountExTax } from "../../discounts/utils";
import Uneditable from "../../../../common/components/form/Uneditable";

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

const USE_ALL_ACCOUNTS_FLAG = "allAccounts";

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
    accountTypes,
    courseClasses,
    selectedContact,
    type
  } = props;

  const [postDiscounts, setPostDiscounts] = useState(false);
  const [useAllAccounts, setUseAllAccounts] = useState(false);
  const [courseClassEnrolments, setCourseClassEnrolments] = useState([]);

  const accountRef = useRef<any>();

  const prevCourseClassId = usePrevious(row.courseClassId);
  
  const plainAccounts = useAppSelector(state => state.plainSearchRecords.Account.items);

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
    () => new Decimal(row.taxEach).mul(row.quantity || 1).toDecimalPlaces(2).toNumber(),
    [row.taxEach, row.quantity]
  );

  const incomeAccountOptions = useMemo(() => accountTypes.income?.concat({
    id: USE_ALL_ACCOUNTS_FLAG,
    description: "Other...",
    accountCode: ""
  }) || [], [accountTypes]);

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
    if (v === USE_ALL_ACCOUNTS_FLAG) {
      setTimeout(() => {
        dispatch(change(form, `${item}.incomeAccountId`, null));
        accountRef.current.focus();
      }, 300);
      setUseAllAccounts(true);
      return;
    }

    dispatch(change(form, `${item}.incomeAccountName`, useAllAccounts 
      ? accountLabelCondition(plainAccounts.find(a => a.id === v))
      : accountLabelCondition(incomeAccountOptions.find(a => a.id === v))));

    if (selectedContact && selectedContact["taxOverride.id"]) return;
    const selectedAccount = accountTypes.income.find(item => item.id === v);
    const selectedAccountTaxId = selectedAccount && selectedAccount["tax.id"];

    selectedAccountTaxId && dispatch(change(form, `${item}.taxId`, Number(selectedAccountTaxId)));
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
    <Grid container columnSpacing={3} rowSpacing={2}>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name={`${item}.title`}
          label="Title"
          disabled={type !== "Quote" && !isNew}
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
          disabled={type !== "Quote" && !isNew}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 6}>
        <FormField type="text" name={`${item}.unit`} label="Unit (e.g.,kg)" disabled={type !== "Quote" && !isNew} />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name={`${item}.incomeAccountId`}
          label="Income account"
          disabled={type !== "Quote" && !isNew}
          items={useAllAccounts ? accountTypes.all : incomeAccountOptions}
          defaultValue={row.incomeAccountName}
          selectValueMark="id"
          selectLabelCondition={accountLabelCondition}
          
          onChange={onIncomeAccountChange}
          inputRef={accountRef}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <FormField
          type="multilineText"
          name={`${item}.description`}
          label="Description"
          disabled={type !== "Quote" && !isNew}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={12}>
            <div className="heading">Assign To Budget</div>
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSelect"
              entity="Course"
              name={`${item}.courseName`}
              label="Course"
              selectValueMark="name"
              selectLabelMark="name"
              selectFilterCondition={courseFilterCondition}
              defaultValue={row.courseName}
              labelAdornment={
                <LinkAdornment link={row.courseId} linkHandler={courseLinkHandler} disabled={!row.courseId} />
              }
              onInnerValueChange={onCourseNameChange}
              disabled={type !== "Quote" && !isNew}
              itemRenderer={CourseItemRenderer}
              rowHeight={55}
              allowEmpty
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSelect"
              entity="Discount"
              aqlColumns="name,discountType,discountDollar,discountPercent,rounding"
              aqlFilter="((validTo >= today) or (validTo == null)) and ((validFrom <= today) or (validFrom == null))"
              label="Discount"
              selectValueMark="id"
              selectLabelMark="name"
              name={`${item}.discountId`}
              defaultValue={row.discountName}
              onInnerValueChange={onDiscountIdChange}
              disabled={type !== "Quote" && !isNew}
              allowEmpty
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              name={`${item}.courseClassId`}
              label="Class code"
              defaultValue={row.classCode}
              selectValueMark="id"
              selectLabelMark="code"
              disabled={(type !== "Quote" && !isNew) || !row.courseId}
              onChange={onClassCodeChange}
              items={courseClasses}
              allowEmpty
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              name={`${item}.enrolmentId`}
              label="Enrolled student"
              selectValueMark="id"
              defaultValue={row.enrolledStudent}
              disabled={(type !== "Quote" && !isNew) || !row.courseClassId}
              onChange={onEnrolmentIdChange}
              items={courseClassEnrolments}
              allowEmpty
            />
          </Grid>
        </Grid>
      </Grid>

      <Grid item container xs={twoColumn ? 6 : 12} columnSpacing={3} rowSpacing={2} className={twoColumn ? undefined : "pt-2"}>
        <Grid item xs={12}>
          <div className="heading">Amount</div>
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="money"
            name={`${item}.priceEachExTax`}
            label="Price each ExTax"
            onChange={onPriceEachExTaxChange}
            disabled={type !== "Quote" && !isNew}
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="money"
            name={`${item}.discountEachExTax`}
            label="Discount each ExTax"
            onChange={onDiscountEachExTaxChange}
            disabled={type !== "Quote" && !isNew}
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="select"
            name={`${item}.taxId`}
            label="Tax type"
            selectValueMark="id"
            selectLabelMark="code"
            onChange={onTaxIdChange}
            disabled={type !== "Quote" && !isNew}
            items={taxes || []}
            
            required
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <Uneditable
            label="Tax amount"
            value={taxDisplayedAmount}
            money
          />
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
              inline
              disabled={type !== "Quote" && !isNew}
              defaultValue={total}
              onBlur={onTotalBlur}
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