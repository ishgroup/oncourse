/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useMemo
} from "react";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd-next";
import Grid from "@mui/material/Grid";
import { FormControlLabel } from "@mui/material";
import { WrappedFieldArrayProps } from "redux-form";
import { Dispatch } from "redux";
import clsx from "clsx";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import StackedLineChartIcon from "@mui/icons-material/StackedLineChart";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import { ScriptComponent, ScriptExtended } from "../../../../../../model/scripts";
import ScriptCard from "./CardBase";
import QueryCardContent from "./QueryCardContent";
import MessageCardContent from "./MessageCardContent";
import ReportCardContent from "./ReportCardContent";
import { getType } from "../../utils";
import { ShowConfirmCaller } from "../../../../../../model/common/Confirm";
import AddScriptAction from "../AddScriptAction";
import ScriptIcon from "../../../../../../../images/icon-script.svg";
import { Binding } from "@api/model";

const onDragEnd = ({ destination, source, fields }) => {
  if (destination && destination.index !== source.index) {
    fields.swap(source.index, destination.index);
  }
};

interface Props {
  dispatch: Dispatch;
  classes: any;
  showConfirm: ShowConfirmCaller;
  hasUpdateAccess: boolean;
  isInternal: boolean;
  onInternalSaveClick: any;
  emailTemplates: any[];
  addComponent?: any;
  values?: any;
  setDragging?: (dragging: boolean) => void;
  isDragging?: boolean;
  syncErrors?: ScriptExtended;
  expanded: any[];
  onExpand: any;
}

interface ScriptItemProps extends Props {
  component: any;
  index: number;
  item: string;
  draggableId: any;
  syncErrors: any;
  expand: boolean;
}

const ScriptCardItem = React.memo<ScriptItemProps & WrappedFieldArrayProps>(props => {
  const {
    fields,
    showConfirm,
    dispatch,
    meta: { form },
    classes,
    hasUpdateAccess,
    isInternal,
    onInternalSaveClick,
    emailTemplates,
    values,
    component,
    index,
    item,
    draggableId,
    syncErrors,
    expand,
    onExpand
  } = props;

  const invalid = Boolean(syncErrors[fields.name] && syncErrors[fields.name][index]);

  const onDelete = useCallback((e, i) => {
    e.stopPropagation();
    showConfirm({
      onConfirm: () => {
        fields.remove(i);
      },
      confirmMessage: "Script component will be deleted permanently"
    });
  }, [fields]);

  const renderVariables = useCallback((variables: Binding[], name, disabled) => (
    <>
      {variables.map(elem => (
        elem.type === "Checkbox" ? (
          <Grid key={getType(elem.type) + elem.label} item xs={12}>
            <FormControlLabel
              control={(
                <FormField
                  type={elem.type.toLowerCase() as any}
                  name={`${name}.${elem.name}`}
                  label={elem.label}
                />
              )}
              disabled={disabled}
              label={elem.label}
            />
          </Grid>
        ) : (
          <Grid key={getType(elem.type) + elem.label} item xs={12}>
            <FormField
              type={getType(elem.type) as any}
              name={`${name}.${elem.name}`}
              label={elem.label}
              disabled={disabled}
              required
            />
          </Grid>
        )
      ))}
    </>
  ), []);

  const getComponent = useMemo(() => {
    switch (component.type) {
      case "Script":
        return (
          <FormField
            type="code"
            name={`${item}.content`}
            disabled={isInternal || !hasUpdateAccess}
            className="mt-3"
          />
        );
      case "Query":
        return (
          <QueryCardContent
            dispatch={dispatch}
            field={component}
            name={item}
            classes={classes}
            disabled={isInternal}
          />
        );
      case "Message":
        return (
          <MessageCardContent
            dispatch={dispatch}
            field={component}
            name={item}
            classes={classes}
            emailTemplates={emailTemplates}
            renderVariables={renderVariables}
            form={form}
            disabled={isInternal}
          />
        );
      case "Report":
        return (
          <ReportCardContent
            dispatch={dispatch}
            field={component}
            name={item}
            form={form}
            disabled={isInternal}
            renderVariables={renderVariables}
          />
        );
      default:
        return null;
    }
  }, [component, component.type, item]);

  const getHeading = useMemo(() => {
    const type = component.type;
    let heading = type;
    let detail = null;

    if (type === "Query") {
      heading = component.entity ? `Find ${component.entity}` : type;
      detail = component.query;
    } else if (type === "Message") {
      heading = "Send message";
      detail = component.template && component.templateEntity ? component.templateEntity.name : null;
      if (emailTemplates && emailTemplates.length && values && values.options) {
        const templateKeyCode = values.options.filter(o => o.name === component.template);
        const emailTemplate = templateKeyCode.length && emailTemplates.find(t => t.keyCode === templateKeyCode[0].value);
        if (emailTemplate) {
          detail = emailTemplate.name;
        }
      }
    }

    return (
      <div className={clsx("w-100 centeredFlex text-nowrap text-truncate", classes.cardReaderCustomHeading)}>
        <Typography className="heading mr-5" component="div">
          {heading}
        </Typography>
        {detail && !expand && (
          <div className="text-nowrap text-truncate pr-1">
            <Typography variant="caption" color="textSecondary">
              {detail}
            </Typography>
          </div>
        )}
      </div>
    );
  }, [
    values,
    values.options,
    component,
    component.type,
    component.entity,
    component.query,
    component.template,
    component.template && component.template.templateEntity,
    component.template && component.template.templateEntity && component.templateEntity.name,
    emailTemplates,
    expand
  ]);
  
  return (
    <Draggable draggableId={draggableId} index={index} isDragDisabled={isInternal}>
      {provided => (
        <div ref={provided.innerRef} {...provided.draggableProps}>
          <ScriptCard
            customHeading
            heading={getHeading}
            onDelete={(!isInternal || (!isInternal && component.type === "Script" && hasUpdateAccess))
                  ? e => onDelete(e, index) : null}
            dragHandlerProps={provided.dragHandleProps}
            noPadding={component.type === "Script"}
            onDetailsClick={isInternal ? onInternalSaveClick : undefined}
            onExpand={invalid ? null : onExpand}
            expanded={invalid || expand}
          >
            {getComponent}
          </ScriptCard>
        </div>
          )}
    </Draggable>
  );
});

const getComponentImage = type => {
  switch (type) {
    case "Script":
      return <img src={ScriptIcon} alt="icon-script" />;
    case "Query":
      return <HelpOutlineIcon />;
    case "Message":
      return <EmailOutlinedIcon />;
    case "Report":
      return <StackedLineChartIcon />;
    default:
      return null;
  }
};

const CardsRenderer: React.FC<Props & WrappedFieldArrayProps> = props => {
  const {
    fields,
    isInternal,
    classes,
    addComponent,
    meta: { form },
    dispatch,
    values,
    hasUpdateAccess,
    setDragging,
    isDragging,
    syncErrors,
    expanded,
    onExpand
  } = props;

  const onDragEndHandler = args => {
    onDragEnd({ ...args, fields });
    setDragging(false);
  };

  return (
    <DragDropContext onDragEnd={onDragEndHandler} onDragStart={setDragging}>
      <Droppable droppableId="droppable">
        {(provided, { isDraggingOver, draggingOverWith, draggingFromThisWith }) => {
          const draggingItemHeight = provided.placeholder.props.on ? provided.placeholder.props.on.client.contentBox.height : 0;

          return (
            <div
              ref={provided.innerRef}
              style={(isDragging || isDraggingOver) ? { paddingBottom: draggingItemHeight } : undefined}
            >
              {fields.map((item, index) => {
                const component: ScriptComponent = fields.get(index);
                const leftIcon = getComponentImage(component.type);
                const draggableId = index + component.id;
                const isDraggingItem = draggingOverWith === draggableId || draggingFromThisWith === draggableId;
                const isAfterDragging = draggingOverWith ? index > Number(draggingOverWith[0]) : false;

                return (
                  <div key={component.id} className={clsx("relative", { "mb-5": isInternal })}>
                    <div style={!isDraggingItem && isAfterDragging && (isDragging || isDraggingOver) ? {
                      transform: `translateY(${draggingItemHeight}px)`,
                    } : {}}
                    >
                      {leftIcon && (
                        <IconButton size="large" className={classes.cardLeftIcon} disableRipple>
                          {leftIcon}
                        </IconButton>
                      )}
                    </div>
                    <ScriptCardItem
                      {...props}
                      expand={expanded.includes(component.id)}
                      onExpand={() => onExpand(component.id)}
                      key={component.id}
                      syncErrors={syncErrors}
                      draggableId={draggableId}
                      component={component}
                      index={index}
                      item={item}
                    />
                    <div style={(isAfterDragging || isDraggingItem) && (isDragging || isDraggingOver) 
                      ? { transform: `translateY(${draggingItemHeight}px)` } 
                      : {}}
                    >
                      {!isInternal && (
                        <AddScriptAction
                          index={index + 1}
                          addComponent={addComponent}
                          form={form}
                          dispatch={dispatch}
                          values={values}
                          hasUpdateAccess={hasUpdateAccess}
                          disabled={isDraggingOver || isDragging}
                          active={(fields.length - 1) === index}
                        />
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          );
        }}
      </Droppable>
    </DragDropContext>
  );
};

export default CardsRenderer;
