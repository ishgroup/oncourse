/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AvetmissExportFlavour, FundingSource } from '@api/model';
import { Button, Grid } from '@mui/material';
import { red } from '@mui/material/colors';
import FormControlLabel from '@mui/material/FormControlLabel';
import Paper from '@mui/material/Paper';
import clsx from 'clsx';
import { mapSelectItems, sortDefaultSelectItems } from 'ish-ui';
import * as React from 'react';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../../common/components/form/formFields/FormField';

const Flavours = Object.keys(AvetmissExportFlavour)
  .filter(val => isNaN(Number(val)))
  .map(mapSelectItems);

Flavours.sort(sortDefaultSelectItems);

const styles = () =>
  ({
    deleteButton: {
      color: red[500]
    },
    inputWidth: {
      minWidth: "185.05px"
    }
  });

const FundingContractItem = props => {
  const {
    fields, onDelete, classes
  } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: FundingSource, index) => {
        const field = fields.get(index);

        return (
          <Paper id={`funding-contracts-item-${index}`} className="card" key={index}>
            <Grid container columnSpacing={3} rowSpacing={2} className="container">
              <Grid item container columnSpacing={3} rowSpacing={2} xs={12}>
                <Grid item xs={5}>
                  <FormField
                    type="text"
                    name={`${item}.name`}
                    label="Name"
                    className={classes.inputWidth}
                                        required
                  />
                </Grid>
                <Grid item xs={7} className="d-flex-start justify-content-end">
                  <Button
                    className={clsx("errorColor", classes.deleteButton)}
                    onClick={() => onDelete(field, index)}
                  >
                    Delete
                  </Button>
                </Grid>
              </Grid>
              <Grid item xs={5}>
                <FormField
                  type="select"
                  name={`${item}.flavour`}
                  label="Flavour"
                  items={Flavours}
                  className="pr-3"
                                    required
                />
              </Grid>
              <Grid item xs={5}>
                <FormControlLabel
                  control={<FormField type="checkbox" name={`${item}.active`} color="primary" />}
                  label="Active"
                />
              </Grid>
            </Grid>
          </Paper>
        );
      })}
    </Grid>
  );
};

export default withStyles(FundingContractItem, styles) as any;
