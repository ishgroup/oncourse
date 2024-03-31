/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as Entities from "@aql/queryLanguageModel";
import { CircularProgress, Grid, Typography } from "@mui/material";
import Collapse from "@mui/material/Collapse";
import { mapSelectItems } from "ish-ui";
import debounce from "lodash.debounce";
import React, { useCallback, useEffect, useState } from "react";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import EntityService from "../../../../../../common/services/EntityService";
import { getPluralSuffix } from "../../../../../../common/utils/strings";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../../../constants/Config";

const records = Object.keys(Entities)
  .filter(i => Entities[i].constructor.name !== "Enum")
  .map(mapSelectItems);

const QueryCardContent = props => {
  const {
    field, name, classes, disabled
  } = props;

  const [queryResultsPending, setQueryResultsPending] = useState(false);
  const [hideQueryResults, setQueryHideResults] = useState(false);
  const [queryResults, setQueryResults] = useState(0);
  const [isValidQuery, setIsValidQuery] = useState(true);

  const onValidateQuery = (isValid, input?) => {
    if (input && input.includes("${")) {
      setIsValidQuery(true);
      return;
    }
    setIsValidQuery(isValid);
  };

  const debounseSearch = useCallback<any>(
    debounce((isValid, entity, query, queryResultsPending) => {
      if (entity && isValid && !queryResultsPending) {
        setQueryResultsPending(true);
        EntityService.getPlainRecords(
          entity,
          "id",
          query
        )
        .then(res => {
          setQueryResults(res.rows.length);
          setQueryResultsPending(false);
          setQueryHideResults(false);
          onValidateQuery(true, query);
        })
        .catch(() => {
          setQueryHideResults(true);
          setQueryResultsPending(false);
          onValidateQuery(false, query);
        });
      }
    }, 600),
    []
  );

  useEffect(() => {
    onValidateQuery(true);
    debounseSearch(isValidQuery, field.entity, field.query, queryResultsPending);
  }, [field.query, field.entity]);

  const validateQueryClosureReturnValue = useCallback(value => {
    if (!value) return "";

    const matches = value.match(/[a-zA-Z\s]*/g);

    if (!matches || matches[0] !== value) return "You can only use letters";

    return "";
  }, [field]);

  const queryAvailable = Boolean(field.entity);

  const validateExpression = useCallback(() => (isValidQuery ? undefined : "Expression is invalid"), [isValidQuery]);

  return (
    <Grid container columnSpacing={3} rowSpacing={2}>
      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${name}.entity`}
          label="Entity"
          items={records}
          className="d-flex mt-2"
          disabled={disabled}
          required
        />
      </Grid>  

      <Grid item xs={12}>
        <Collapse
          in={queryAvailable}
          classes={{
            wrapper: field.entity ? "overflow-visible" : undefined
          }}
        >
          <div className={classes.queryField}>
            <FormField
              type="aql"
              name={`${name}.query`}
              label="Query"
              placeholder="All records"
              rootEntity={field.entity}
              disabled={!field.entity || disabled}
              validate={validateExpression}
            />
          </div>
        </Collapse>
      </Grid>

      <Grid item container xs={12} className="mb-2">
        <Grid item xs={6}>
          <FormField
            name={`${name}.queryClosureReturnValue`}
            type="text"
            label="Returned results name"
            validate={validateQueryClosureReturnValue}
          />
        </Grid>

        <Grid item xs={6} className="d-flex p-2" alignItems="flex-end">
          {queryResultsPending && !hideQueryResults && <CircularProgress size={24} thickness={4} />}
          {!queryResultsPending && !hideQueryResults && (
          <Typography variant="caption" color="textSecondary">
            {queryResults === PLAIN_LIST_MAX_PAGE_SIZE ? `more than ${queryResults}` : queryResults}
            {' '}
            record
            {getPluralSuffix(queryResults)}
            {' '}
            found
          </Typography>
            )}
        </Grid>
      </Grid>
    </Grid>
  );
};

export default QueryCardContent;
