/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import TabsList from "../../../../common/components/layout/TabsList";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import DiscountGeneral from "./DiscountGeneral";
import DiscountStudents from "./DiscountStudents";
import DiscountClasses from "./DiscountClasses";

const items = [
  {
    label: "General",
    component: props => <DiscountGeneral {...props} />
  },
  {
    label: "Students",
    component: props => <DiscountStudents {...props} />
  },
  {
    label: "Classes",
    component: props => <DiscountClasses {...props} />
  },
  {
    label: "Corporate passes",
    component: props => (
      <CorporatePassCommon
        path="corporatePassDiscounts"
        title="LIMIT WITH CORPORATE PASS"
        titleCaption="This discount is only be available with the following Corporate Passes"
        {...props}
      />
    )
  }
];

const DiscountEditView = React.memo<any>(props => {
  const {
    isNew,
    isNested,
    values,
    dispatch,
    dirty,
    form,
    nestedIndex,
    rootEntity,
    twoColumn,
    showConfirm,
    openNestedEditView,
    manualLink,
    access
  } = props;

  const corporatePassAccess = access[plainCorporatePassPath] && access[plainCorporatePassPath]["GET"];

  const checkedItems = items.filter(i => i.label !== "Corporate passes" || corporatePassAccess);

  return values ? (
    <>
      <TabsList
        items={checkedItems}
        itemProps={{
          isNew,
          isNested,
          values,
          dispatch,
          dirty,
          form,
          nestedIndex,
          rootEntity,
          twoColumn,
          showConfirm,
          openNestedEditView,
          manualLink
        }}
      />
    </>
  ) : null;
});

const mapStateToProps = (state: State) => ({
  access: state.access
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(DiscountEditView);
