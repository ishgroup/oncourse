/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList from "../../../../common/components/layout/TabsList";
import RoomsGeneral from "./RoomsGeneral";
import AvailabilityFormComponent from "../../../../common/components/form/availabilityComponent/AvailabilityFormComponent";

const items = [
  {
    label: "General",
    component: props => <RoomsGeneral {...props} />
  },
  {
    label: "Notes",
    component: props => <OwnApiNotes {...props} />
  },
  {
    label: "Availability Rules",
    component: props => <AvailabilityFormComponent {...props} timezone={props.values && props.values.siteTimeZone} />
  }
];

const RoomEditView = props => {
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
    openNestedEditView,
    showConfirm,
    manualLink,
    rootEntity
  } = props;

  return (
    <>
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
          openNestedEditView,
          showConfirm,
          manualLink,
          rootEntity
        }}
      />
    </>
  );
};

export default RoomEditView;
