/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FieldValidationType } from '@api/model';
import { TreeItem } from '@atlaskit/tree';
import { FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { SelectItemDefault } from 'ish-ui';
import * as React from 'react';
import { useMemo } from 'react';
import { change } from 'redux-form';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { useAppDispatch, useAppSelector } from '../../../../../common/utils/hooks';
import {
  CollectionFormField,
  CollectionFormItem
} from '../../../../../model/preferences/data-collection-forms/collectionFormSchema';
import { CustomField } from '../../../../entities/customFieldTypes/components/CustomFieldsTypes';
import { DATA_COLLECTION_FORM } from './DataCollectionForm';

interface Props {
  item: TreeItem;
  field: CollectionFormField;
  fields: CollectionFormItem[];
  formType: string;
}

const validationTypes = Object.keys(FieldValidationType).map<SelectItemDefault>(k => ({ value: k, label: k.toLowerCase().capitalize().replaceAll('_', ' ') }));

const CollectionFormField = ({
   item,
   field,
   fields,
   formType
}: Props) => {

  const dispatch = useAppDispatch();

  const customFieldTypes = useAppSelector(state => state.preferences.customFields);

  const customFieldType = field.relatedFieldKey && customFieldTypes.find(c => c.fieldKey === field.relatedFieldKey.replace(/customField.\w+./, ""));

  const onRelatedKeyChange = () => {
    dispatch(change(DATA_COLLECTION_FORM, `items[${item.id}].relatedFieldValue`, null));
  };

  const availableRelations = useMemo(() => {
    const result = [];

    for (const fieldItem of fields) {
      if (fieldItem.baseType === "field"
        && fieldItem.type.uniqueKey !== field.type.uniqueKey) {
        result.push({ value: fieldItem.type.uniqueKey, label: fieldItem.label });
      }
    }

    return result;
  }, [fields]);

  const relatedValueField = useMemo(() => {
    return customFieldType  ? <CustomField
        type={{ ...customFieldType, fieldKey: "relatedFieldValue", name: "Display condition value" }}
        value={field.relatedFieldValue}
        fieldName={`items[${item.id}]`}
        dispatch={dispatch}
        form={DATA_COLLECTION_FORM}
      />
      : <FormField
        type="text"
        name={`items[${item.id}].relatedFieldValue`}
        label={$t('display_condition_value')}
        disabled={!field.relatedFieldKey}
      />;
  }, [customFieldType, field.relatedFieldValue, field.relatedFieldKey, item.id]);

  return (
    <Grid container columnSpacing={3}>
      <Grid container size={4} rowSpacing={2}>
        <Grid size={12}>
          <FormField
            type="text"
            name={`items[${item.id}].label`}
            label={$t('label')}
            required
          />
        </Grid>
        <Grid size={12}>
          <FormField
            type="multilineText"
            name={`items[${item.id}].helpText`}
            label={$t('help_text')}
            className="mt-1"
            truncateLines={4}
          />
        </Grid>
      </Grid>

      <Grid container size={4} rowSpacing={2}>
        <Grid size={12}>
          <FormField
            type="select"
            items={availableRelations}
            name={`items[${item.id}].relatedFieldKey`}
            label={$t('display_condition_field')}
            onChange={onRelatedKeyChange}
            allowEmpty
          />
        </Grid>
        <Grid size={12}>
          {relatedValueField}
        </Grid>
      </Grid>

      <Grid container size={4} rowSpacing={2}>
        <Grid size={12}>
          <FormControlLabel
            className="switchWrapper w-100"
            control={<FormField type="switch" name={`items[${item.id}].mandatory`} />}
            label={$t('mandatory')}
            labelPlacement="start"
          />
        </Grid>
        {formType === 'Survey' && <Grid size={12}>
          <FormControlLabel
            className="switchWrapper w-100"
            control={<FormField type="switch" name={`items[${item.id}].isVisibleToTutor`} />}
            label={$t('visible_to_tutors')}
            labelPlacement="start"
          />
        </Grid>}
        {formType === 'Enrolment' && <Grid size={12}>
          <FormField
            type="select"
            items={validationTypes}
            name={`items[${item.id}].validationType`}
            label={$t('validate_against')}
            allowEmpty
          />
        </Grid>}
      </Grid>
    </Grid>
  );
};

export default CollectionFormField;