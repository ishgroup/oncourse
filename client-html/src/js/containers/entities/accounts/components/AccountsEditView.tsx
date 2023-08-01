/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tax } from "@api/model";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import * as React from "react";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { State } from "../../../../reducers/state";

const formattedAccountTypes: any[] = [
  {
    value: "asset",
    label: "asset"
  },
  {
    value: "COS",
    label: "COS"
  },
  {
    value: "liability",
    label: "liability"
  },
  {
    value: "expense",
    label: "expense"
  },
  {
    value: "equity",
    label: "equity"
  },
  {
    value: "income",
    label: "income"
  }
];

const getFormattedTaxes = (taxes: Tax[]) =>
  taxes.map(tax => ({
    value: tax.id,
    label: `${tax.code}`
  }));

const AccountsEditView = props => {
  const {
   twoColumn, taxTypes, isNew, values, syncErrors
  } = props;
  const isCustom = values && values.isCustom === true;
  const isDisabled = isNew ? false : !isCustom;

  let canDisable = false;

  let isIncomeType = false;

  if (values !== null && values !== undefined) {
    isIncomeType = values.type === "income";
    canDisable = values.isDefaultAccount;
  }

  return (
    <Grid container item columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12} className="p-3">
      <Grid item xs={12}>
        <FullScreenStickyHeader
          twoColumn={twoColumn}
          title={values && values.accountCode}
          opened={isNew || Object.keys(syncErrors).includes("accountCode")}
          fields={(
            <Grid item xs={12}>
              <FormField
                type="text"
                name="accountCode"
                label="Code"
                required
              />
            </Grid>
          )}
        />
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="select"
          disabled={isDisabled}
          name="type"
          label="Type"
          items={formattedAccountTypes}
          required
        />
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="text"
          name="description"
          label="Description"
          required={isNew || isCustom}
          multiline
        />
      </Grid>
      <Grid item xs={12}>
        <FormControlLabel
          className="checkbox pr-3"
          control={
            <FormField type="checkbox" disabled={canDisable} name="isEnabled" color="secondary" />
          }
          label="Enabled"
        />
      </Grid>
      <Grid item xs={12}>
        {isIncomeType ? (
          <FormField
            type="select"
            name="tax.id"
            label="Tax type"
            required={isNew || isCustom}
            items={getFormattedTaxes(taxTypes) || []}
          />
        ) : null}
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  taxTypes: state.preferences.taxTypes
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(AccountsEditView);
