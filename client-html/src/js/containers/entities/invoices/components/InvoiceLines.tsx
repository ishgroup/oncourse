/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Course, Currency, Discount, InvoiceType, Tax } from '@api/model';
import { Grid, Typography } from '@mui/material';
import $t from '@t';
import { Decimal } from 'decimal.js-light';
import { decimalMul, LinkAdornment, NoArgFunction, NumberArgFunction, usePrevious } from 'ish-ui';
import { normalizeNumberToPositive } from 'ish-ui/dist/utils/numbers/numbersNormalizing';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { IAction } from '../../../../common/actions/IshAction';
import InstantFetchErrorHandler from '../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import LoadingIndicator from '../../../../common/components/progress/LoadingIndicator';
import EntityService from '../../../../common/services/EntityService';
import { getCustomColumnsMap } from '../../../../common/utils/common';
import {
  bankRounding,
  getCurrentTax,
  getPriceAndDeductionsByTotal,
  getTotalAndDeductionsByPrice,
  useAppSelector
} from '../../../../common/utils/hooks';
import { AccountTypes } from '../../../../model/entities/Account';
import { InvoiceLineWithTotal } from '../../../../model/entities/Invoice';
import { State } from '../../../../reducers/state';
import { accountLabelCondition } from '../../accounts/utils';
import CourseItemRenderer from '../../courses/components/CourseItemRenderer';
import { courseFilterCondition, openCourseLink } from '../../courses/utils';
import { openDiscountsLink, plainDiscountToAPIModel } from '../../discounts/utils';
import {
  getInvoiceLineCourse,
  getInvoiceLineEnrolments,
  setInvoiceLineCourse,
  setInvoiceLineEnrolments
} from '../actions';
import { INVOICE_LINE_DISCOUNT_AQL, INVOICE_LINE_DISCOUNT_COLUMNS } from '../constants';
import { InvoicesState } from '../reducers/state';

interface HeaderProps {
  row: InvoiceLineWithTotal;
  shortCurrencySymbol: string;
}

export const HeaderContent = React.memo<HeaderProps>(({ row, shortCurrencySymbol }) => {
  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.title}
        </Typography>
      </div>

      <Typography variant="body2" color="textSecondary" className="centeredFlex pl-1 money">
        {shortCurrencySymbol} {row.total}
      </Typography>
    </div>
  );
});

const USE_ALL_ACCOUNTS_FLAG = "allAccounts";

interface InvoiceLineBaseProps {
  row: InvoiceLineWithTotal,
  rows: InvoiceLineWithTotal[];
  item: string;
  isNew: boolean;
  currency: Currency;
  twoColumn: boolean;
  dispatch: Dispatch<IAction>;
  form: string;
  taxes: Tax[];
  selectedLineCourse: InvoicesState['selectedLineCourse'];
  getInvoiceLineCourse: NumberArgFunction;
  clearInvoiceLineCourse: NoArgFunction;
  selectedLineEnrolments: InvoicesState['selectedLineEnrolments'];
  getInvoiceLineEnrolments: NumberArgFunction;
  clearInvoiceLineEnrolments: NoArgFunction;
  accountTypes: AccountTypes;
  courseClasses: InvoicesState['selectedLineCourseClasses'];
  selectedContact: InvoicesState['selectedContact'];
  type: InvoiceType;
}

const InvoiceLineBase = React.memo<InvoiceLineBaseProps>(({
  row,
  rows,
  item,
  isNew,
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
}) => {

  const [useAllAccounts, setUseAllAccounts] = useState(false);
  const [courseClassEnrolments, setCourseClassEnrolments] = useState([]);
  const [currentDiscount, setCurrentDiscount] = useState<Discount | number>(null);
  const [loading, setLoading] = useState(false);

  const accountRef = useRef<any>(undefined);

  const prevCourseClassId = usePrevious(row.courseClassId);
  
  const plainAccounts = useAppSelector(state => state.plainSearchRecords.Account.items);

  useEffect(() => {
    if (selectedLineEnrolments) {
      setCourseClassEnrolments(selectedLineEnrolments);
    }
  }, [selectedLineEnrolments]);

  useEffect(() => {
    if (row.discountId && typeof currentDiscount !== 'number' && currentDiscount?.id !== row.discountId) {
      setLoading(true);

      EntityService.getPlainRecords(
        'Discount',
        INVOICE_LINE_DISCOUNT_COLUMNS,
        `id is ${row.discountId} and ${INVOICE_LINE_DISCOUNT_AQL}`,
        1,
        0,
      )
      .then(({ rows }) => {
        setCurrentDiscount(plainDiscountToAPIModel(rows.map(getCustomColumnsMap(INVOICE_LINE_DISCOUNT_COLUMNS))[0]));
        setLoading(false);
      })
      .catch(e => {
        InstantFetchErrorHandler(dispatch, e);
        setLoading(false);
      });
    } else {
      setCurrentDiscount(null);
    }
  }, [row.discountId]);

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
      const averageTotal = rows.reduce((pr, cur) => new Decimal(pr).plus(cur.total || 0), new Decimal(0)).toNumber();

      if (!isNaN(averageTotal)) {
        dispatch(change(form, "total", bankRounding(averageTotal)));
        dispatch(change(form, "paymentPlans[0].amount", bankRounding(averageTotal)));
      }
    }
  }, [form, item, rows, isNew, row.total]);

  const taxRate = useMemo(() => {
    const tax = taxes.find(t => t.id === row.taxId);

    return tax ? tax.rate : 0;
  }, [taxes, row.taxId]);

  const taxDisplayedAmount = useMemo(() => bankRounding(new Decimal(row.taxEach).mul(row.quantity)), [row.taxEach, row.quantity]);

  const incomeAccountOptions = useMemo(() => accountTypes.income?.concat({
    id: USE_ALL_ACCOUNTS_FLAG as any,
    description: "Other...",
    accountCode: ""
  }) || [], [accountTypes]);

  const courseLinkHandler = () => {
    openCourseLink(row.courseId);
  };

  const onCourseNameChange =
    (value: Course) => {
      dispatch(change(form, `${item}.courseCode`, value ? value.code : null));
      dispatch(change(form, `${item}.courseId`, value ? value.id : null));
      dispatch(change(form, `${item}.courseClassId`, null));
      dispatch(change(form, `${item}.classCode`, null));
      dispatch(change(form, `${item}.enrolledStudent`, null));
      dispatch(change(form, `${item}.enrolmentId`, null));
    };

  const onClassCodeChange = () => {
    dispatch(change(form, `${item}.enrolledStudent`, null));
  };

  const onEnrolmentIdChange =
    id => {
      const student = courseClassEnrolments.find(i => i.id === id);

      dispatch(change(form, `${item}.enrolledStudent`, student ? student.label : null));
    };
  
  const recalculateByTotal = (total: number, taxRate: number, discount: Discount | number, quantity) => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(new Decimal(total).div(quantity).toNumber(), taxRate, discount);

    dispatch(change(form, `${item}.taxEach`, taxEach));
    dispatch(change(form, `${item}.discountEachExTax`, discountAmount));
    dispatch(change(form, `${item}.priceEachExTax`, priceEachExTax));
  };

  const recalculateByPrice = (priceEachExTax: number, taxRate: number, discount: Discount | number, quantity) => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice( priceEachExTax, taxRate, discount);

    dispatch(change(form, `${item}.taxEach`, taxEach));
    dispatch(change(form, `${item}.discountEachExTax`, discountEach));
    dispatch(change(form, `${item}.total`, decimalMul(total, quantity)));
  }; 
  
  const recalculate = (total: number, priceEachExTax: number, qantity: number, taxRate: number, discount: Discount | number)=> {
    if (total === 0 && priceEachExTax === 0) {
      dispatch(change(form, `${item}.taxEach`, 0));
      dispatch(change(form, `${item}.discountEachExTax`, 0));
      return;
    }

    total
      ? recalculateByTotal(total, taxRate, discount, qantity)
      : recalculateByPrice(priceEachExTax, taxRate, discount, qantity);
  }; 

  const onPriceEachExTaxBlur = (e, value) => {
    recalculateByPrice(value, taxRate, currentDiscount, row.quantity);
  };

  const onDiscountEachExTaxBlur = (e, value) => {
    setCurrentDiscount(value || 0);
    recalculateByPrice(row.priceEachExTax, taxRate, value || 0, row.quantity);
  };

  const onTaxIdChange = value => {
    recalculate(row.total, row.priceEachExTax, row.quantity, getCurrentTax(taxes, value)?.rate, currentDiscount);
  };

  const onQuantityBlur = (e, value) => {
    recalculateByPrice(row.priceEachExTax || 0, taxRate, currentDiscount, value);
  };

  const onTotalBlur = (e, total) => {
    recalculate(total, row.priceEachExTax, row.quantity, taxRate, currentDiscount);
  };

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
    const apiDiscount: Discount | number =  discount ? plainDiscountToAPIModel(discount) : null;
    setCurrentDiscount(apiDiscount);
    dispatch(change(form, `${item}.discountName`, typeof apiDiscount !== 'number' && apiDiscount?.name || null));

    if (
      (typeof apiDiscount !== 'number' && apiDiscount !== null && apiDiscount?.discountType === 'Fee override') ||
      (!discount && typeof currentDiscount !== 'number' && currentDiscount?.discountType === 'Fee override')
    ) {
      const total = bankRounding(new Decimal(apiDiscount?.discountValue).mul(new Decimal(taxRate).plus(1)).toNumber());
      recalculateByTotal(
        total,
        taxRate,
        apiDiscount,
        row.quantity
      );
      dispatch(change(form, `${item}.total`, decimalMul(total, row.quantity)));
      return;
    }

    recalculate(row.total, row.priceEachExTax, row.quantity, taxRate, apiDiscount);
  };

  const disableFinanceFileds = type !== "Quote" && !isNew;
  const hasFeeOverride = typeof currentDiscount !== 'number' && currentDiscount?.discountType === 'Fee override';

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="relative">
      <LoadingIndicator customLoading={loading}/>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name={`${item}.title`}
          label={$t('title')}
          disabled={disableFinanceFileds}
          required
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 6}>
        <FormField
          type="number"
          name={`${item}.quantity`}
          label={$t('quantity')}
          normalize={normalizeNumberToPositive}
          onBlur={onQuantityBlur}
          disabled={disableFinanceFileds}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 6}>
        <FormField type="text" name={`${item}.unit`} label={$t('unit_egkg')} disabled={disableFinanceFileds} />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name={`${item}.incomeAccountId`}
          label={$t('income_account')}
          disabled={disableFinanceFileds}
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
          label={$t('description')}
          disabled={disableFinanceFileds}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={12}>
            <div className="heading">{$t('assign_to_budget')}</div>
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSelect"
              entity="Course"
              name={`${item}.courseName`}
              label={$t('course')}
              selectValueMark="name"
              selectLabelMark="name"
              selectFilterCondition={courseFilterCondition}
              defaultValue={row.courseName}
              labelAdornment={
                <LinkAdornment link={row.courseId} linkHandler={courseLinkHandler} disabled={!row.courseId} />
              }
              onInnerValueChange={onCourseNameChange}
              disabled={disableFinanceFileds}
              itemRenderer={CourseItemRenderer}
              rowHeight={55}
              allowEmpty
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSelect"
              entity="Discount"
              aqlColumns={INVOICE_LINE_DISCOUNT_COLUMNS}
              aqlFilter={INVOICE_LINE_DISCOUNT_AQL}
              label={$t('discount')}
              selectValueMark="id"
              selectLabelMark="name"
              name={`${item}.discountId`}
              defaultValue={row.discountName}
              onInnerValueChange={onDiscountIdChange}
              disabled={disableFinanceFileds}
              labelAdornment={
                <LinkAdornment
                  link={row.discountId}
                  disabled={!row.discountId}
                  linkHandler={openDiscountsLink}
                />
              }
              allowEmpty
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              name={`${item}.courseClassId`}
              label={$t('class_code')}
              defaultValue={row.classCode}
              selectValueMark="id"
              selectLabelMark="code"
              disabled={disableFinanceFileds || !row.courseId}
              onChange={onClassCodeChange}
              items={courseClasses}
              allowEmpty
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              name={`${item}.enrolmentId`}
              label={$t('enrolled_student')}
              selectValueMark="id"
              defaultValue={row.enrolledStudent}
              disabled={disableFinanceFileds || !row.courseClassId}
              onChange={onEnrolmentIdChange}
              items={courseClassEnrolments}
              allowEmpty
            />
          </Grid>
        </Grid>
      </Grid>

      <Grid item container xs={twoColumn ? 6 : 12} columnSpacing={3} rowSpacing={2} className={twoColumn ? undefined : "pt-2"}>
        <Grid item xs={12}>
          <div className="heading">{$t('amount')}</div>
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="money"
            name={`${item}.priceEachExTax`}
            label={$t('price_each_extax')}
            onBlur={onPriceEachExTaxBlur}
            disabled={disableFinanceFileds}
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="money"
            name={`${item}.discountEachExTax`}
            label={$t('discount_each_extax')}
            onBlur={onDiscountEachExTaxBlur}
            disabled={disableFinanceFileds || hasFeeOverride}
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="select"
            name={`${item}.taxId`}
            label={$t('tax_type')}
            selectValueMark="id"
            selectLabelMark="code"
            onChange={onTaxIdChange}
            disabled={disableFinanceFileds}
            items={taxes || []}
            required={isNew}
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <Uneditable
            label={$t('tax_amount')}
            value={taxDisplayedAmount}
            money
          />
        </Grid>
      </Grid>

      <Grid item xs={12}>
        <div className="centeredFlex justify-content-end pr-0-5 mb-1">
          <Typography variant="subtitle2" noWrap>
            {$t('total')}
          </Typography>
          <Typography variant="body2" color="textSecondary" className="pl-1" component="div">
            <FormField
              type="money"
              name={`${item}.total`}
              disabled={disableFinanceFileds || hasFeeOverride}
              defaultValue={row.total}
              onBlur={onTotalBlur}
              inline
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

const mapDispatchToProps = (dispatch: Dispatch<any>) => ( {
  getInvoiceLineCourse: (id: number) => dispatch(getInvoiceLineCourse(id)),
  getInvoiceLineEnrolments: (courseClassId: number) => dispatch(getInvoiceLineEnrolments(courseClassId)),
  clearInvoiceLineCourse: () => dispatch(setInvoiceLineCourse(null, [])),
  clearInvoiceLineEnrolments: () => dispatch(setInvoiceLineEnrolments(null))
});

export const InvoiceLines = connect(mapStateToProps, mapDispatchToProps)(InvoiceLineBase);