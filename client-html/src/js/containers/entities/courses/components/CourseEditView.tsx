/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import { useSelector } from "react-redux";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import CourseClassesTab from "./CourseClassesTab";
import CourseGeneralTab from "./CourseGeneralTab";
import CourseMarketingTab from "./CourseMarketingTab";
import CourseVetTab from "./CourseVetTab";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import AvailabilityFormComponent from "../../../../common/components/form/availabilityComponent/AvailabilityFormComponent";
import { State } from "../../../../reducers/state";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <CourseGeneralTab {...props} />
  },
  {
    label: "Classes",
    component: props => <CourseClassesTab {...props} />
  },
  {
    label: "Marketing",
    component: props => <CourseMarketingTab {...props} />
  },
  {
    label: "Notes",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="pl-3 pr-3" />
  },
  {
    label: "Vet",
    component: props => <CourseVetTab {...props} />
  },
  {
    label: "Availability Rules",
    component: props => <AvailabilityFormComponent {...props} className="saveButtonTableOffset" />
  }
];

const styles = () =>
  createStyles({
    icon: {
      alignItems: "right"
    },
    moduleRowClass: {
      gridTemplateColumns: "1fr 0.5fr"
    }
  });

const CourseEditView = props => {
  const hasVetPermissions = useSelector<State, any>(state => state.access["VET_COURSE"]);

  const usedItems = useMemo(() => (hasVetPermissions ? items : items.filter(i => i.label !== "Vet")), [
    hasVetPermissions
  ]);

  return <TabsList onParentScroll={props.onScroll} items={props.values ? usedItems : []} itemProps={props} />;
};

export default withStyles(styles)(CourseEditView);
