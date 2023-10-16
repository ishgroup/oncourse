/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { TreeItem } from "@atlaskit/tree";
import Delete from "@mui/icons-material/Delete";
import DragIndicator from "@mui/icons-material/DragIndicator";
import { Grid } from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import { useMemo } from "react";
import * as React from "react";
import { change, Field } from "redux-form";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { ToogleCheckbox } from "../../../../../common/components/form/ToogleCheckbox";
import { useAppDispatch } from "../../../../../common/utils/hooks";
import { DATA_COLLECTION_FORM } from "./DataCollectionForm";

interface Props {
  item: TreeItem;
  field: Field;
}

const CollectionFormField = ({
   item,
   field,
   fields
}) => {

  const dispatch = useAppDispatch();
  
  const onRelatedKeyChange = (e, newValue) => {
    if (!newValue) dispatch(change(DATA_COLLECTION_FORM, `items[${item.id}].relatedFIeldValue`, null));
  };

  const availableRelations = useMemo(() => {

  }, [fields])

  return (
    <Grid container columnSpacing={3}>
      <Grid item container xs={4} rowSpacing={2}>
        <Grid item xs={12}>
          <FormField
            type="text"
            name={`items[${item.id}].label`}
            label="Label"
            required
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="multilineText"
            name={`items[${item.id}].helpText`}
            label="Help Text"
            className="mt-1"
            truncateLines={4}
          />
        </Grid>
      </Grid>

      <Grid item container xs={4} rowSpacing={2}>
        <Grid item xs={12}>
          <FormField
            type="text"
            name={`items[${item.id}].relatedFieldKey`}
            label="Display condition field"
            onChange={onRelatedKeyChange}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            name={`items[${item.id}].relatedFIeldValue`}
            label="Display condition value"
            required={Boolean(field.relatedFieldKey)}
            disabled={!field.relatedFieldKey}
          />
        </Grid>
      </Grid>

      <Grid item container xs={4} display="flex" alignItems="center" justifyContent="center">
        <Field
          name={`items[${item.id}].mandatory`}
          label="Label"
          margin="none"
          type="checkbox"
          chackedLabel="Mandatory"
          uncheckedLabel="Optional"
          component={ToogleCheckbox}
        />
      </Grid>
    </Grid>
  );
};

export default CollectionFormField;