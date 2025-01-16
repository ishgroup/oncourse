import * as React from "react";
import TabsList from "../../../../common/components/navigation/TabsList";
import CorporatePassDiscounts from "./CorporatePassDiscounts";
import CorporatePassLimit from "./CorporatePassLimit";

import CorporatePassSettings from "./CorporatePassSettings";

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
    values,
    classes,
    dispatch,
    dirty,
    form,
    twoColumn,
    submitSucceeded,
    manualLink,
    syncErrors,
    onScroll
  } = props;

  return (
    <TabsList
      onParentScroll={onScroll}
      items={values ? items : []}
      itemProps={{
        isNew,
        isNested,
        syncErrors,
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
