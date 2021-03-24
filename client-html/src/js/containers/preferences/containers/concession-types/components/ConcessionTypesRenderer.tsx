/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@material-ui/core/Card";
import Button from "@material-ui/core/Button";
import { FormControlLabel } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { ConcessionType } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { validateSingleMandatoryField, validateUniqueNamesInArray } from "../../../../../common/utils/validation";

const renderConcessionTypes = props => {
  const { fields, classes, onDelete } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: ConcessionType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`concession-type-${index}`} className="card" key={index}>
            <Grid container spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container>
                  <Grid item xs={6}>
                    <FormField
                      type="text"
                      name={`${item}.name`}
                      label="Concession Type Name"
                      fullWidth
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
                            value="true"
                            fullWidth
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
                          value="true"
                          fullWidth
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
                          value="true"
                          fullWidth
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
