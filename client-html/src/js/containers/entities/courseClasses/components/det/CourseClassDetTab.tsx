/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Grid from "@material-ui/core/Grid/Grid";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";

const CourseClassDetTab: React.FC<EditViewProps<CourseClassExtended>> = () => (
  <>
    <div className="pl-3 pr-3 centeredFlex">
      <div className="heading mt-2 mb-2">DET export</div>
    </div>
    <Grid container className="pl-3 pr-3">
      <Grid item xs={12}>
        <FormField type="text" name="initialDetExport" label="Initial DET export" />
      </Grid>
      <Grid item xs={12}>
        <FormField type="text" name="midwayDetExport" label="Midway DET export" />
      </Grid>
      <Grid item xs={12}>
        <FormField type="text" name="finalDetExport" label="Final DET export" />
      </Grid>
    </Grid>
  </>
  );

export default CourseClassDetTab;
