/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ConcessionType } from '@api/model';
import { Button, Card, FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import * as React from 'react';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { validateSingleMandatoryField, validateUniqueNamesInArray } from '../../../../../common/utils/validation';

const renderConcessionTypes = props => {
  const { fields, classes, onDelete } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: ConcessionType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`concession-type-${index}`} className="card" key={index}>
            <Grid container columnSpacing={3} spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container columnSpacing={3}>
                  <Grid item xs={6}>
                    <FormField
                      type="text"
                      name={`${item}.name`}
                      label={$t('concession_type_name')}
                                            className={classes.field}
                      validate={[validateSingleMandatoryField, validateUniqueNamesInArray]}
                    />
                  </Grid>

                  <Grid item xs={6}>
                    <div className="d-flex">
                      <FormControlLabel
                        className={classes.checkbox}
                        control={(
                          <FormField
                            type="checkbox"
                            name={`${item}.allowOnWeb`}
                            color="primary"
                          />
                        )}
                        label={$t('allow_students_to_selfselect_on_website')}
                      />
                      <div className="flex-fill" />
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
                    </div>
                  </Grid>

                  <Grid item xs={6}>
                    <FormControlLabel
                      className={classes.checkbox}
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.requireExpary`}
                          color="primary"
                        />
                      )}
                      label={$t('require_expiry_date')}
                    />
                  </Grid>

                  <Grid item xs={6}>
                    <FormControlLabel
                      className={classes.checkbox}
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.requireNumber`}
                          color="primary"
                        />
                      )}
                      label={$t('require_concession_type_number')}
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

export default renderConcessionTypes;
