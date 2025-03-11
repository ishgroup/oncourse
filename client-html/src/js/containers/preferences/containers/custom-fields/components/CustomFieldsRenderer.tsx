/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CustomFieldType, DataType, EntityType } from '@api/model';
import DeleteIcon from '@mui/icons-material/Delete';
import DragIndicator from '@mui/icons-material/DragIndicator';
import { Card, FormControlLabel, Grid } from '@mui/material';
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import clsx from 'clsx';
import {
  CheckboxField,
  EditInPlaceDateTimeField,
  EditInPlaceField,
  EditInPlaceMoneyField,
  mapSelectItems,
  SelectItemDefault,
  sortDefaultSelectItems,
  StyledCheckbox
} from 'ish-ui';
import React, { useMemo, useState } from 'react';
import { DragDropContext, Draggable, Droppable } from 'react-beautiful-dnd-next';
import { change, Field } from 'redux-form';
import FormField from '../../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../../common/components/form/formFields/Uneditable';
import ExpandableItem from '../../../../../common/components/layout/expandable/ExpandableItem';
import { reorder } from '../../../../../common/utils/DnD';
import { useAppSelector } from '../../../../../common/utils/hooks';
import {
  validateEmail,
  validateRegex,
  validateSingleMandatoryField,
  validateUniqueNamesInArray,
  validateURL
} from '../../../../../common/utils/validation';
import ListMapRenderer from './ListMapRenderer';

const mapEntityType = (entityType: EntityType) => {
  switch (entityType) {
    case "Article":
      return "Sale (Product)";
    case "Voucher":
      return "Sale (Voucher)";
    case "Membership":
      return "Sale (Membership)";
    case "ArticleProduct":
      return "Product";
    case "VoucherProduct":
      return "Voucher type";
    case "MembershipProduct":
      return "Membership type";
    default:
      return entityType;
  }
};

const entityTypeCondition = (item: SelectItemDefault) => mapEntityType(item.label as any);

const EntityTypes = Object.keys(EntityType)
  .filter(val => Number.isNaN(Number(val)))
  .map(mapSelectItems);

EntityTypes.sort(sortDefaultSelectItems);

const DataTypes = Object.keys(DataType)
  .filter(val => !['Record', 'Message template'].includes(val))
  .map(mapSelectItems);

DataTypes.sort(sortDefaultSelectItems);

const preventStarEnter = e => {
  if (e.key.match(/\*/)) {
    e.preventDefault();
  }
};

const onDragEnd = (result, items, dispatch) => {
  // dropped outside the list
  if (!result.destination) {
    return;
  }

  // dropped on the same position
  if (result.source.index === result.destination.index) {
    return;
  }

  const reordered = reorder(items, result.source.index, result.destination.index);

  dispatch(change("CustomFieldsForm", "types", reordered));

  setTimeout(
    () =>
      reordered.forEach((item: CustomFieldType, index: number) => {
        if (item.sortOrder !== index) {
          dispatch(change("CustomFieldsForm", `types[${index}].sortOrder`, index));
        }
      }),
    500
  );
};

const validateListMap = (value, dataType) => {
  let error;
  let fields = [];

  try {
    fields = JSON.parse(value);
  } catch (e) {
    console.error(e);
  }

  if (Array.isArray(fields)) {
    fields.forEach(f => {
      if (!f.value || (dataType === "Map" && !f.value.includes("*") && !f.label)) {
        error = "Options fields are invalid";
      }
    });
  } else {
    return "At least one option is required";
  }

  return error;
};

const CustomFieldsResolver = React.memo<{ field: CustomFieldType & { uniqid: string }, classes: any }>(
  ({ classes, field, ...props }) => {

    const currencySymbol = useAppSelector(state => state.currency?.shortCurrencySymbol);

    switch (field.dataType) {
      case "Checkbox":
        return (
          <FormControlLabel
            className={clsx(classes.checkbox)}
            control={<CheckboxField {...props} stringValue color="primary" className={undefined} />}
            label={$t('checked_by_default')}
          />
        );
      case "Date":
        return <EditInPlaceDateTimeField {...props} type="date" />;
      case "Date time":
        return <EditInPlaceDateTimeField {...props} type="datetime" />;
      case "Email":
        return <EditInPlaceField {...props} />;
      case "Long text":
        return <EditInPlaceField {...props} multiline />;
      case "List":
        return (
          <ListMapRenderer
            {...props as any}
            dataType={field.dataType}
            key={field.id || field.uniqid}
            label={$t('options')}
            onKeyPress={preventStarEnter}
          />
      );
      case "Map":
        return <ListMapRenderer {...props as any} dataType={field.dataType} key={field.id || field.uniqid} label={$t('options')} />;
      case "Money":
        return <EditInPlaceMoneyField {...props} currencySymbol={currencySymbol} />;
      case "URL":
      case "Text":
      default:
        return <EditInPlaceField {...props} />;
    }
}
);

const validateResolver = (value, allValues, props, name) => {
  const index = name.match(/\[(\d)]/);
  const root = index && allValues.types[Number(index[1])] as CustomFieldType;

  if (!root) {
    return undefined;
  }

  switch (root.dataType) {
    case "Email":
      return validateEmail(value);
    case "URL":
      return validateURL(value);
    case "List":
    case "Map":
      return validateListMap(value, root.dataType);
  }

  return undefined;
};

const ExpandableCustomFields = React.memo<{
  item,
  classes,
  field,
  onDataTypeChange,
  onDelete,
  index,
  onAddOther,
  isListOrMap,
  expanded,
  onChange,
  form,
  dispatch
}>(props => {
  const {
    item,
    classes,
    field,
    onDataTypeChange,
    onDelete,
    index,
    onAddOther,
    isListOrMap,
    expanded,
    onChange,
    form,
    dispatch
  } = props;

  const isExpanded = useMemo(() => ((expanded !== null && [index].includes(expanded)) || !field.id || !field.name), [expanded, field]);

  const onEntityChange = () => {
    dispatch(change(form, `${item}.dataType`, null));
  };

  const onTypeChange = type => {
    if (type === 'Portal subdomain' && field.entityType !== 'Contact') {
      dispatch(change(form, `${item}.entityType`, 'Contact'));
    }
    onDataTypeChange(type);
  };

  const availableDataTypes = useMemo(() => {
    if (field.entityType === "WaitingList") {
      return DataTypes.filter(t => t.label !== 'File');
    }
    return DataTypes;
  }, [field.entityType]);
  
  const availableEntities = useMemo(() => {
    if (field.dataType === 'Portal subdomain') {
      return EntityTypes.filter(t => t.value === 'Contact');
    }
    return EntityTypes;
  }, [field.dataType]);

  return (
    <ExpandableItem
      expanded={isExpanded}
      onChange={onChange}
      classes={{
        expandIcon: clsx("zIndex2", classes.expandIcon),
        expansionPanelRoot: classes.expansionPanelRoot,
        expansionPanelDetails: classes.expansionPanelDetails,
        collapseRoot: "w-100",
      }}
      elevation={0}
      expandButtonId={`custom-field-${index}`}
      collapsedContent={(
        <Grid container columnSpacing={3} className="relative align-items-center">
          <Grid item xs={4}>
            <Uneditable
              value={field.name}
              label={$t('name')}
            />
          </Grid>
          <Grid item xs={4}>
            <Uneditable
              value={mapEntityType(field.entityType)}
              label={$t('record_type')}
            />
          </Grid>
          <Grid item xs={4}>
            <Uneditable
              value={field.dataType}
              label={$t('data_type')}
            />
          </Grid>
        </Grid>
      )}
      buttonsContent={(
        <div className="d-flex align-items-baseline zIndex2 relative">
          <div className="centeredFlex">
            <IconButton onClick={() => onDelete(field, index)} size="small" className={classes.deleteButtonCustom}>
              <DeleteIcon fontSize="inherit" />
            </IconButton>
          </div>
        </div>
      )}
      detailsContent={(
        <Grid container columnSpacing={3} rowSpacing={3} className="relative">
          <Grid item xs={4}>
            <FormField
              type="text"
              name={`${item}.name`}
              label={$t('name')}
                            className={classes.field}
              validate={[validateSingleMandatoryField, validateUniqueNamesInArray]}
            />
          </Grid>

          <Grid item xs={4}>
            <FormField
              type="text"
              name={`${item}.fieldKey`}
              label={$t('custom_field_key')}
              disabled={!!field.id}
              className={classes.field}
              required
            />
          </Grid>

          <Grid item xs={4}>
            <FormField
              type="select"
              name={`${item}.dataType`}
              label={$t('data_type')}
              items={availableDataTypes}
              disabled={!!field.id}
              debounced={false}
              className={classes.field}
              onChange={onTypeChange}
              required
            />
          </Grid>

          <Grid item xs={4}>
            <FormField
              type="select"
              name={`${item}.entityType`}
              selectLabelCondition={entityTypeCondition}
              label={$t('record_type')}
              items={availableEntities}
              disabled={!!field.id}
              className={classes.field}
              onChange={onEntityChange}
              sort
              required
            />
          </Grid>

          <Grid item xs={4}>
            <FormControlLabel
              className={classes.checkbox}
              control={(
                <FormField
                  type="checkbox"
                  name={`${item}.mandatory`}
                  color="primary"
                />
              )}
              label={$t('mandatory')}
            />

            {field.dataType === "List" && (
              <FormControlLabel
                className={classes.checkbox}
                control={(
                  <StyledCheckbox
                    checked={field.defaultValue && field.defaultValue.includes("*")}
                    onChange={(e, checked) => onAddOther(index, checked)}
                    color="primary"
                  />
                )}
                label={$t('add_other_option')}
              />
            )}
          </Grid>

          <Grid item xs={4}>
            <Collapse in={isListOrMap} mountOnEnter unmountOnExit>
              <Field
                name={`${item}.defaultValue`}
                label={$t('default_value')}
                field={field}
                component={CustomFieldsResolver}
                className={classes.field}
                classes={classes}
                validate={validateResolver}
              />
            </Collapse>
            <Collapse in={field.dataType === "Pattern text"} mountOnEnter unmountOnExit>
              <FormField
                type="text"
                name={`${item}.pattern`}
                label={$t('pattern')}
                disabled={!!field.id}
                className={classes.field}
                validate={validateRegex}
                required
              />
            </Collapse>
          </Grid>
        </Grid>
      )}
    />
  );
});

const renderCustomFields = React.memo<any>(props => {
  const {
    fields, classes, onDelete, dispatch, meta: { form }
  } = props;

  const [expanded, setExpanded] = useState<number>(null);

  const onAddOther = (index, checked) => {
    const field = fields.get(index);
    const value = field.defaultValue ? JSON.parse(field.defaultValue) : [];

    if (checked) {
      value.push({ value: "*" });
    } else {
      const otherIndex = value.findIndex(v => v.value === "*");
      if (otherIndex !== -1) {
        value.splice(otherIndex, 1);
      }
    }
    dispatch(change(form, `${fields.name}[${index}].defaultValue`, value.length ? JSON.stringify(value) : null));
  };

  return (
    <DragDropContext onDragEnd={result => onDragEnd(result, fields.getAll(), dispatch)}>
      <Droppable droppableId="droppableCustomFields">
        {provided => (
          <div ref={provided.innerRef} className={classes.container}>
            {fields.map((item: string, index) => {
              const field: CustomFieldType = fields.get(index);

              const isListOrMap = ["List", "Map"].includes(field.dataType);

              const onDataTypeChange = () => {
                dispatch(change(form, `${item}.defaultValue`, null));
              };

              return (
                <Draggable key={index} draggableId={String(index + 1)} index={index}>
                  {provided => (
                    <div
                      id={`custom-field-${index}`}
                      key={index}
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                    >
                      <Card className="card d-flex">
                        <div className="centeredFlex mr-2" {...provided.dragHandleProps}>
                          <DragIndicator className={clsx("dndActionIcon", classes.dragIcon)} />
                        </div>
                        <ExpandableCustomFields
                          item={item}
                          classes={classes}
                          field={field}
                          onDataTypeChange={onDataTypeChange}
                          onDelete={onDelete}
                          index={index}
                          onAddOther={onAddOther}
                          isListOrMap={isListOrMap}
                          expanded={expanded}
                          onChange={() => setExpanded(expanded === index ? null : index)}
                          dispatch={dispatch}
                          form={form}
                        />
                      </Card>
                    </div>
                  )}
                </Draggable>
              );
            })}
            {provided.placeholder}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
});

export default renderCustomFields;