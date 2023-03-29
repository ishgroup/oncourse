/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@mui/material/Card";
import clsx from "clsx";
import Button from "@mui/material/Button";
import { FormControlLabel } from "@mui/material";
import Grid from "@mui/material/Grid";
import { ContactRelationType } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";

const renderContactRelationTypes = props => {
  const { fields, classes, onDelete } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: ContactRelationType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`contact-relation-type-${index}`} className="card" key={index}>
            <Grid container columnSpacing={3} spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container columnSpacing={3}>
                  <Grid item xs={6}>
                    <FormField
                      type="text"
                      name={`${item}.relationName`}
                      label="Relationship Name"
                                            className={classes.field}
                      disabled={field.systemType}
                      required
                    />
                  </Grid>

                  <Grid item xs={6}>
                    <div className="d-flex">
                      <FormField
                        type="text"
                        name={`${item}.reverseRelationName`}
                        label="Reverse relationship Name"
                                                className={clsx(classes.field, "flex-fill")}
                        disabled={field.systemType}
                        required
                      />
                      <div>
                        {!field.systemType && (
                          <Button
                            size="small"
                            color="secondary"
                            className={classes.deleteButton}
                            onClick={() => onDelete(field, index)}
                          >
                            Delete
                          </Button>
                        )}
                      </div>
                    </div>
                  </Grid>

                  <Grid item xs={12}>
                    <FormControlLabel
                      className={classes.checkbox}
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.portalAccess`}
                          color="primary"
                        />
                      )}
                      label={`Allow ${field.relationName} to access all information in ${field.reverseRelationName} skillsOnCourse portal`}
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

export default renderContactRelationTypes;
