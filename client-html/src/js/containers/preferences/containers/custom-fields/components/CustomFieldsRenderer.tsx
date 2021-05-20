/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import { change, Field } from "redux-form";
import {
 FormControlLabel, Grid, Button, Collapse, Card
} from "@material-ui/core";
import DragIndicator from "@material-ui/icons/DragIndicator";
import { CustomFieldType, DataType, EntityType } from "@api/model";
import { CheckboxField, StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import EditInPlaceDateTimeField from "../../../../../common/components/form/form-fields/EditInPlaceDateTimeField";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import EditInPlaceMoneyField from "../../../../../common/components/form/form-fields/EditInPlaceMoneyField";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import {
  validateEmail, validateSingleMandatoryField, validateURL, validateUniqueNamesInArray, validateRegex
} from "../../../../../common/utils/validation";
import { mapSelectItems, sortDefaultSelectItems } from "../../../../../common/utils/common";
import ListMapRenderer from "./ListMapRenderer";

const EntityTypes = Object.keys(EntityType)
  .filter(val => isNaN(Number(val)))
  .map(mapSelectItems);

EntityTypes.sort(sortDefaultSelectItems);

const DataTypes = Object.keys(DataType)
  .filter(val => !['Record', 'File', 'Message template'].includes(val))
  .map(mapSelectItems);

DataTypes.sort(sortDefaultSelectItems);

const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};

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
    switch (field.dataType) {
      case "Checkbox":
        return (
          <FormControlLabel
            className={clsx(classes.checkbox)}
            control={<CheckboxField {...props} stringValue color="primary" className={undefined} />}
            label="Checked by default"
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
            label="Options"
            onKeyPress={preventStarEnter}
          />
      );
      case "Map":
        return <ListMapRenderer {...props as any} dataType={field.dataType} key={field.id || field.uniqid} label="Options" />;
      case "Money":
        return <EditInPlaceMoneyField {...props} />;
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

const renderCustomFields = props => {
  const {
    fields, classes, onDelete, dispatch, meta: { form }
  } = props;

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
                    <div id={`custom-field-${index}`} key={index} ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>
                      <Card className="card d-flex">
                        <div className="centeredFlex mr-2">
                          <DragIndicator className={clsx("dndActionIcon", classes.dragIcon)} />
                        </div>

                        <Grid container spacing={2} className="relative">
                          <Grid item xs={12}>
                            <Grid container>
                              <Grid item xs={3}>
                                <FormField
                                  type="text"
                                  name={`${item}.name`}
                                  label="Name"
                                  fullWidth
                                  className={classes.field}
                                  validate={[validateSingleMandatoryField, validateUniqueNamesInArray]}
                                />
                              </Grid>

                              <Grid item xs={4}>
                                <FormField
                                  type="text"
                                  name={`${item}.fieldKey`}
                                  label="Custom field key"
                                  fullWidth
                                  disabled={field.id}
                                  className={classes.field}
                                  required
                                />
                              </Grid>

                              <Grid item xs={4}>
                                <FormField
                                  type="select"
                                  name={`${item}.dataType`}
                                  label="Data Type"
                                  items={DataTypes}
                                  disabled={field.id}
                                  onChange={onDataTypeChange}
                                  className={classes.field}
                                  fullWidth
                                  required
                                />
                              </Grid>

                              <Grid item xs={1}>
                                <Button
                                  size="small"
                                  classes={{
                                    root: classes.deleteButton
                                  }}
                                  onClick={() => onDelete(field, index)}
                                >
                                  Delete
                                </Button>
                              </Grid>

                              <Grid item xs={3}>
                                <FormField
                                  type="select"
                                  name={`${item}.entityType`}
                                  label="Record Type"
                                  items={EntityTypes}
                                  disabled={field.id}
                                  className={classes.field}
                                  fullWidth
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
                                      value="true"
                                      fullWidth
                                    />
                                  )}
                                  label="Mandatory"
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
                                    label="Add 'other' option"
                                  />
                                )}
                              </Grid>

                              <Grid item xs={5}>
                                <Collapse in={isListOrMap} mountOnEnter unmountOnExit>
                                  <Field
                                    name={`${item}.defaultValue`}
                                    label="Default value"
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
                                    label="Pattern"
                                    disabled={field.id}
                                    className={classes.field}
                                    validate={validateRegex}
                                    required
                                  />
                                </Collapse>
                              </Grid>
                            </Grid>
                          </Grid>
                        </Grid>
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
};

export default renderCustomFields;
