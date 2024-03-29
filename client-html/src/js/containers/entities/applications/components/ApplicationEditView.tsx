/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Application } from "@api/model";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import React from "react";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import { EditViewProps } from "../../../../model/common/ListView";
import ApplicationDocuments from "./ApplicationDocuments";
import ApplicationGeneral from "./ApplicationGeneral";

const styles = theme => createStyles({
  documentsRoot: {
    padding: theme.spacing(3)
  }
});

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <ApplicationGeneral {...props} />
  },
  {
    label: "Notes",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="pl-3 pr-3" />
  },
  {
    label: "Documents",
    component: props => <ApplicationDocuments {...props} />
  }
];

const ApplicationEditView: React.FC<EditViewProps<Application> & { classes: any }> = props => (
  <TabsList
    onParentScroll={props.onScroll}
    items={props.values ? items : []}
    itemProps={props}
  />
  );

export default withStyles(styles)(ApplicationEditView);
