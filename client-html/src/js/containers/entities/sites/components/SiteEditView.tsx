/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList from "../../../../common/components/navigation/TabsList";
import SitesGeneral from "./SitesGeneral";
import Directions from "./SitesDirections";
import AvailabilityFormComponent from "../../../../common/components/form/availabilityComponent/AvailabilityFormComponent";

const items = [
  {
    label: "General",
    component: props => <SitesGeneral {...props} />
  },
  {
    label: "Directions",
    component: props => <Directions {...props} />
  },
  {
    label: "Notes",
    component: props => <OwnApiNotes {...props} />
  },
  {
    label: "Availability Rules",
    component: props => <AvailabilityFormComponent {...props} timezone={props.values && props.values.timezone} />
  }
];

const SiteEditView = props => <TabsList
  items={props.values ? items : []}
  itemProps={...props}
/>;

export default SiteEditView;
