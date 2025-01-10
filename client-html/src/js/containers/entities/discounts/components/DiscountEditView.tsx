/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { connect } from "react-redux";
import TabsList from "../../../../common/components/navigation/TabsList";
import { plainCorporatePassPath } from "../../../../constants/Api";
import { State } from "../../../../reducers/state";
import CorporatePassCommon from "../../common/components/CorporatePassCommon";
import DiscountClasses from "./DiscountClasses";
import DiscountGeneral from "./DiscountGeneral";
import DiscountStudents from "./DiscountStudents";

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
    rootEntity,
    twoColumn,
    showConfirm,
    manualLink,
    access,
    syncErrors,
    onScroll
  } = props;

  const corporatePassAccess = access[plainCorporatePassPath] && access[plainCorporatePassPath]["GET"];

  const checkedItems = items.filter(i => i.label !== "Corporate passes" || corporatePassAccess);

  return values ? (
    <>
      <TabsList
        onParentScroll={onScroll}
        items={checkedItems}
        itemProps={{
          isNew,
          isNested,
          values,
          dispatch,
          dirty,
          form,
          rootEntity,
          twoColumn,
          showConfirm,
          manualLink,
          syncErrors
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
