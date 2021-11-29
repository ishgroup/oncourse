/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { format } from "date-fns";
import { Discount, DiscountType } from "@api/model";
import { checkPermissions } from "../../../common/actions";
import { plainCorporatePassPath } from "../../../constants/Api";
import { FilterGroup } from "../../../model/common/ListView";
import ListView from "../../../common/components/list-view/ListView";
import {
  setListEditRecord,
  getFilters,
  clearListState,
  setFilterGroups
} from "../../../common/components/list-view/actions";
import {
  getDiscount,
  updateDiscount,
  createDiscount,
  removeDiscount,
  getDiscountContactRelationTypes,
  getDiscountCosAccounts
} from "./actions";
import DiscountEditView from "./components/DiscountEditView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { III_DD_MMM_YYYY } from "../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getEntityTags } from "../../tags/actions";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Active",
        expression: "((validTo >= today) or (validTo == null)) and ((validFrom <= today) or (validFrom == null))",
        active: false
      },
      {
        name: "Inactive",
        expression: "((validTo <= today) or (validFrom >= today))",
        active: false
      }
    ]
  }
];

const Initial: Discount = {
  addByDefault: false,
  code: null,
  availableOnWeb: true,
  corporatePassDiscounts: [],
  cosAccount: null,
  description: null,
  discountConcessionTypes: [],
  discountCourseClasses: [],
  discountMax: null,
  discountMemberships: [],
  discountMin: null,
  discountPercent: 0,
  discountType: "Percent",
  discountValue: 0,
  hideOnWeb: false,
  minEnrolments: 0,
  minValue: 0,
  name: null,
  predictedStudentsPercentage: 0.1,
  rounding: "No Rounding",
  studentAge: null,
  studentAgeUnder: null,
  studentEnrolledWithinDays: null,
  studentPostcode: null,
  validFrom: null,
  validFromOffset: null,
  validTo: null,
  validToOffset: null
};

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Discount and entityId" },
  { title: "Classes", list: "class", expression: "discountCourseClasses.discount.id" },
  { title: "Enrolments", list: "enrolment", expression: "invoiceLines.invoiceLineDiscounts.discount.id" },
  { title: "Memberships", list: "membership", expression: "discountMemberships.discount.id" }
];

const formatDiscountDate = v => (v ? format(new Date(v), III_DD_MMM_YYYY) : "any date");

const customColumnFormats = {
  validFrom: formatDiscountDate,
  validTo: formatDiscountDate
};

const manualLink = getManualLink("discounts");

class Discounts extends React.PureComponent<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getTagsForClassesSearch();
    this.props.getRecords();
    this.props.checkPermissions();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  secondaryColumnCondition = discount => {
    if (discount.discountType === DiscountType["Fee override"] && discount.discountDollar) {
      return `Fix price to ${discount.discountDollar}`;
    }
    if (discount.discountType === DiscountType["Dollar"] && discount.discountDollar) {
      return `${discount.discountDollar} discount`;
    }
    if (discount.discountType === DiscountType["Percent"] && discount.discountPercent) {
      return `${discount.discountPercent} discount`;
    }
    return "";
  };

  render() {
    const {
      getDiscountRecord,
      onDelete,
      onInit,
      onCreate,
      onSave
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            customColumnFormats,
            primaryColumn: "name",
            secondaryColumn: "discountDollar",
            secondaryColumnCondition: this.secondaryColumnCondition
          }}
          editViewProps={{
            manualLink,
            hideTitle: true
          }}
          EditViewContent={DiscountEditView}
          getEditRecord={getDiscountRecord}
          rootEntity="Discount"
          onInit={onInit}
          onCreate={onCreate}
          onDelete={onDelete}
          onSave={onSave}
          findRelated={findRelatedGroup}
          filterGroupsInitial={filterGroups}
          noListTags
        />
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getRecords: () => {
    dispatch(getDiscountContactRelationTypes());
    dispatch(getDiscountCosAccounts());
  },
  getFilters: () => {
    dispatch(getFilters("Discount"));
  },
  getTagsForClassesSearch: () => {
    dispatch(getEntityTags("CourseClass"));
    dispatch(getEntityTags("Course"));
  },
  clearListState: () => dispatch(clearListState()),
  getDiscountRecord: (id: string) => dispatch(getDiscount(id)),
  onSave: (id: string, discount: Discount) => dispatch(updateDiscount(id, discount)),
  onCreate: (discount: Discount) => dispatch(createDiscount(discount)),
  onDelete: (id: string) => dispatch(removeDiscount(id)),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  checkPermissions: () => dispatch(checkPermissions({ path: plainCorporatePassPath, method: "GET" }))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Discounts);
