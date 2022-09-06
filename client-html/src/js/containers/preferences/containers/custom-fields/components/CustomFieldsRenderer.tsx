/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo, useState } from "react";
import clsx from "clsx";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd-next";
import { change, Field } from "redux-form";
import Card from "@mui/material/Card";
import Collapse from "@mui/material/Collapse";
import Grid from "@mui/material/Grid";
import FormControlLabel from "@mui/material/FormControlLabel";
import DeleteIcon from "@mui/icons-material/Delete";
import IconButton from "@mui/material/IconButton";
import DragIndicator from "@mui/icons-material/DragIndicator";
import { CustomFieldType, DataType, EntityType } from "@api/model";
import { CheckboxField, StyledCheckbox } from "../../../../../common/components/form/formFields/CheckboxField";
import EditInPlaceDateTimeField from "../../../../../common/components/form/formFields/EditInPlaceDateTimeField";
import EditInPlaceField from "../../../../../common/components/form/formFields/EditInPlaceField";
import EditInPlaceMoneyField from "../../../../../common/components/form/formFields/EditInPlaceMoneyField";
import FormField from "../../../../../common/components/form/formFields/FormField";
import {
  validateEmail,
  validateRegex,
  validateSingleMandatoryField,
  validateUniqueNamesInArray,
  validateURL
} from "../../../../../common/utils/validation";
import { mapSelectItems, sortDefaultSelectItems } from "../../../../../common/utils/common";
import ListMapRenderer from "./ListMapRenderer";
import ExpandableItem from "../../../../../common/components/layout/expandable/ExpandableItem";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { SelectItemDefault } from "../../../../../model/entities/common";

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

const ExpandableCustomFields = React.memo<any>(props => {
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
  } = props;

  const isExpanded = useMemo(() => ((expanded !== null && [index].includes(expanded)) || !field.id || !field.name), [expanded, field]);

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
              label="Name"
            />
          </Grid>
          <Grid item xs={4}>
            <Uneditable
              value={mapEntityType(field.entityType)}
              label="Record Type"
            />
          </Grid>
          <Grid item xs={4}>
            <Uneditable
              value={field.dataType}
              label="Data Type"
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
              disabled={!!field.id}
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
              disabled={!!field.id}
              onChange={onDataTypeChange}
              debounced={false}
              className={classes.field}
              fullWidth
              required
            />
          </Grid>

          <Grid item xs={4}>
            <FormField
              type="select"
              name={`${item}.entityType`}
              selectLabelCondition={entityTypeCondition}
              label="Record Type"
              items={EntityTypes}
              disabled={!!field.id}
              className={classes.field}
              sort
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

          <Grid item xs={4}>
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
                      {...provided.dragHandleProps}
                    >
                      <Card className="card d-flex">
                        <div className="centeredFlex mr-2">
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
