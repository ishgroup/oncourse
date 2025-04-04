/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tax } from '@api/model';
import { Button, Card, FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { formatFieldPercent, parseFieldPercent, preventNegativeOrLogEnter } from 'ish-ui';
import * as React from 'react';
import FormField from '../../../../../common/components/form/formFields/FormField';

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
                  label={$t('tax_code')}
                  disabled={field.systemType || !field.editable}
                  className={classes.field}
                  required
                />
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="number"
                  name={`${item}.rate`}
                  label={$t('rate')}
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
                        disabled={!field.editable}
                                              />
                    )}
                    label={$t('gst')}
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
                        {$t('delete2')}
                      </Button>
                    </div>
                  )}
                </div>
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="select"
                  name={`${item}.payableAccountId`}
                  label={$t('payable_account')}
                  items={assetAccounts}
                  className={classes.field}
                  disabled={!field.editable}
                  required
                />
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="select"
                  name={`${item}.receivableAccountId`}
                  label={$t('receivable_account')}
                  items={liabilityAccounts}
                  className={classes.field}
                  disabled={!field.editable}
                  required
                />
              </Grid>

              <Grid item xs={4}>
                <FormField
                  type="text"
                  name={`${item}.description`}
                  label={$t('description')}
                  className={classes.field}
                  disabled={!field.editable}
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
