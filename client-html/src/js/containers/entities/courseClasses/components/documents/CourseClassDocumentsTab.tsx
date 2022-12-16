/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { FieldArray } from "redux-form";
import { Grid, Divider } from "@mui/material";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import DocumentsRenderer from "../../../../../common/components/form/documents/DocumentsRenderer";

const CourseClassDocumentsTab: React.FC<EditViewProps<CourseClassExtended>> = ({
  twoColumn,
  dispatch,
  form,
  showConfirm,
  isNew
}) => (
  <Grid container className="pl-3 pr-3 pb-2">
    <FieldArray
      name="documents"
      label="Documents"
      entity="CourseClass"
      component={DocumentsRenderer}
      isNew={isNew}
      xsGrid={12}
      mdGrid={twoColumn ? 6 : 12}
      lgGrid={twoColumn ? 4 : 12}
      dispatch={dispatch}
      form={form}
      showConfirm={showConfirm}
      rerenderOnEveryChange
    />
    <Grid item xs={12}>
      <Divider className="mt-2" />
    </Grid>
  </Grid>
);

export default CourseClassDocumentsTab;
