/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Account, VoucherProduct } from "@api/model";
import React from "react";
import { connect } from "react-redux";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { EditViewProps } from "../../../../model/common/ListView";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import VoucherProductGeneral from "./VoucherProductGeneral";

interface VoucherProductEditViewProps extends EditViewProps<VoucherProduct>{
  accounts?: Account[];
  access?: AccessState;
}
const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <VoucherProductGeneral {...props} />
  },
  {
    label: "Notes",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="pl-3 pr-3 mb-2" />
  },
  {
    label: "Corporate passes",
    component: ({ classes, ...rest }) => (
      <CorporatePassCommon
        path="corporatePasses"
        titleCaption="Corporate Pass which can be used to purchase this voucher"
        {...rest}
      />
    )
  }
];
const VoucherProductEditView: React.FC<VoucherProductEditViewProps> = props => {
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
    access,
    submitSucceeded,
    syncErrors,
    onScroll
  } = props;

  const corporatePassAccess = access[plainCorporatePassPath] && access[plainCorporatePassPath]["GET"];

  const checkedItems = items.filter(i => i.label !== "Corporate passes" || corporatePassAccess);

  return (
    <TabsList
      onParentScroll={onScroll}
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
)(VoucherProductEditView);
