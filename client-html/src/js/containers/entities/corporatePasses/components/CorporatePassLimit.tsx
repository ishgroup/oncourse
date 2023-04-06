/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Component } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import { CorporatePass, Sale, SaleType } from "@api/model";
import { change } from "redux-form";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { State } from "../../../../reducers/state";
import { clearSales, getSales } from "../../sales/actions";
import { entityForLink } from "../../common/utils";
import { mapPlainDiscountClasses } from "../../discounts/utils";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

const styles = createStyles({
  dataRowClass: {
    gridTemplateColumns: "3fr 2fr"
  }
});

interface Props {
  classes?: any;
  values?: CorporatePass;
  dispatch?: any;
  form?: string;
  sales: Sale[];
  courseClassItems: Sale[];
  getSearchResult: (search: string) => void;
  searchCourseClassSales: (search: string) => void;
  clearSearchResult: (pending: boolean) => void;
  clearCourseClassSales: (pending: boolean) => void;
  submitSucceeded?: boolean;
  pending?: boolean;
  salesError?: boolean;
  discountClassesError?: boolean;
}

const transform = (sale: Sale): NestedListItem => ({
    id: sale.type + sale.id,
    entityId: sale.id,
    entityName: sale.type,
    primaryText: sale.name,
    secondaryText: sale.code,
    link:
      sale.type === SaleType.Class
        ? `/class?search=id is ${sale.id}`
        : `/${entityForLink(sale.type)}/${sale.id}`,
    active: typeof sale.active === "boolean" ? sale.active : true
  });

const salesSort = (a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1);

class CorporatePassLimit extends Component<Props, any> {
  onDeleteAll = () => {
    const { dispatch, form } = this.props;

    dispatch(change(form, "linkedSalables", []));
  };

  onDelete = (saleToDelete: NestedListItem) => {
    const { values, dispatch, form } = this.props;
    dispatch(
      change(
        form,
        "linkedSalables",
        values.linkedSalables.filter(sale => sale.id !== saleToDelete.entityId || sale.type !== saleToDelete.entityName)
      )
    );
  };

  onAdd = (salesToAdd: NestedListItem[]) => {
    const {
     values, sales, courseClassItems, dispatch, form
    } = this.props;

    const salesCombined = (sales || []).concat(courseClassItems || []);

    const newSalesList = values.linkedSalables.concat(
      salesToAdd.map(v1 => salesCombined.find(v2 => v2.id === v1.entityId && v2.type === v1.entityName))
    );

    newSalesList.sort(salesSort);
    dispatch(change(form, "linkedSalables", newSalesList));
  };

  render() {
    const {
      classes,
      values,
      sales,
      courseClassItems,
      getSearchResult,
      searchCourseClassSales,
      clearSearchResult,
      clearCourseClassSales,
      discountClassesError,
      submitSucceeded,
      pending,
      salesError
    } = this.props;

    const listValues = values && values.linkedSalables ? values.linkedSalables.map(transform) : [];
    let searchValues = [];

    if (sales) {
      searchValues = searchValues.concat(sales.map(transform));
    }

    if (courseClassItems) {
      searchValues = searchValues.concat(courseClassItems.map(transform));
    }

    return (
      <div className="pl-3 pr-3 mb-2">
        <div className="mw-600">
          <NestedList
            title="Limit Use"
            titleCaption="This pass will only be available for the following classes and products"
            formId={values.id}
            values={listValues}
            searchValues={searchValues}
            pending={pending}
            onAdd={this.onAdd}
            onDelete={this.onDelete}
            onDeleteAll={this.onDeleteAll}
            onSearch={(search, entity) => {
              switch (entity) {
                case "Product":
                  getSearchResult(search);
                  break;
                case "CourseClass":
                  searchCourseClassSales(search);
                  break;
              }
            }}
            clearSearchResult={(pending, entity) => {
              switch (entity) {
                case "Product":
                  clearSearchResult(pending);
                  break;
                case "CourseClass":
                  clearCourseClassSales(pending);
                  break;
              }
            }}
            sort={salesSort}
            resetSearch={submitSucceeded}
            dataRowClass={classes.dataRowClass}
            searchType="withToggle"
            aqlEntities={["Product", "CourseClass"]}
            aqlQueryError={discountClassesError || salesError}
          />
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  sales: state.sales.items,
  salesError: state.sales.error,
  courseClassItems: state.plainSearchRecords["CourseClass"].items,
  pending: state.plainSearchRecords["CourseClass"].loading,
  discountClassesError: state.plainSearchRecords["CourseClass"].error
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSearchResult: (search: string) => {
    if (search) dispatch(getSales(search));
  },
  clearSearchResult: (pending: boolean) => dispatch(clearSales(pending)),
  searchCourseClassSales: (search: string) => {
    dispatch(setCommonPlainSearch("CourseClass", `${search ? `(${search}) AND ` : ""}(isDistantLearningCourse is true OR endDateTime > now) AND isCancelled is false`));
    dispatch(getCommonPlainRecords("CourseClass", 0, "course.name,uniqueCode,isActive", null, null, PLAIN_LIST_MAX_PAGE_SIZE, items => items.map(mapPlainDiscountClasses)));
  },
  clearCourseClassSales: (pending: boolean) => dispatch(clearCommonPlainRecords("CourseClass", pending))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(CorporatePassLimit));
