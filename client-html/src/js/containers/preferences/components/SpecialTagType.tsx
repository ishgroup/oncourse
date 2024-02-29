/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Button, Card, FormControlLabel, Grid } from "@mui/material";
import { makeAppStyles, NumberArgFunction } from "ish-ui";
import React from "react";
import FormField from "../../../common/components/form/formFields/FormField";
import { cardsFormStyles } from "../styles/formCommonStyles";

interface SpecialTagTypeProps {
  index: number;
  onDelete: NumberArgFunction;
}

const useStyles = makeAppStyles(cardsFormStyles);

function SpecialTagType({ index, onDelete }: SpecialTagTypeProps) {
  const classes = useStyles();
  return (
    <Grid item xs={12}>
      <Card id={`special-tag-type-${index}`} className="card">
        <Grid container columnSpacing={3} spacing={2} className="relative">
          <Grid item xs={12}>
            <Grid container columnSpacing={3}>
              <Grid item xs={6}>
                <FormField
                  type="text"
                  name={`types.${index}.name`}
                  label="Name"
                  className={classes.field}
                  required
                />
              </Grid>

              <Grid item xs={6}>
                <div className="d-flex">
                  <FormControlLabel
                    className={classes.checkbox}
                    control={<FormField
                      type="switch"
                      name={`types.${index}.status`}
                      format={v => v === "Show on website"}
                      parse={v => (v ? "Show on website" : "Private")}
                      debounced={false}
                    />}
                    label="Is wisible on web"
                    labelPlacement="start"
                  />
                  <div>
                    <Button
                      size="small"
                      color="secondary"
                      onClick={() => onDelete(index)}
                      className={classes.deleteButton}
                    >
                      Delete
                    </Button>
                  </div>
                </div>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Card>
    </Grid>
  );
}

export default SpecialTagType;