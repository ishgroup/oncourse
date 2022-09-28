/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { Account, MembershipProduct, Tax } from "@api/model";
import { connect } from "react-redux";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import MembershipProductGeneral from "./MembershipProductGeneral";
import MembershipProductDiscounts from "./MembershipProductDiscounts";
import { EditViewProps } from "../../../../model/common/ListView";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";

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
    label: "Notes",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="mb-2" />
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

interface MembershipProductEditViewProps extends EditViewProps<MembershipProduct> {
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
    manualLink,
    accounts,
    taxes,
    access,
    submitSucceeded,
    syncErrors
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
        manualLink,
        accounts,
        taxes,
        submitSucceeded,
        syncErrors
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
