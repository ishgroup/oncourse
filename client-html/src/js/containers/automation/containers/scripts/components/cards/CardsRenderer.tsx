/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useState } from "react";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd";
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
import { ScriptComponent } from "../../../../../../model/scripts";
import ScriptCard from "./CardBase";
import QueryCardContent from "./QueryCardContent";
import MessageCardContent from "./MessageCardContent";
import ReportCardContent from "./ReportCardContent";
import { getType } from "../../utils";
import { ShowConfirmCaller } from "../../../../../../model/common/Confirm";
import AddScriptAction from "../AddScriptAction";
import ScriptIcon from "../../../../../../../images/icon-script.svg";

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
}

interface ScriptItemProps extends Props {
  component: any;
  index: number;
  item: any;
  onDragging: (isDragging: boolean) => void;
  isDragging: boolean;
}

const ScriptCardItem: React.FC<ScriptItemProps & WrappedFieldArrayProps> = props => {
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
    addComponent,
    values,
    component,
    index,
    item,
    onDragging,
    isDragging,
  } = props;

  const [expand, setExpand] = useState<boolean>(false);

  const onDelete = (e, i) => {
    e.stopPropagation();
    showConfirm({
      onConfirm: () => {
        fields.remove(i);
      },
      confirmMessage: "Script component will be deleted permanently"
    });
  };

  const renderVariables = (variables, name, disabled) => (
    <>
      {variables.map(elem => (
        elem.type === "Checkbox" ? (
          <Grid key={getType(elem.type) + elem.label} item xs={12}>
            <FormControlLabel
              control={(
                <FormField
                  type={elem.type.toLowerCase()}
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
              type={getType(elem.type)}
              name={`${name}.${elem.name}`}
              label={elem.label}
              disabled={disabled}
              required
            />
          </Grid>
        )
      ))}
    </>
  );

  const getComponent = (type, item, component) => {
    switch (type) {
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
  };

  const getComponentImage = type => {
    if (type === "Script") {
      return (
        <IconButton size="large" className={classes.cardLeftIcon}>
          <img src={ScriptIcon} alt="icon-script" />
        </IconButton>
      );
    }
    if (type === "Query") {
      return (
        <IconButton size="large" className={classes.cardLeftIcon}>
          <HelpOutlineIcon />
        </IconButton>
      );
    }
    if (type === "Message") {
      return (
        <IconButton size="large" className={classes.cardLeftIcon}>
          <EmailOutlinedIcon />
        </IconButton>
      );
    }
    if (type === "Report") {
      return (
        <IconButton size="large" className={classes.cardLeftIcon}>
          <StackedLineChartIcon />
        </IconButton>
      );
    }
    return null;
  };

  const getHeading = (component, emailTemplates, values) => {
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
        if (templateKeyCode.length > 0) {
          detail = emailTemplates.find(t => t.keyCode === templateKeyCode[0].value).name;
        }
      }
    }

    return (
      <Grid container className={clsx("align-items-center", classes.cardReaderCustomHeading)} spacing={2}>
        <Grid item sm={detail && !expand ? 4 : 12}>
          <Typography className="heading text-truncate" component="div">
            {heading}
          </Typography>
        </Grid>
        {detail && !expand && (
          <Grid item sm={8} className="text-nowrap text-truncate">
            <Typography variant="caption" color="textSecondary">
              {detail}
            </Typography>
          </Grid>
        )}
      </Grid>
    );
  };

  return (
    <>
      <Draggable draggableId={index + component.id} index={index} isDragDisabled={isInternal}>
        {provided => {
          onDragging(!!provided.draggableProps.style.transition);
          return (
            <div ref={provided.innerRef} {...provided.draggableProps}>
              {getComponentImage(component.type)}
              <ScriptCard
                heading={getHeading(component, emailTemplates, values)}
                onDelete={(!isInternal || (!isInternal && component.type === "Script" && hasUpdateAccess))
                  ? e => onDelete(e, index) : null}
                dragHandlerProps={provided.dragHandleProps}
                noPadding={component.type === "Script"}
                onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                customHeading
                onExpand={() => setExpand(!expand)}
              >
                {getComponent(component.type, item, component)}
              </ScriptCard>
              {!isInternal && (
                <AddScriptAction
                  index={index + 1}
                  addComponent={addComponent}
                  form={form}
                  dispatch={dispatch}
                  values={values}
                  hasUpdateAccess={hasUpdateAccess}
                  active={!isDragging && (fields.length - 1) === index}
                  disabled={isDragging}
                />
              )}
            </div>
          );
        }}
      </Draggable>
    </>
  );
};

const CardsRenderer: React.FC<Props & WrappedFieldArrayProps> = props => {
  const {
    fields,
    isInternal,
    setDragging,
    isDragging
  } = props;

  return (
    <DragDropContext onDragEnd={args => onDragEnd({ ...args, fields })}>
      <Droppable droppableId="droppable">
        {provided => (
          <div ref={provided.innerRef} className="card-reader-list">
            {fields.map((item, index) => {
              const component: ScriptComponent = fields.get(index);

              return (
                <div key={component.id} className={clsx("card-reader-item", { "mb-5": isInternal })}>
                  <ScriptCardItem
                    {...props}
                    component={component}
                    index={index}
                    item={item}
                    onDragging={setDragging}
                    isDragging={isDragging}
                  />
                </div>
              );
            })}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
};

export default CardsRenderer;
