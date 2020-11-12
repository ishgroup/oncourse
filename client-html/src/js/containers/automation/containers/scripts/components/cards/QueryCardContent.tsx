/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import Grid from "@material-ui/core/Grid";
import Collapse from "@material-ui/core/Collapse";
import * as Entities from "@aql/queryLanguageModel";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { mapSelectItems } from "../../../../../../common/utils/common";

const records = Object.keys(Entities)
  .filter(i => Entities[i].constructor.name !== "Enum")
  .map(mapSelectItems);

const QueryCardContent = props => {
  const {
    field, name, classes, onValidateQuery, isValidQuery
  } = props;

  const queryAvailable = Boolean(field.entity);

  const validateExpression = useCallback(() => (isValidQuery ? undefined : "Expression is invalid"), [isValidQuery]);

  return (
    <Grid container>
      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${name}.entity`}
          label="Entity"
          items={records}
          className="d-flex mt-2"
          required
        />

        <Grid item xs={12}>
          <Collapse
            in={queryAvailable}
            classes={{
              container: field.entity ? "overflow-visible" : undefined
            }}
          >
            <div className={classes.queryField}>
              <FormField
                type="aql"
                name={`${name}.query`}
                label="Query"
                rootEntity={field.entity}
                disabled={!field.entity}
                onValidateQuery={onValidateQuery}
                validate={validateExpression}
                required
              />
            </div>
          </Collapse>
        </Grid>

        <Grid item xs={12}>
          <FormField
            type="text"
            name={`${name}.queryClosureReturnValue`}
            label="Returned Results Name"
            disabled
            required
          />
        </Grid>
      </Grid>
    </Grid>
  );
};

export default QueryCardContent;
