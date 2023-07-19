/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Assessment } from "@api/model";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import AssessmentGeneralTab from "./AssessmentGeneralTab";
import AssessmentDocuments from "./AssessmentDocuments";
import { EditViewProps } from "../../../../model/common/ListView";


const items: TabsListItem[] = [
  {
    label: "GENERAL",
    component: props => <AssessmentGeneralTab {...props} />
  },
  {
    label: "NOTES",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="pl-3 pr-3" />
  },
  {
    label: "DOCUMENTS",
    component: props => <AssessmentDocuments {...props} />
  }
];

const AssessmentEditView: React.FC<EditViewProps<Assessment>> = props => {
  const {
    isNew,
    isNested,
    values,
    dispatch,
    dirty,
    form,
    twoColumn,
    manualLink,
    showConfirm,
    rootEntity,
    onCloseClick,
    toogleFullScreenEditView,
    syncErrors,
    onScroll
  } = props;

  return (
    <TabsList
      onParentScroll={onScroll}
      items={items}
      itemProps={{
        isNew,
        syncErrors,
        isNested,
        values,
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
