import React, { Component } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { CorporatePass, Discount } from "@api/model";
import { change } from "redux-form";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { State } from "../../../../reducers/state";
import { discountSort, mapPlainDiscounts, transformDiscountForNestedList } from "../../discounts/utils";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

interface Props {
  values?: CorporatePass;
  dispatch?: any;
  form?: string;
  discounts: Discount[];
  getSearchResult: (search: string) => void;
  clearSearchResult: (pending: boolean) => void;
  submitSucceeded?: boolean;
  pending: boolean;
  discountsError: boolean;
}

class CorporatePassDiscounts extends Component<Props, any> {
  onDelete = (discountToDelete: NestedListItem) => {
    const { values, dispatch, form } = this.props;
    dispatch(
      change(
        form,
        "linkedDiscounts",
        values.linkedDiscounts.filter(discount => discount.id !== discountToDelete.entityId)
      )
    );
  };

  onAdd = (discountsToAdd: NestedListItem[]) => {
    const {
     values, discounts, dispatch, form
    } = this.props;

    const newDiscountList = values.linkedDiscounts.concat(
      discountsToAdd.map(v1 => discounts.find(v2 => v2.id === v1.entityId))
    );
    newDiscountList.sort(discountSort);
    dispatch(change(form, "linkedDiscounts", newDiscountList));
  };

  render() {
    const {
      discounts, getSearchResult, clearSearchResult, submitSucceeded, pending, values, discountsError
    } = this.props;

    const listValues = values && values.linkedDiscounts ? values.linkedDiscounts.map(transformDiscountForNestedList) : [];
    const searchValues = discounts ? discounts.map(transformDiscountForNestedList) : [];

    return (
      <div className="pl-3 pr-3 pb-3 saveButtonTableOffset">
        <div className="mw-500">
          <NestedList
            formId={values.id}
            title="Auto-Apply Discounts"
            searchPlaceholder="Find discounts"
            values={listValues}
            searchValues={searchValues}
            pending={pending}
            onAdd={this.onAdd}
            onDelete={this.onDelete}
            onSearch={getSearchResult}
            clearSearchResult={clearSearchResult}
            sort={discountSort}
            resetSearch={submitSucceeded}
            aqlEntities={["Discount"]}
            aqlQueryError={discountsError}
            usePaper
          />
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  discounts: state.plainSearchRecords["Discount"].items.map(mapPlainDiscounts),
  pending: state.plainSearchRecords["Discount"].loading,
  discountsError: state.plainSearchRecords["Discount"].error,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSearchResult: (search: string) => {
    dispatch(setCommonPlainSearch("Discount", search));
    dispatch(getCommonPlainRecords("Discount", 0, "name,discountType,discountDollar,discountPercent", null, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearSearchResult: (pending: boolean) => dispatch(clearCommonPlainRecords("Discount", pending)),
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CorporatePassDiscounts);
