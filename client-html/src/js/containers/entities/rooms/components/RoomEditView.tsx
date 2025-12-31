/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import AvailabilityFormComponent
  from "../../../../common/components/form/availabilityComponent/AvailabilityFormComponent";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList from "../../../../common/components/navigation/TabsList";
import RoomsGeneral from "./RoomsGeneral";

const items = [
  {
    label: "General",
    component: props => <RoomsGeneral {...props} />
  },
  {
    label: "Notes",
    component: props => <OwnApiNotes {...props} className="pl-3 pr-3" />
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
    values,
    classes,
    dispatch,
    dirty,
    form,
    twoColumn,
    showConfirm,
    manualLink,
    rootEntity,
    onEditViewScroll,
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
        values,
        classes,
        dispatch,
        dirty,
        form,
        twoColumn,
        showConfirm,
        manualLink,
        rootEntity,
        onEditViewScroll,
        syncErrors
      }}
    />
 );
};

export default RoomEditView;
