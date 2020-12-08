import { MembershipDiscount, MembershipProduct } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { State } from "../../../../reducers/state";
import { PanelItemChangedMessage } from "../../../../common/components/form/nestedList/components/PaperListRenderer";
import NestedList, {
  NestedListItem,
  NestedListPanelItem
} from "../../../../common/components/form/nestedList/NestedList";
import { clearDiscountsSearch, searchDiscounts } from "../actions";
import RelatedCoursesCommon from "../../common/components/RelatedCoursesCommon";
import { EditViewProps } from "../../../../model/common/ListView";

interface MembershipDiscountsProps extends EditViewProps<MembershipProduct>{
  foundDiscounts?: MembershipDiscount[];
  searchDiscounts?: (search: string) => void;
  clearDiscountsSearch?: (pending: boolean) => void;
  discountsPending?: boolean;
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
    submitSucceeded,
    contactRelationTypes,
    dispatch,
    form
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
          aqlEntity="Discount"
          usePaper
        />
      </div>
      <RelatedCoursesCommon
        values={values}
        dispatch={dispatch}
        form={form}
        submitSucceeded={submitSucceeded}
      />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  foundDiscounts: state.membershipProducts.discountItems,
  discountsPending: state.membershipProducts.discountsPending,
  contactRelationTypes: state.membershipProducts.contactRelationTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchDiscounts: (search: string) => dispatch(searchDiscounts(search)),
  clearDiscountsSearch: (pending: boolean) => dispatch(clearDiscountsSearch(pending))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(MembershipProductDiscounts);
