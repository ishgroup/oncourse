/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { TreeItem } from "@atlaskit/tree";
import { Grid } from "@mui/material";
import { useMemo } from "react";
import * as React from "react";
import { change, Field } from "redux-form";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { ToogleCheckbox } from "../../../../../common/components/form/ToogleCheckbox";
import { useAppDispatch } from "../../../../../common/utils/hooks";
import {
  CollectionFormField,
  CollectionFormItem
} from "../../../../../model/preferences/data-collection-forms/collectionFormSchema";
import { DATA_COLLECTION_FORM } from "./DataCollectionForm";

interface Props {
  item: TreeItem;
  field: CollectionFormField;
  fields: CollectionFormItem[];
}

const CollectionFormField = ({
   item,
   field,
   fields
}: Props) => {

  const dispatch = useAppDispatch();
  
  const onRelatedKeyChange = (e, newValue) => {
    if (!newValue) dispatch(change(DATA_COLLECTION_FORM, `items[${item.id}].relatedFieldValue`, null));
  };

  const availableRelations = useMemo(() => {
    const result = [];

    // for (const fieldItem of fields) {
    //   if (fieldItem.type)
    // }

    return result;
  }, [fields]);

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
            name={`items[${item.id}].relatedFieldValue`}
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