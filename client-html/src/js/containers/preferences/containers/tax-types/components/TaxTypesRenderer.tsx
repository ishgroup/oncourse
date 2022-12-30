/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@mui/material/Card";
import Button from "@mui/material/Button";
import { FormControlLabel } from "@mui/material";
import Grid from "@mui/material/Grid";
import { Tax } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
import {
  formatFieldPercent,
  parseFieldPercent,
  preventNegativeOrLogEnter
} from "../../../../../common/utils/numbers/numbersNormalizing";

const validatePercentage = value => (!value && value !== 0
    ? "Field is mandatory"
    : value * 100 > 100
    ? "Tax rate cannot be greater than 100%"
    : undefined);

const renderTaxTypes = props => {
  const {
 fields, classes, onDelete, assetAccounts, liabilityAccounts
} = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: Tax, index) => {
        const field = fields.get(index);

        return (
          <Card className="card" key={index}>
            <Grid container columnSpacing={3} rowSpacing={2}  className="relative">
              <Grid item xs={4}>
                <FormField
                  type="text"
                  name={`${item}.code`}
                  label="Tax Code"
                  fullWidth
                  disabled={field.systemType || !field.editable}
                  className={classes.field}
                  required
                />
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="number"
                  name={`${item}.rate`}
                  label="Rate"
                  min="0"
                  max="100"
                  step="0.01"
                  format={formatFieldPercent}
                  parse={parseFieldPercent}
                  onKeyPress={preventNegativeOrLogEnter}
                  disabled={!field.editable}
                  className={classes.field}
                  validate={validatePercentage}
                  debounced={false}
                  fullWidth
                />
              </Grid>

              <Grid item xs={4}>
                <div className="d-flex">
                  <FormControlLabel
                    className={classes.checkbox}
                    control={(
                      <FormField
                        type="checkbox"
                        name={`${item}.gst`}
                        color="primary"
                        value="true"
                        disabled={!field.editable}
                        fullWidth
                      />
                    )}
                    label="GST"
                  />
                  <div className="flex-fill" />
                  {!field.systemType && (
                    <div>
                      <Button
                        size="small"
                        color="secondary"
                        className={classes.deleteButton}
                        onClick={() => onDelete(field, index)}
                      >
                        Delete
                      </Button>
                    </div>
                  )}
                </div>
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="select"
                  name={`${item}.payableAccountId`}
                  label="Payable Account"
                  items={assetAccounts}
                  className={classes.field}
                  disabled={!field.editable}
                  required
                  fullWidth
                />
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="select"
                  name={`${item}.receivableAccountId`}
                  label="Receivable Account"
                  items={liabilityAccounts}
                  className={classes.field}
                  disabled={!field.editable}
                  required
                  fullWidth
                />
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="text"
                  name={`${item}.description`}
                  label="Description"
                  className={classes.field}
                  disabled={!field.editable}
                  fullWidth
                />
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};

export default renderTaxTypes;
