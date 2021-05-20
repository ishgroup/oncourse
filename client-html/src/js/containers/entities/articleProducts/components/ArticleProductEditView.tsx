/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Account, ArticleProduct, Tax } from "@api/model";
import { connect } from "react-redux";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import ArticleProductGeneral from "./ArticleProductGeneral";
import { EditViewProps } from "../../../../model/common/ListView";

interface ArticleProductEditViewProps extends EditViewProps<ArticleProduct> {
  classes?: any;
  accounts?: Account[];
  taxes?: Tax[];
  access?: AccessState;
}

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <ArticleProductGeneral {...props} />
  },
  {
    label: "Corporate Passes",
    component: props => (
      <CorporatePassCommon
        path="corporatePasses"
        titleCaption="Corporate Pass which can be used to purchase this article"
        {...props}
      />
    )
  }
];

const ArticleProductEditView: React.FC<ArticleProductEditViewProps> = props => {
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
)(ArticleProductEditView);
