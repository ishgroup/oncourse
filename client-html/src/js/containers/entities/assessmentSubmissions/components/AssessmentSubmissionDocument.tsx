/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { FieldArray } from "redux-form";
import Grid from "@material-ui/core/Grid/Grid";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";

interface AssessmentDocumentsProps {
  classes?: any;
  dispatch?: any;
  form?: string;
  showConfirm?: any;
  twoColumn?: boolean;
}

const AssessmentSubmissionDocuments: React.FC<AssessmentDocumentsProps> = props => {
  const {
    classes, dispatch, form, showConfirm, twoColumn
  } = props;

  return (
    <Grid container className="p-3 saveButtonTableOffset">
      <FieldArray
        name="documents"
        label="Documents"
        entity="AssessmentSubmission"
        classes={classes}
        component={DocumentsRenderer}
        xsGrid={12}
        mdGrid={twoColumn ? 6 : 12}
        lgGrid={twoColumn ? 4 : 12}
        dispatch={dispatch}
        form={form}
        showConfirm={showConfirm}
        rerenderOnEveryChange
      />
    </Grid>
  );
};

export default AssessmentSubmissionDocuments;
