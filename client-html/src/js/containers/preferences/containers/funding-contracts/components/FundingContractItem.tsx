/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import Paper from "@mui/material/Paper";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import Button from "@mui/material/Button";
import { red } from "@mui/material/colors";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import { AvetmissExportFlavour, FundingSource } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
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
                    fullWidth
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
                  fullWidth
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

export default withStyles(styles)(FundingContractItem) as any;
