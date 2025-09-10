/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PaymentType, PayType } from '@api/model';
import { Button, Card, FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { mapSelectItems, sortDefaultSelectItems } from 'ish-ui';
import * as React from 'react';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { validateSingleMandatoryField, validateUniqueNamesInArray } from '../../../../../common/utils/validation';

const PayTypes = Object.keys(PayType)
  .filter(val => Number.isNaN(Number(val)))
  .map(mapSelectItems);

PayTypes.sort(sortDefaultSelectItems);

const renderPaymentTypes = props => {
  const {
    fields, classes, onDelete, assetAccounts
  } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: PaymentType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`payment-type-item-${index}`} className="card" key={index}>
            <Grid container spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container columnSpacing={3} rowSpacing={2}>
                  <Grid item xs={10} md={11}>
                    <FormField
                      type="text"
                      name={`${item}.name`}
                      label={$t('name')}
                      disabled={field.systemType}
                      validate={[validateSingleMandatoryField, validateUniqueNamesInArray]}
                    />
                  </Grid>

                  <Grid item xs={2} md={1}>
                    {!field.systemType && (
                      <div className="d-flex">
                        <div className="flex-fill" />
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
                  </Grid>

                  <Grid item xs={4} className={classes.checkbox}>
                    <FormControlLabel
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.active`}
                          color="primary"
                          disabled={field.systemType}
                                                  />
                      )}
                      label={$t('active')}
                    />
                  </Grid>

                  <Grid item xs={4} className={classes.checkbox}>
                    <FormControlLabel
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.bankedAuto`}
                          color="primary"
                          disabled={field.systemType}
                                                  />
                      )}
                      label={$t('banked_automatically')}
                    />
                  </Grid>

                  <Grid item xs={4} className={classes.checkbox}>
                    <FormControlLabel
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.reconcilable`}
                          color="primary"
                          disabled={field.systemType}
                                                  />
                      )}
                      label={$t('reconcilable')}
                    />
                  </Grid>

                  {!field.systemType && (
                    <>
                      <Grid item xs={4}>
                        <FormField
                          type="select"
                          name={`${item}.type`}
                          label={$t('type')}
                          items={PayTypes}
                          className={classes.field}
                          required
                        />
                      </Grid>

                      <Grid item xs={4}>
                        <FormField
                          type="select"
                          name={`${item}.undepositAccountId`}
                          label={$t('undeposited_funds_account')}
                          items={assetAccounts}
                          className={classes.field}
                          required
                        />
                      </Grid>

                      <Grid item xs={4}>
                        <FormField
                          type="select"
                          name={`${item}.accountId`}
                          label={$t('account')}
                          items={assetAccounts}
                          className={classes.field}
                          required
                        />
                      </Grid>
                    </>
                  )}
                </Grid>
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};

export default renderPaymentTypes;
