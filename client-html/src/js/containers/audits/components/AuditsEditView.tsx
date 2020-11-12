/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import FormField from "../../../common/components/form/form-fields/FormField";

const AuditsEditView = () => (
  <Grid container>
    <Grid item className="p-3">
      <FormField disabled type="dateTime" name="created" label="Date and time" fullWidth />
      <FormField disabled type="text" name="entityIdentifier" label="Entity name" fullWidth />
      <FormField disabled type="text" name="entityId" multiline label="Entity ID" fullWidth />
      <FormField disabled type="text" name="action" label="Action" fullWidth />
      <FormField disabled type="text" name="message" label="Message" fullWidth multiline />
    </Grid>
  </Grid>
);

export default AuditsEditView;
