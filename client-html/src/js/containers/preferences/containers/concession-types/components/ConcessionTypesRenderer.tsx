/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@mui/material/Card";
import Button from "@mui/material/Button";
import { FormControlLabel } from "@mui/material";
import Grid from "@mui/material/Grid";
import { ConcessionType } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { validateSingleMandatoryField, validateUniqueNamesInArray } from "../../../../../common/utils/validation";

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
                      label="Concession Type Name"
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
                        label="Allow students to self-select on website"
                      />
                      <div className="flex-fill" />
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
                      label="Require expiry date"
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
                      label="Require concession type number"
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
