/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import { Grid, Typography } from "@material-ui/core";
import { change } from "redux-form";
import {
  Account,
  Course,
  Currency,
  ProductStatus,
  VoucherProduct,
  VoucherProductCourse
} from "@api/model";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../reducers/state";
import CustomSelector, { CustomSelectorOption } from "../../../../common/components/custom-selector/CustomSelector";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import {
 getMinMaxFee, clearMinMaxFee
} from "../actions";
import EditInPlaceMoneyField from "../../../../common/components/form/form-fields/EditInPlaceMoneyField";
import RelationsCommon from "../../common/components/RelationsCommon";
import { EditViewProps } from "../../../../model/common/ListView";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

interface VoucherProductGeneralProps extends EditViewProps<VoucherProduct> {
  accounts?: Account[];
  currency?: Currency;
  minFee?: number;
  maxFee?: number;
  searchCourses?: (search: string) => void;
  clearCourses?: (pending: boolean) => void;
  pendingCourses?: boolean;
  foundCourses?: Course[];
  submitSucceeded?: any;
  getMinMaxFee?: (ids: string) => void;
}

const parseFloatValue = value => (value ? parseFloat(value) : value);

const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);

const preformatCurrency = (currency: Currency) => value =>
  formatCurrency(value, currency ? currency.shortCurrencySymbol : "$");

const productStatusItems = Object.keys(ProductStatus).map(value => ({ value }));

const validateGreaterThenZero = value => (value > 0 ? undefined : "Must be greater then 0");

const getRedemptionOptions = (): CustomSelectorOption[] => [
    {
      // RedemptionType.Enrolment
      caption: "enrolment in",
      body: "classes from",
      type: "number",
      component: EditInPlaceField,
      fieldName: "maxCoursesRedemption",
      validate: [validateNonNegative, validateGreaterThenZero]
    },
    {
      // RedemptionType.Purchase
      caption: "purchase price",
      body: "",
      type: null
    },
    {
      // RedemptionType.Value
      caption: "value",
      body: "",
      type: "string",
      fieldName: "value",
      component: EditInPlaceMoneyField,
      validate: validateGreaterThenZero
    }
  ];

const onSelectRedemption = (props, setRedemptionIndex) => index => {
  const { form, dispatch } = props;
  setRedemptionIndex(index);
  if (index === RedemptionType.Enrollment) {
    dispatch(change(form, "value", null));
  } else if (index === RedemptionType.Purchase) {
    dispatch(clearMinMaxFee());
    dispatch(change(form, "courses", []));
    dispatch(change(form, "value", null));
    dispatch(change(form, "maxCoursesRedemption", null));
    dispatch(change(form, "feeExTax", null));
  } else if (index === RedemptionType.Value) {
    dispatch(clearMinMaxFee());
    dispatch(change(form, "courses", []));
    dispatch(change(form, "maxCoursesRedemption", null));
  }
};

enum RedemptionType {
  Enrollment = 0,
  Purchase,
  Value
}

const getInitialRedemptionIndex = (isNew: boolean, voucher: VoucherProduct) => {
  if (isNew || !voucher || voucher.maxCoursesRedemption != null) {
    return RedemptionType.Enrollment;
  }
  if (voucher.feeExTax != null) {
    return RedemptionType.Value;
  }
  return RedemptionType.Purchase;
};

const coursesToNestedListItems = (courses: VoucherProductCourse[]): NestedListItem[] => courses.map(course => ({
      id: course.id.toString(),
      entityId: course.id,
      primaryText: course.name,
      secondaryText: course.code,
      link: `/course/${course.id}`,
      active: true
    }));

const onAddCourses = props => (items: NestedListItem[]) => {
  const {
 values, dispatch, form, foundCourses
} = props;
  const courses = [].concat(values.courses, items.map(i => foundCourses.find(c => i.entityId === c.id)));
  dispatch(change(form, "courses", courses));
};

const onDeleteAllCourses = props => () => {
  const { dispatch, form } = props;
  dispatch(change(form, "courses", []));
};

const onDeleteCourse = props => (item: NestedListItem) => {
  const { dispatch, form, values } = props;
  const courses = values.courses.filter(c => c.id !== item.entityId);
  dispatch(change(form, "courses", courses));
};

const validateCourses = values => (values && values.length === 0 ? "At least one course must be added" : undefined);

const sortCourses = (a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1);

const VoucherProductGeneral: React.FC<VoucherProductGeneralProps> = props => {
  const {
    twoColumn,
    accounts,
    currency,
    values,
    isNew,
    minFee,
    maxFee,
    searchCourses,
    clearCourses,
    foundCourses,
    pendingCourses,
    submitSucceeded,
    getMinMaxFee,
    dispatch,
    form,
    rootEntity
  } = props;
  const [redemptionIndex, setRedemptionIndex] = useState(null);
  const initialRedemptionIndex = getInitialRedemptionIndex(isNew, values);
  if (redemptionIndex === null) {
    setRedemptionIndex(initialRedemptionIndex);
  }
  const formatCurrency = preformatCurrency(currency);
  const id = values && values.id;

  const coursesIds = values
    && values.courses
    && values.courses
      .map(c => c.id)
      .sort((a, b) => (a < b ? -1 : a > b ? 1 : 0))
      .join(",");

  useEffect(() => {
    if (values) {
      setRedemptionIndex(getInitialRedemptionIndex(isNew, values));
    }
  }, [id]);

  useEffect(() => {
    if (values) {
      if (values.courses && values.courses.length > 0) {
        getMinMaxFee(coursesIds);
      } else {
        dispatch(clearMinMaxFee());
      }
    }
  }, [coursesIds]);

  return (
    <div className="generalRoot">
      <div className="pt-1">
        <Grid container>
          <Grid item xs={twoColumn ? 2 : 6}>
            <FormField
              type="text"
              name="name"
              label="Name"
              required
            />
          </Grid>
          <Grid item xs={twoColumn ? 2 : 6}>
            <FormField
              type="text"
              name="code"
              label="SKU"
              required
            />
          </Grid>
        </Grid>
      </div>
      <div className="mr-2">
        <FormField
          type="select"
          name="liabilityAccountId"
          label="Liability account"
          validate={value => (accounts.find((item: Account) => item.id === value) ? undefined : `Mandatory field`)}
          items={accounts}
          selectValueMark="id"
          selectLabelCondition={a => `${a.accountCode}, ${a.description}`}
        />
      </div>
      <Typography color="inherit" component="div" noWrap>
        Expires
        <FormField
          type="number"
          name="expiryDays"
          color="primary"
          formatting="inline"
          hidePlaceholderInEditMode
          validate={[validateSingleMandatoryField, validateNonNegative]}
          parse={parseFloatValue}
        />
        days after purchase
      </Typography>
      <div className="pt-2">
        <div className="mb-2">
          <CustomSelector
            caption="Can be redeemed for"
            options={getRedemptionOptions()}
            onSelect={onSelectRedemption(props, setRedemptionIndex)}
            initialIndex={redemptionIndex === null ? initialRedemptionIndex : redemptionIndex}
            disabled={values && values.soldVouchersCount > 0}
          />
        </div>
      </div>
      {redemptionIndex === RedemptionType.Enrollment && (
        <div className="pb-2">
          <div className={twoColumn ? "mb-2 mw-800" : "mb-2"}>
            <NestedList
              formId={values.id}
              title="Courses"
              name="courses"
              searchPlaceholder="Find course"
              validate={validateCourses}
              values={coursesToNestedListItems(values.courses)}
              searchValues={coursesToNestedListItems(foundCourses)}
              onSearch={searchCourses}
              clearSearchResult={clearCourses}
              pending={pendingCourses}
              onAdd={onAddCourses(props)}
              onDelete={onDeleteCourse(props)}
              onDeleteAll={onDeleteAllCourses(props)}
              sort={sortCourses}
              resetSearch={submitSucceeded}
              searchType="withToggle"
              disabled={values && values.soldVouchersCount > 0}
              aqlEntities={["Course"]}
            />
          </div>
          <Typography color="inherit" component="div">
            Current class fee price range:
            {' '}
            <span className="money">{formatCurrency(minFee)}</span>
            {' '}
            to
            {" "}
            <span className="money">{formatCurrency(maxFee)}</span>
          </Typography>
        </div>
      )}
      {redemptionIndex !== RedemptionType.Purchase && (
        <FormField
          type="money"
          name="feeExTax"
          validate={[validateSingleMandatoryField, validateNonNegative]}
          label="Voucher price"
        />
      )}
      <FormField
        type="select"
        name="status"
        label="Status"
        items={productStatusItems}
        selectLabelMark="value"
      />
      <FormField type="multilineText" name="description" label="Web description" />
      <RelationsCommon
        values={values}
        dispatch={dispatch}
        form={form}
        submitSucceeded={submitSucceeded}
        rootEntity={rootEntity}
      />
    </div>
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchCourses: (search: string) => {
    dispatch(setCommonPlainSearch("Course", search));
    dispatch(getCommonPlainRecords("Course", 0, "code,name", null, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearCourses: (pending: boolean) => dispatch(clearCommonPlainRecords("Course", pending)),
  getMinMaxFee: (ids: string) => dispatch(getMinMaxFee(ids))
});

const mapStateToProps = (state: State) => ({
  accounts: state.accounts.liabilityItems,
  currency: state.currency,
  minFee: state.voucherProducts.minFee,
  maxFee: state.voucherProducts.maxFee,
  foundCourses: state.plainSearchRecords["Course"].items,
  pendingCourses: state.plainSearchRecords["Course"].loading
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(VoucherProductGeneral);
