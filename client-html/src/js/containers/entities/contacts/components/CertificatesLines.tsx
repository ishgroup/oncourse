/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid/Grid";
import { FormControlLabel } from "@material-ui/core";
import FormField from "../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../common/components/form/Uneditable";

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
    <Grid container>
      <Grid item xs={6}>
        <FormControlLabel
          className="checkbox pr-3"
          control={(
            <FormField
              type="checkbox"
              name={`${item}.fullQualification`}
              color="primary"
              fullWidth
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
