/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Typography from "@mui/material/Typography";
import clsx from "clsx";
import { AddButton } from "ish-ui";
import React, { useMemo } from "react";
import { FieldArray } from "redux-form";
import MinifiedEntitiesRenderer from "./MinifiedEntitiesRenderer";

const MinifiedEntitiesList = props => {
  const {
    entity,
    name,
    header,
    onAdd,
    onDelete,
    onViewMore,
    FieldsContent,
    HeaderContent,
    count,
    oneItemHeader,
    accordion,
    validate,
    syncErrors,
    namePath,
    twoColumn,
    fieldProps
  } = props;

  const error = useMemo(
    () =>
      syncErrors
      && syncErrors[name]
      && syncErrors[name]._error && (
        <Typography color="error" variant="body2" className="text-pre-wrap" component="p">
          {syncErrors[name]._error}
        </Typography>
      ),
    [syncErrors, name]
  );

  return (
    <div id={name}>
      <div className="centeredFlex">
        <Typography className={clsx("heading mb-2 mt-2", {"errorColor": error})}>
          {count > 0 ? count : ""}
          {' '}
          {count !== 1 ? header : oneItemHeader}
        </Typography>
        {onAdd && (
          <AddButton onClick={onAdd}/>
        )}
      </div>

      <div className="shakingError">
        {error}
      </div>

      <FieldArray
        entity={entity}
        name={name}
        namePath={namePath}
        component={MinifiedEntitiesRenderer}
        onDelete={onDelete}
        FieldsContent={FieldsContent}
        HeaderContent={HeaderContent}
        onViewMore={onViewMore}
        accordion={accordion}
        validate={validate}
        twoColumn={twoColumn}
        syncErrors={syncErrors}
        fieldProps={fieldProps}
        rerenderOnEveryChange
      />
    </div>
  );
};

export default MinifiedEntitiesList;
