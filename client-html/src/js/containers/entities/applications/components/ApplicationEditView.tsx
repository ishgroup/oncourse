/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import { Application } from "@api/model";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import ApplicationGeneral from "./ApplicationGeneral";
import ApplicationDocuments from "./ApplicationDocuments";
import { EditViewProps } from "../../../../model/common/ListView";

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
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} />
  },
  {
    label: "Documents",
    component: props => <ApplicationDocuments {...props} />
  }
];

const ApplicationEditView: React.FC<EditViewProps<Application> & { classes: any }> = props => (
  <TabsList
    items={props.values ? items : []}
    itemProps={props}
  />
  );

export default withStyles(styles)(ApplicationEditView);
