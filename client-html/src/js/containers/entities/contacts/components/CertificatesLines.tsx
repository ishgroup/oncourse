/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormControlLabel } from "@mui/material";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import React from "react";
import FormField from "../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";

const CertificatesHeaderBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row } = props;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.qualificationName}
        </Typography>
      </div>
    </div>
  );
});

export const CertificatesHeaderLine = CertificatesHeaderBase;

export const CertificatesContentLine: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row, item } = props;

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={6}>
        <FormControlLabel
          className="checkbox pr-3"
          control={(
            <FormField
              type="checkbox"
              name={`${item}.fullQualification`}
              color="primary"
              stringValue
              disabled
            />
          )}
          label="Full qualification or skill set"
        />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.nationalCode} label="National code" />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.certificateNumber} label="Certificate #" />
      </Grid>
      <Grid item xs={6}>
        <FormField type="date" name={`${item}.createdOn`} label="Created" disabled />
      </Grid>
      <Grid item xs={6}>
        <FormField type="date" name={`${item}.lastPrintedOn`} label="Last printed" disabled />
      </Grid>
    </Grid>
  );
});
