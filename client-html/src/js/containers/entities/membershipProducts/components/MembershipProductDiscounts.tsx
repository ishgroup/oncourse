import { MembershipDiscount, MembershipProduct } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change, FieldArray } from "redux-form";
import { State } from "../../../../reducers/state";
import { PanelItemChangedMessage } from "../../../../common/components/form/nestedList/components/PaperListRenderer";
import NestedList, {
  NestedListItem,
  NestedListPanelItem
} from "../../../../common/components/form/nestedList/NestedList";
import RelationsCommon from "../../common/components/RelationsCommon";
import { EditViewProps } from "../../../../model/common/ListView";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";

interface MembershipDiscountsProps extends EditViewProps<MembershipProduct>{
  foundDiscounts?: MembershipDiscount[];
  searchDiscounts?: (search: string) => void;
  clearDiscountsSearch?: (pending: boolean) => void;
  discountsPending?: boolean;
  discountsError?: boolean;
  contactRelationTypes?: NestedListPanelItem[];
}

const discountsToNestedListItems = (discounts: MembershipDiscount[]): NestedListItem[] =>
  (discounts
    ? discounts.map(md => ({
        id: md.discountId.toString(),
        entityId: md.discountId,
        primaryText: md.discountName,
        secondaryText: "",
        panelItemIds: md.contactRelationTypes,
        link: `/discount/${md.discountId}`,
        active: true
      }))
    : []);

const onAddDiscounts = props => (items: NestedListItem[]) => {
  const {
    values, dispatch, form, foundDiscounts
  } = props;
  const discounts = values.membershipDiscounts.concat(
    items.map(item => foundDiscounts.find(d => d.discountId === item.entityId))
  );
  dispatch(change(form, "membershipDiscounts", discounts));
};

const onDeleteAllDiscounts = props => () => {
  const { dispatch, form } = props;
  dispatch(change(form, "membershipDiscounts", []));
};

const onDeleteDiscount = props => (item: NestedListItem) => {
  const { values, dispatch, form } = props;
  const discounts = values.membershipDiscounts.filter(md => item.entityId !== md.discountId);
  dispatch(change(form, "membershipDiscounts", discounts));
};

const onChangePanelItem = props => (message: PanelItemChangedMessage) => {
  const { values, dispatch, form } = props;
  const discountId = values.membershipDiscounts.findIndex(discount => discount.discountId === message.item.entityId);
  const contactRelations = values.membershipDiscounts[discountId].contactRelationTypes
    ? values.membershipDiscounts[discountId].contactRelationTypes
    : [];
  dispatch(
    change(
      form,
      `membershipDiscounts[${discountId}].contactRelationTypes`,
      message.checked
        ? [...contactRelations, message.panelItemId].sort()
        : contactRelations.filter(e => e !== message.panelItemId).sort()
    )
  );
};

const MembershipProductDiscounts: React.FC<MembershipDiscountsProps> = props => {
  const {
    twoColumn,
    values,
    foundDiscounts,
    searchDiscounts,
    clearDiscountsSearch,
    discountsPending,
    discountsError,
    submitSucceeded,
    contactRelationTypes,
    dispatch,
    form,
    rootEntity,
    showConfirm
  } = props;

  const discounts = values ? values.membershipDiscounts : [];

  return (
    <div className="pt-0 pr-3 pb-0 pl-3">
      <div className={twoColumn ? "mb-2 mw-400" : "mb-2"}>
        <NestedList
          formId={values.id}
          title="DISCOUNTS"
          searchPlaceholder="Add discounts"
          values={discountsToNestedListItems(discounts)}
          searchValues={discountsToNestedListItems(foundDiscounts)}
          onSearch={searchDiscounts}
          clearSearchResult={clearDiscountsSearch}
          pending={discountsPending}
          aqlQueryError={discountsError}
          onAdd={onAddDiscounts(props)}
          onDelete={onDeleteDiscount(props)}
          onDeleteAll={onDeleteAllDiscounts(props)}
          onChangePanelItem={onChangePanelItem(props)}
          sort={(a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1)}
          resetSearch={submitSucceeded}
          inlineSecondaryText
          panelItems={contactRelationTypes}
          panelCaption="Also apply to"
          searchType="withToggle"
          aqlEntities={["Discount"]}
          usePaper
        />
      </div>
      <RelationsCommon
        values={values}
        dispatch={dispatch}
        form={form}
        submitSucceeded={submitSucceeded}
        rootEntity={rootEntity}
      />

      <div className="pb-3 mb-3 mt-1">
        <FieldArray
          name="documents"
          label="Documents"
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
      </div>
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  foundDiscounts: state.plainSearchRecords["Discount"].items,
  discountsPending: state.plainSearchRecords["Discount"].loading,
  discountsError: state.plainSearchRecords["Discount"].error,
  contactRelationTypes: state.plainSearchRecords["ContactRelationType"].items
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchDiscounts: (search: string) => {
    dispatch(setCommonPlainSearch("Discount", `${search} and (validTo == null or validTo >= today)`));
    dispatch(getCommonPlainRecords("Discount", 0, "name", null, null, PLAIN_LIST_MAX_PAGE_SIZE, items => items.map(d => ({ discountId: d.id, discountName: d.name }))
      .sort((a, b) => {
        if (a.discountId < b.discountId) {
          return -1;
        }
        if (a.discountId > b.discountId) {
          return 1;
        }
        return 0;
      })));
  },
  clearDiscountsSearch: (pending: boolean) => dispatch(clearCommonPlainRecords("Discount", pending))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(MembershipProductDiscounts);
