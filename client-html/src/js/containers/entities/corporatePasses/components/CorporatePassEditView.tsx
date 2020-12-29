import * as React from "react";
import TabsList from "../../../../common/components/layout/TabsList";

import CorporatePassSettings from "./CorporatePassSettings";
import CorporatePassDiscounts from "./CorporatePassDiscounts";
import CorporatePassLimit from "./CorporatePassLimit";

const items = [
  {
    label: "Settings",
    component: props => <CorporatePassSettings {...props} />
  },
  {
    label: "Limit",
    component: props => <CorporatePassLimit {...props} />
  },
  {
    label: "Discounts",
    component: props => <CorporatePassDiscounts {...props} />
  }
];

const CorporatePassEditView = props => {
  const {
    isNew,
    isNested,
    nestedIndex,
    values,
    classes,
    dispatch,
    dirty,
    form,
    twoColumn,
    submitSucceeded,
    manualLink
  } = props;

  return (
    <TabsList
      items={values ? items : []}
      itemProps={{
        isNew,
        isNested,
        nestedIndex,
        values,
        classes,
        dispatch,
        dirty,
        form,
        twoColumn,
        submitSucceeded,
        manualLink
      }}
    />
  );
};

export default CorporatePassEditView;
