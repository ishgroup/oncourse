/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { format as formatDate } from "date-fns";
import React from "react";
import Grid from "@material-ui/core/Grid";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { III_DD_MMM_YYYY } from "../../../../../common/utils/dates/format";

const MembershipEditView: React.FC<any> = props => {
  const { values } = props;

  return values ? (
    <Grid container className="p-3">
      <Grid container>
        <Grid item sm={2}>
          <Uneditable label="SKU" value={values.code} />
        </Grid>
        <Grid item sm={8}>
          <Uneditable label="Sale price" value={values.totalFee} money />
        </Grid>
      </Grid>
      <Grid container>
        <Grid item sm={6}>
          <Uneditable label="Description" value={values.description} multiline />
        </Grid>
      </Grid>
      <Grid container>
        <Grid item sm={6}>
          {values.validTo && (
            <Uneditable label="Expires on" value={values.validTo} format={v => formatDate(new Date(v), III_DD_MMM_YYYY)} />
          )}
          {values.expireNever && (
            <Uneditable label="Expires on" value={values.expireNever} />
          )}
        </Grid>
      </Grid>
    </Grid>
  ) : null;
};

export default MembershipEditView;
