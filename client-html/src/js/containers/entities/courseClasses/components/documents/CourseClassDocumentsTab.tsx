/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Divider, Grid } from "@mui/material";
import { format } from "date-fns";
import { III_DD_MMM_YYYY_HH_MM } from "ish-ui";
import React, { useMemo } from "react";
import { FieldArray } from "redux-form";
import DocumentsRenderer from "../../../../../common/components/form/documents/DocumentsRenderer";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";

const CourseClassDocumentsTab: React.FC<EditViewProps<CourseClassExtended>> = ({
  twoColumn,
  dispatch,
  form,
  showConfirm,
  values
}) => {
  const portalDocsPlaceholders = useMemo(() => {
    switch (values.type) {
      default:
      case "Distant Learning":
      case "With Sessions":
        return [null, null];
      case "Hybrid":
        return [format(new Date(values.startDateTime), III_DD_MMM_YYYY_HH_MM), format(new Date(values.endDateTime), III_DD_MMM_YYYY_HH_MM)];
    }
  }, [values.type, values.startDateTime, values.endDateTime]);

  return <div className="pl-3 pr-3 pb-2">
    <Divider className='mb-2' />
    <FieldArray
      name="documents"
      label="Documents"
      entity="CourseClass"
      component={DocumentsRenderer}
      xsGrid={12}
      mdGrid={twoColumn ? 6 : 12}
      lgGrid={twoColumn ? 4 : 12}
      dispatch={dispatch}
      form={form}
      showConfirm={showConfirm}
      rerenderOnEveryChange
    />
    <Grid container rowSpacing={2} columnSpacing={3} className="mt-2">
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="dateTime"
          name="portalDocAccessStart"
          label="Portal access start"
          placeholder={portalDocsPlaceholders[0]}
        />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="dateTime"
          name="portalDocAccessEnd"
          label="Portal access end"
          placeholder={portalDocsPlaceholders[1]}
        />
      </Grid>
    </Grid>
    <Divider className="mt-2" />
  </div>;
};

export default CourseClassDocumentsTab;