/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@mui/material/Grid";
import FormControlLabel from "@mui/material/FormControlLabel";
import { Tax } from "@api/model";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

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
   twoColumn, taxTypes, isNew, values
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
    <Grid container columnSpacing={3} className="p-3">
      <Grid item lg={twoColumn ? 11 : 11} md={twoColumn ? 11 : 11} xs={11}>
        <Grid container columnSpacing={3}>
          <Grid item xs={twoColumn ? 6 : 12}>
            <FullScreenStickyHeader
              twoColumn={twoColumn}
              title={values && values.accountCode}
              fields={(
                <FormField
                  type="text"
                  name="accountCode"
                  label="Code"
                  required
                />
              )}
            />
            <FormField
              type="select"
              disabled={isDisabled}
              name="type"
              label="Type"
              items={formattedAccountTypes}
              className="mb-2"
              required
            />
            <FormField
              type="text"
              name="description"
              label="Description"
              required={isNew || isCustom}
              className="mb-2"
              multiline
            />
            <FormControlLabel
              className="checkbox pr-3 mb-2"
              control={
                <FormField type="checkbox" disabled={canDisable} name="isEnabled" color="secondary" />
              }
              label="Enabled"
            />
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
