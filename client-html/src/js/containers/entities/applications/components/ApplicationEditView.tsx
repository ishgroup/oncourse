/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import { Application } from "@api/model";
import { Dispatch } from "redux";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import ApplicationGeneral from "./ApplicationGeneral";
import ApplicationDocuments from "./ApplicationDocuments";

const styles = theme => createStyles({
  documentsRoot: {
    padding: theme.spacing(0, 3, 9, 3)
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

interface ApplicationEditViewProps {
  values?: Application;
  isNew?: boolean;
  isNested?: boolean;
  classes?: any;
  dispatch?: Dispatch<any>;
  dirty?: boolean;
  form?: string;
  nestedIndex?: number;
  rootEntity?: string;
  twoColumn?: boolean;
  showConfirm?: any;
  openNestedEditView?: any;
  manualLink?: string;
}

const ApplicationEditView: React.FC<ApplicationEditViewProps> = props => {
  const {
    values,
    isNew,
    isNested,
    classes,
    dispatch,
    dirty,
    form,
    nestedIndex,
    rootEntity,
    twoColumn,
    showConfirm,
    openNestedEditView,
    manualLink
  } = props;

  return (
    <TabsList
      items={values ? items : []}
      itemProps={{
        isNew,
        isNested,
        values,
        classes,
        dispatch,
        dirty,
        form,
        nestedIndex,
        rootEntity,
        twoColumn,
        showConfirm,
        openNestedEditView,
        manualLink
      }}
    />
  );
};

export default withStyles(styles)(ApplicationEditView);
