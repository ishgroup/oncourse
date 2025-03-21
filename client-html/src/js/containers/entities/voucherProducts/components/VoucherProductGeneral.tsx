/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, Course, Currency, ProductStatus, VoucherProduct, VoucherProductCourse } from '@api/model';
import { Grid, Typography } from '@mui/material';
import $t from '@t';
import { ConfirmProps, formatCurrency } from 'ish-ui';
import React, { useEffect, useMemo, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, FieldArray } from 'redux-form';
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from '../../../../common/actions/CommonPlainRecordsActions';
import CustomSelector, { CustomSelectorOption } from '../../../../common/components/custom-selector/CustomSelector';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import { FormEditorField } from '../../../../common/components/form/formFields/FormEditor';
import FormField from '../../../../common/components/form/formFields/FormField';
import NestedList, { NestedListItem } from '../../../../common/components/form/nestedList/NestedList';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { useAppSelector } from '../../../../common/utils/hooks';
import { normalizeString } from '../../../../common/utils/strings';
import { validateSingleMandatoryField } from '../../../../common/utils/validation';
import { PLAIN_LIST_MAX_PAGE_SIZE } from '../../../../constants/Config';
import { EditViewProps } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import { PreferencesState } from '../../../preferences/reducers/state';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import { useTagGroups } from '../../../tags/utils/useTagGroups';
import RelationsCommon from '../../common/components/RelationsCommon';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { clearMinMaxFee, getMinMaxFee } from '../actions';

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
  coursesError?: boolean;
  dataCollectionRules?: PreferencesState["dataCollectionRules"];
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
      formFileldProps: {
        type: "number",
        name: "maxCoursesRedemption",
        validate: [validateNonNegative, validateGreaterThenZero]
      }
    },
    {
      // RedemptionType.Purchase
      caption: "purchase price",
      body: "",
    },
    {
      // RedemptionType.Value
      caption: "value",
      body: "",
      formFileldProps: {
        type: "money",
        name: "value",
        validate: validateGreaterThenZero
      }
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

const accountLabelCondition = a => `${a.accountCode}, ${a.description}`;

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
    coursesError,
    dispatch,
    form,
    rootEntity,
    dataCollectionRules,
    syncErrors,
    showConfirm
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

  const liabilityAccounts = useMemo(() => accounts.filter(a => a.type === "liability"), [accounts]);

  const expenseAccounts = useMemo(() => accounts.filter(a => a.type === "expense"), [accounts]);

  const tags = useAppSelector(state => state.tags.entityTags["VoucherProduct"]);

  const { tagsGrouped, subjectsField } = useTagGroups({ tags, tagsValue: values.tags, dispatch, form });

  const courseHandlers = useMemo(() => {
    if (values.soldVouchersCount === 0) {
      return {
        onAdd: onAddCourses(props),
        onDelete: onDeleteCourse(props),
        onDeleteAll: onDeleteAllCourses(props)
      };
    }
    
    const confirmProps: ConfirmProps = {
      title: "INFORMATION",
      confirmMessage: "Any changes you make to the courses that can be enrolled in with this voucher type will also affect vouchers of this type that have already been sold",
      confirmButtonText: "Edit"
    };
    
    return {
      onAdd: arg => showConfirm({ ...confirmProps, onConfirm: () => onAddCourses(props).call(null, arg) }),
      onDelete: arg => showConfirm({ ...confirmProps, onConfirm: () => onDeleteCourse(props).call(null, arg) }),
      onDeleteAll: () => showConfirm({ ...confirmProps, onConfirm: onDeleteAllCourses(props) })
    };
  }, [ values, dispatch, form, foundCourses]);

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 pt-3 pr-3">
      <Grid item container xs={12}>
        <FullScreenStickyHeader
          opened={isNew || Object.keys(syncErrors).some(k => ['code', 'name'].includes(k))}
          twoColumn={twoColumn}
          title={twoColumn ? (
            <div className="d-inline-flex-center">
              <span>
                {values && values.code}
              </span>
              <span className="ml-2">
                {values && values.name}
              </span>
            </div>
            ) : (
              <div>
                <div>
                  {values && values.code}
                </div>
                <div className="mt-2">
                  {values && values.name}
                </div>
              </div>
            )}
          fields={(
            <Grid container columnSpacing={3} rowSpacing={2}>
              <Grid item xs={twoColumn ? 2 : 12}>
                <FormField
                  type="text"
                  label={$t('sku')}
                  name="code"
                  required
                 />
              </Grid>
              <Grid item xs={twoColumn ? 4 : 12}>
                <FormField
                  type="text"
                  label={$t('name')}
                  name="name"
                  required
                />
              </Grid>
            </Grid>
            )}
        />
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <FormField
          type="tags"
          name="tags"
          tags={tagsGrouped.tags}
          className="mb-2"
        />

        {subjectsField}
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <EntityChecklists
          entity="VoucherProduct"
          form={form}
          entityId={values.id}
          checked={values.tags}
        />
      </Grid>
        
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="liabilityAccountId"
          label={$t('liability_account')}
          items={liabilityAccounts}
          selectValueMark="id"
          selectLabelCondition={accountLabelCondition}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="underpaymentAccountId"
          label={$t('default_voucher_underpayment_account')}
          items={expenseAccounts}
          selectValueMark="id"
          selectLabelCondition={accountLabelCondition}
          required
        />
      </Grid>

      <Grid item xs={12}>
        <Typography color="inherit" component="div" noWrap>
          {$t('expires')}
          {" "}
          <FormField
            type="number"
            name="expiryDays"
            validate={[validateSingleMandatoryField, validateNonNegative]}
            parse={parseFloatValue}
            debounced={false}
            inline
          />
          {" "}
          {$t('days_after_purchase')}
        </Typography>
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <CustomSelector
          caption="Can be redeemed for"
          options={getRedemptionOptions()}
          onSelect={onSelectRedemption(props, setRedemptionIndex)}
          initialIndex={redemptionIndex === null ? initialRedemptionIndex : redemptionIndex}
          disabled={values && values.soldVouchersCount > 0}
        />
      </Grid>

      {redemptionIndex === RedemptionType.Enrollment && (
      <Grid item xs={12}>
        <div className={twoColumn ? "mb-2 mw-800" : "mb-2"}>
          <NestedList
            formId={values.id}
            title={$t('courses')}
            name="courses"
            searchPlaceholder="Find course"
            validate={validateCourses}
            values={coursesToNestedListItems(values.courses)}
            searchValues={coursesToNestedListItems(foundCourses)}
            onSearch={searchCourses}
            clearSearchResult={clearCourses}
            pending={pendingCourses}
            sort={sortCourses}
            resetSearch={submitSucceeded}
            searchType="withToggle"
            aqlEntities={["Course"]}
            aqlQueryError={coursesError}
            {...courseHandlers}
          />
        </div>
        <Typography color="inherit" component="div">
          {$t('Current class fee price range')}:
          {' '}
          <span className="money">{formatCurrency(minFee)}</span>
          {' '}
          {$t('to')}
          {" "}
          <span className="money">{formatCurrency(maxFee)}</span>
        </Typography>
      </Grid>
        )}

      {redemptionIndex !== RedemptionType.Purchase && (
        <Grid item xs={twoColumn ? 6 : 12} style={redemptionIndex === RedemptionType.Value ? { marginTop: "10px" } : null}>
          <FormField
            type="money"
            name="feeExTax"
            validate={[validateSingleMandatoryField, validateNonNegative]}
            label={$t('voucher_price')}
          />
        </Grid>
      )}

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="status"
          label={$t('status')}
          items={productStatusItems}
          selectLabelMark="value"
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="dataCollectionRuleId"
          label={$t('data_collection_rule')}
          selectValueMark="id"
          selectLabelMark="name"
          items={dataCollectionRules || []}
          format={normalizeString}
          required
          sort
        />
      </Grid>

      <CustomFields
        entityName="VoucherProduct"
        fieldName="customFields"
        entityValues={values}
        form={form}
        gridItemProps={{
          xs: twoColumn ? 6 : 12
        }}
      />

      <Grid item xs={12}>
        <FormEditorField
          name="description"
          label={$t('web_description')}
        />
      </Grid>

      <Grid item xs={12}>
        <RelationsCommon
          values={values}
          dispatch={dispatch}
          form={form}
          submitSucceeded={submitSucceeded}
          rootEntity={rootEntity}
          customAqlEntities={["Course", "Product"]}
        />
      </Grid>

      <Grid item xs={12} className="pb-3 mb-3">
        <FieldArray
          name="documents"
          label={$t('documents')}
          entity="ArticleProduct"
          component={DocumentsRenderer}
          xsGrid={12}
          mdGrid={twoColumn ? 6 : 12}
          lgGrid={twoColumn ? 4 : 12}
          dispatch={dispatch}
          form={form}
          showConfirm={showConfirm}
          rerenderOnEveryChange
        />
      </Grid>
    </Grid>
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
  accounts: state.plainSearchRecords.Account.items,
  currency: state.location.currency,
  minFee: state.voucherProducts.minFee,
  maxFee: state.voucherProducts.maxFee,
  foundCourses: state.plainSearchRecords["Course"].items,
  coursesError: state.plainSearchRecords["Course"].error,
  pendingCourses: state.plainSearchRecords["Course"].loading,
  dataCollectionRules: state.preferences.dataCollectionRules
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(VoucherProductGeneral);
