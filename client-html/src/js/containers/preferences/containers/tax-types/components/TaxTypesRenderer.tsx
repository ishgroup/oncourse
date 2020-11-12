/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@material-ui/core/Card";
import Button from "@material-ui/core/Button";
import { FormControlLabel } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { Tax } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
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
            <Grid container spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container>
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
                      type="persent"
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
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};

export default renderTaxTypes;
