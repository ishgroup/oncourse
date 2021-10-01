/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import TabsList from "../../../../common/components/layout/TabsList";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import AssessmentSubmissionGeneralTab from "./AssessmentSubmissionGeneralTab";
import AssessmentSubmissionDocuments from "./AssessmentSubmissionDocument";

const items = [
  {
    label: "GENERAL",
    component: props => <AssessmentSubmissionGeneralTab {...props} />
  },
  {
    label: "NOTES",
    component: ({ ...props }) => <OwnApiNotes {...props as any} />
  },
  {
    label: "DOCUMENTS",
    component: props => <AssessmentSubmissionDocuments {...props} />
  },
];

const AssessmentSubmissionEditView = props =>
   (
     <TabsList
       items={props.values ? items : []}
       itemProps={...props}
     />
  );
export default AssessmentSubmissionEditView;
