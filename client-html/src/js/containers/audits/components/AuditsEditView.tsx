/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from "@mui/material/Grid";
import * as React from "react";
import FormField from "../../../common/components/form/formFields/FormField";

const AuditsEditView = () => (
  <Grid container columnSpacing={3} rowSpacing={2} className="p-3 overflow-hidden">
    <Grid item xs={12}>
      <FormField disabled type="dateTime" name="created" label="Date and time" />
    </Grid>
    <Grid item xs={12}>
      <FormField disabled type="text" name="entityIdentifier" label="Entity name" />
    </Grid>
    <Grid item xs={12}>
      <FormField disabled type="text" name="entityId" label="Entity ID" />
    </Grid>
    <Grid item xs={12}>
      <FormField disabled type="text" name="action" label="Action" />
    </Grid>
    <Grid item xs={12}>
      <FormField disabled type="text" name="message" label="Message" multiline />
    </Grid>
  </Grid>
);

export default AuditsEditView;
