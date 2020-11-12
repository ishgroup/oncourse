/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid/Grid";
import FormField from "../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../common/components/form/Uneditable";

const MembershipHeaderBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row } = props;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.name}
        </Typography>
      </div>
    </div>
  );
});

export const MembershipHeader = MembershipHeaderBase;

export const MembershipContent: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row, item } = props;

  return (
    <Grid container>
      <Grid item xs={12}>
        <Uneditable value={row.name} label="Name" url={`/sale/${row.id}`} />
      </Grid>
      <Grid item xs={12}>
        <FormField type="date" name={`${item}.expiryDate`} label="Expires" disabled />
      </Grid>
    </Grid>
  );
});
