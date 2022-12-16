/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { FieldArray } from "redux-form";
import Grid from "@mui/material/Grid";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import { EditViewProps } from "../../../../model/common/ListView";
import { Assessment } from "@api/model";


const AssessmentDocuments: React.FC<EditViewProps<Assessment>> = props => {
  const {
    classes, dispatch, form, showConfirm, twoColumn
  } = props;

  return (
    <Grid container columnSpacing={3} className="p-3 saveButtonTableOffset">
      <FieldArray
        name="documents"
        label="Documents"
        entity="Assessment"
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

export default AssessmentDocuments;
