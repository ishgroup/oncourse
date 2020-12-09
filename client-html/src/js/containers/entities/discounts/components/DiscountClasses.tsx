/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { change } from "redux-form";
import { FormControlLabel, Typography } from "@material-ui/core";
import { connect } from "react-redux";
import { Sale } from "@api/model";
import { Dispatch } from "redux";
import { createStyles, withStyles } from "@material-ui/core/styles";
import FormField from "../../../../common/components/form/form-fields/FormField";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { State } from "../../../../reducers/state";
import { clearCourseClassSales, getCourseClassSales } from "../../sales/actions";
import { normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";
import { validateSingleMandatoryField, validateNonNegative } from "../../../../common/utils/validation";
import { Classes } from "../../../../model/entities/CourseClass";

/**
 * @param sale
 */
const courseClassToNestedListItem = (sale: Sale): NestedListItem => ({
  id: sale.id.toString(),
  entityId: sale.id,
  primaryText: sale.name,
  secondaryText: sale.code,
  link: `/${Classes.path}?search=id is ${sale.id}`,
  active: sale.active
});

const styles = createStyles(() => ({
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
      <div className="p-3 pb-0">
        <div className={twoColumn ? "mb-2 mw-800" : "mb-2"}>
          <NestedList
            formId={values.id}
            title="Classes"
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
          />
        </div>
        <FormControlLabel
          className="pr-3 mb-2"
          control={<FormField type="checkbox" name="addByDefault" color="secondary" fullWidth />}
          label="Add this discount when creating or duplicating all classes"
        />
        <Typography color="inherit" component="div" noWrap>
          Require at least
          <FormField
            type="number"
            name="minEnrolments"
            color="primary"
            formatting="inline"
            hidePlaceholderInEditMode
            validate={[validateSingleMandatoryField, validateNonNegative]}
            parse={normalizeNumber}
          />
          enrolments on one invoice from the classes above
        </Typography>
        <Typography color="inherit" component="div" noWrap>
          Require at least
          <FormField
            type="money"
            name="minValue"
            color="primary"
            formatting="inline"
            hidePlaceholderInEditMode
            validate={[validateSingleMandatoryField, validateNonNegative]}
          />
          on one invoice
        </Typography>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  foundDiscountClasses: state.sales.courseClassItems,
  pending: state.sales.pending
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchDiscountClasses: (search: string) => {
    if (search) dispatch(getCourseClassSales(search));
  },
  clearDiscountClasses: (pending: boolean) => dispatch(clearCourseClassSales(pending))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(DiscountClasses));
