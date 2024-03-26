/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import FacultyCoursesTab from "./FacultyCoursesTab";
import FacultyGeneralTab from "./FacultyGeneralTab";
import FacultyMarketingTab from "./FacultyMarketingTab";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <FacultyGeneralTab {...props} />
  },
  {
    label: "Relations",
    component: props => <FacultyCoursesTab {...props} />
  },
  {
    label: "Marketing",
    component: props => <FacultyMarketingTab {...props} />
  },
  {
    label: "Notes",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="pl-3 pr-3" />
  }
];

function FacultyEditView(props) {
  return <TabsList onParentScroll={props.onScroll} items={props.values ? items : []} itemProps={props} />;
}

export default FacultyEditView;