/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Account, VoucherProduct } from "@api/model";
import { connect } from "react-redux";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import VoucherProductGeneral from "./VoucherProductGeneral";
import { EditViewProps } from "../../../../model/common/ListView";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";

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
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="mb-2" />
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
    openNestedEditView,
    manualLink,
    accounts,
    access,
    submitSucceeded,
    syncErrors
  } = props;

  const corporatePassAccess = access[plainCorporatePassPath] && access[plainCorporatePassPath]["GET"];

  const checkedItems = items.filter(i => i.label !== "Corporate passes" || corporatePassAccess);

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
