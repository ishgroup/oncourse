/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Sale } from '@api/model';
import { FormControlLabel, Typography } from '@mui/material';
import $t from '@t';
import { normalizeNumber } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from '../../../../common/actions/CommonPlainRecordsActions';
import FormField from '../../../../common/components/form/formFields/FormField';
import NestedList, { NestedListItem } from '../../../../common/components/form/nestedList/NestedList';
import { validateNonNegative, validateSingleMandatoryField } from '../../../../common/utils/validation';
import { PLAIN_LIST_MAX_PAGE_SIZE } from '../../../../constants/Config';
import { State } from '../../../../reducers/state';
import { mapPlainDiscountClasses } from '../utils';

/**
 * @param sale
 */
const courseClassToNestedListItem = (sale: Sale): NestedListItem => ({
  id: sale.id.toString(),
  entityId: sale.id,
  primaryText: sale.name,
  secondaryText: sale.code,
  link: `/class?search=id is ${sale.id}`,
  active: sale.active
});

const styles = (() => ({
  dataRowClass: {
    gridTemplateColumns: "1fr 0.5fr"
  }
}));

class DiscountClasses extends React.PureComponent<any, any> {
  onDeleteAllDiscountClasses = () => {
    const { dispatch, form } = this.props;
    dispatch(change(form, "discountCourseClasses", []));
  };

  onDeleteDiscountClass = (item: NestedListItem) => {
    const { values, dispatch, form } = this.props;
    const classes = values.discountCourseClasses.filter(c => item.entityId !== c.id);
    dispatch(change(form, "discountCourseClasses", classes));
  };

  onAddDiscountClasses = (items: NestedListItem[]) => {
    const {
      values, dispatch, form, foundDiscountClasses
    } = this.props;
    const classes = values.discountCourseClasses.concat(
      items.map(item => foundDiscountClasses.find(c => item.entityId === c.id))
    );
    dispatch(change(form, "discountCourseClasses", classes));
  };

  render() {
    const {
      classes,
      values,
      foundDiscountClasses,
      discountClassesError,
      pending,
      searchDiscountClasses,
      clearDiscountClasses,
      submitSucceeded,
      twoColumn
    } = this.props;

    const discountClasses = values && values.discountCourseClasses ? values.discountCourseClasses : [];
    const discountClassItems = discountClasses ? discountClasses.map(v => courseClassToNestedListItem(v)) : [];
    const foundDiscountClassItems = foundDiscountClasses
      ? foundDiscountClasses.map(v => courseClassToNestedListItem(v))
      : [];

    return (
      <div className="p-3">
        <div className={twoColumn ? "mb-2 mw-800" : "mb-2"}>
          <NestedList
            formId={values.id}
            title={$t('classes')}
            titleCaption="This discount will only available for the following classes"
            searchPlaceholder="Find classes"
            values={discountClassItems}
            searchValues={foundDiscountClassItems}
            onSearch={searchDiscountClasses}
            clearSearchResult={clearDiscountClasses}
            pending={pending}
            onAdd={this.onAddDiscountClasses}
            onDelete={this.onDeleteDiscountClass}
            onDeleteAll={this.onDeleteAllDiscountClasses}
            sort={(a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1)}
            resetSearch={submitSucceeded}
            dataRowClass={classes.dataRowClass}
            aqlEntities={["CourseClass"]}
            aqlQueryError={discountClassesError}
          />
        </div>
        <FormControlLabel
          className="pr-3 mb-2"
          control={<FormField type="checkbox" name="addByDefault" color="secondary"  />}
          label={$t('add_this_discount_when_creating_or_duplicating_all')}
        />
        <Typography color="inherit" component="div" noWrap>
          {$t('require_at_least')}
          {" "}
          <FormField
            type="number"
            name="minEnrolments"
            inline
            validate={[validateSingleMandatoryField, validateNonNegative]}
            parse={normalizeNumber}
            debounced={false}
          />
          {" "}
          {$t('enrolments_on_one_invoice_from_the_classes_above')}
        </Typography>
        <Typography color="inherit" component="div" noWrap>
          {$t('require_at_least')}
          {" "}
          <FormField
            type="money"
            name="minValue"
            inline
            validate={[validateSingleMandatoryField, validateNonNegative]}
          />
          {" "}
          {$t('on_one_invoice')}
        </Typography>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  foundDiscountClasses: state.plainSearchRecords["CourseClass"].items,
  pending: state.plainSearchRecords["CourseClass"].loading,
  discountClassesError: state.plainSearchRecords["CourseClass"].error
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchDiscountClasses: (search: string) => {
    dispatch(setCommonPlainSearch("CourseClass", `${search ? `(${search}) AND ` : ""}(isDistantLearningCourse is true OR endDateTime > now) AND isCancelled is false`));
    dispatch(getCommonPlainRecords("CourseClass", 0, "course.name,uniqueCode,isActive", null, null, PLAIN_LIST_MAX_PAGE_SIZE, items => items.map(mapPlainDiscountClasses)));
  },
  clearDiscountClasses: (pending: boolean) => dispatch(clearCommonPlainRecords("CourseClass", pending))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(DiscountClasses, styles));
