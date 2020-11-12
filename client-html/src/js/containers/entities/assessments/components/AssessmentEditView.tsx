/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Dispatch } from "redux";
import { Assessment } from "@api/model";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import AssessmentGeneralTab from "./AssessmentGeneralTab";
import AssessmentDocuments from "./AssessmentDocuments";

interface AssessmentEditViewProps {
  values?: Assessment;
  isNew?: boolean;
  isNested?: boolean;
  classes?: any;
  dispatch?: Dispatch<any>;
  dirty?: boolean;
  form?: string;
  rootEntity?: string;
  twoColumn?: boolean;
  showConfirm?: any;
  openNestedEditView?: any;
  manualLink?: string;
  onCloseClick?: any;
  toogleFullScreenEditView?: any;
}

const items: TabsListItem[] = [
  {
    label: "GENERAL",
    component: props => <AssessmentGeneralTab {...props} />
  },
  {
    label: "NOTES",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} />
  },
  {
    label: "DOCUMENTS",
    component: props => <AssessmentDocuments {...props} />
  }
];

const AssessmentEditView: React.FC<AssessmentEditViewProps> = props => {
  const {
    isNew,
    isNested,
    values,
    classes,
    dispatch,
    dirty,
    form,
    twoColumn,
    manualLink,
    showConfirm,
    rootEntity,
    onCloseClick,
    toogleFullScreenEditView
  } = props;

  return (
    <TabsList
      items={items}
      itemProps={{
        isNew,
        isNested,
        values,
        classes,
        dispatch,
        dirty,
        form,
        twoColumn,
        manualLink,
        showConfirm,
        onCloseClick,
        rootEntity,
        toogleFullScreenEditView
      }}
    />
  );
};

export default props => (props.values ? <AssessmentEditView {...props} /> : null);
