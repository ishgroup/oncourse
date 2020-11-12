/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { FieldArray } from "redux-form";
import Typography from "@material-ui/core/Typography";
import AddCircle from "@material-ui/icons/AddCircle";
import IconButton from "@material-ui/core/IconButton";
import clsx from "clsx";
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
    twoColumn
  } = props;

  const error = useMemo(
    () =>
      syncErrors
      && syncErrors[name]
      && syncErrors[name]._error && (
        <Typography color="error" variant="body2" className="text-pre-wrap" paragraph>
          {syncErrors[name]._error}
        </Typography>
      ),
    [syncErrors, name]
  );

  return (
    <div id={name}>
      <div className="centeredFlex">
        <Typography className={clsx("heading pt-2 pb-2", { "errorColor": error })}>
          {count > 0 ? count : ""}
          {' '}
          {count !== 1 ? header : oneItemHeader}
        </Typography>

        {onAdd && (
          <IconButton onClick={onAdd}>
            <AddCircle className="addButtonColor" />
          </IconButton>
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
        rerenderOnEveryChange
      />
    </div>
  );
};

export default MinifiedEntitiesList;
