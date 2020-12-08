/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Account, MembershipProduct, Tax } from "@api/model";
import { connect } from "react-redux";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import MembershipProductGeneral from "./MembershipProductGeneral";
import MembershipProductDiscounts from "./MembershipProductDiscounts";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <MembershipProductGeneral {...props} />
  },
  {
    label: "Discounts",
    component: props => <MembershipProductDiscounts {...props} />
  },
  {
    label: "Corporate Passes",
    component: props => (
      <CorporatePassCommon
        path="corporatePasses"
        titleCaption="Corporate Pass which can be used to purchase this membership"
        {...props}
      />
    )
  }
];

interface MembershipProductEditViewProps {
  values?: MembershipProduct;
  isNew?: boolean;
  isNested?: boolean;
  classes?: any;
  dispatch?: any;
  dirty?: boolean;
  form?: string;
  nestedIndex?: number;
  rootEntity?: string;
  twoColumn?: boolean;
  submitSucceeded?: boolean;
  showConfirm?: any;
  openNestedEditView?: any;
  manualLink?: string;
  accounts?: Account[];
  taxes?: Tax[];
  access?: AccessState;
}

const MembershipProductEditView: React.FC<MembershipProductEditViewProps> = props => {
  const {
    values,
    isNew,
    isNested,
    dispatch,
    dirty,
    form,
    nestedIndex,
    rootEntity,
    twoColumn,
    showConfirm,
    openNestedEditView,
    manualLink,
    accounts,
    taxes,
    access,
    submitSucceeded
  } = props;

  const corporatePassAccess = access[plainCorporatePassPath] && access[plainCorporatePassPath]["GET"];

  const checkedItems = items.filter(i => i.label !== "Corporate Passes" || corporatePassAccess);

  return (
    <TabsList
      items={values ? checkedItems : []}
      itemProps={{
        values,
        isNew,
        isNested,
        dispatch,
        dirty,
        form,
        nestedIndex,
        rootEntity,
        twoColumn,
        showConfirm,
        openNestedEditView,
        manualLink,
        accounts,
        taxes,
        submitSucceeded
      }}
    />
  );
};

const mapStateToProps = (state: State) => ({
  access: state.access
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(MembershipProductEditView);
