/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import Paper from "@material-ui/core/Paper";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import Button from "@material-ui/core/Button";
import red from "@material-ui/core/colors/red";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { Typography } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { AvetmissExportFlavour, FundingSource } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { mapSelectItems, sortDefaultSelectItems } from "../../../../../common/utils/common";

const Flavours = Object.keys(AvetmissExportFlavour)
  .filter(val => isNaN(Number(val)))
  .map(mapSelectItems);

Flavours.sort(sortDefaultSelectItems);

const styles = () =>
  createStyles({
    deleteButton: {
      color: red[500]
    },
    inputWidth: {
      minWidth: "185.05px"
    }
  });

const FundingContractItem = props => {
  const {
    fields, onDelete, classes, hasLicence
  } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: FundingSource, index) => {
        const field = fields.get(index);

        return (
          <Paper id={`funding-contracts-item-${index}`} className="card" key={index}>
            <Grid className="container">
              <Grid item container xs={12}>
                <Grid item xs={5}>
                  <FormField
                    type="text"
                    name={`${item}.name`}
                    label="Name"
                    className={classes.inputWidth}
                    disabled={!hasLicence}
                    fullWidth
                    required
                  />
                </Grid>
                <Grid item xs={7} className="d-flex-start justify-content-end">
                  <Button
                    className={clsx("errorColor", classes.deleteButton)}
                    onClick={() => onDelete(field, index)}
                    disabled={!hasLicence}
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
                  disabled={!hasLicence}
                  fullWidth
                  required
                />
              </Grid>
              <Grid item xs={5}>
                <FormControlLabel
                  className="checkbox p-3"
                  control={<FormField type="checkbox" name={`${item}.active`} color="primary" />}
                  label="Active"
                />
              </Grid>
            </Grid>
            {!hasLicence && (
              <Typography color="error">Your license does not allow you to edit funding contracts</Typography>
            )}
          </Paper>
        );
      })}
    </Grid>
  );
};

export default withStyles(styles)(FundingContractItem) as any;
