/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import Grid from "@material-ui/core/Grid";
import { FormControlLabel } from "@material-ui/core";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { ScriptComponent } from "../../../../../../model/scripts";
import ScriptCard from "./CardBase";
import QueryCardContent from "./QueryCardContent";
import MessageCardContent from "./MessageCardContent";
import ReportCardContent from "./ReportCardContent";
import { getType } from "../../utils";

const onDragEnd = ({ destination, source, fields }) => {
  if (destination && destination.index !== source.index) {
    fields.swap(source.index, destination.index);
  }
};

const CardsRenderer = props => {
  const {
    fields,
    dispatch,
    meta: { form },
    onValidateQuery,
    classes,
    showConfirm,
    hasUpdateAccess,
    isInternal,
    isValidQuery,
    onInternalSaveClick,
    emailTemplates
  } = props;

  const onDelete = (e, index, fieldsIns, showConfirm) => {
    e.stopPropagation();
    showConfirm(() => {
      const component = fieldsIns.get(index);
      if (component && component.type === "Query") {
        const allComponents = fieldsIns.getAll();
        allComponents.splice(index, 1);
        if (!allComponents.some(c => c.type === "Query")) {
          onValidateQuery(true);
        }
      }
      fieldsIns.remove(index);
    }, "Script component will be deleted permanently");
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

  return (
    <DragDropContext onDragEnd={args => onDragEnd({ ...args, fields })}>
      <Droppable droppableId="droppable">
        {provided => (
          <div ref={provided.innerRef} className="pt-3 pb-3">
            {fields.map((item, index) => {
              const component: ScriptComponent = fields.get(index);

              switch (component.type) {
                case "Script": {
                  return (
                    <Draggable key={component.id} draggableId={index + component.id} index={index} isDragDisabled={isInternal}>
                      {provided => (
                        <div ref={provided.innerRef} {...provided.draggableProps}>
                          <ScriptCard
                            heading="Script"
                            className="mb-3"
                            onDelete={!isInternal && hasUpdateAccess ? e => onDelete(e, index, fields, showConfirm) : null}
                            dragHandlerProps={provided.dragHandleProps}
                            expanded
                            noPadding
                            onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                          >
                            <FormField
                              type="code"
                              name={`${item}.content`}
                              disabled={isInternal || !hasUpdateAccess}
                              className="mt-3"
                            />
                          </ScriptCard>
                        </div>
                      )}
                    </Draggable>
                  );
                }
                case "Query": {
                  return (
                    <Draggable key={component.id} draggableId={index + component.id} index={index} isDragDisabled={isInternal}>
                      {provided => (
                        <div ref={provided.innerRef} {...provided.draggableProps}>
                          <ScriptCard
                            heading="Query"
                            className="mb-3"
                            onDelete={!isInternal ? e => onDelete(e, index, fields, showConfirm) : null}
                            dragHandlerProps={provided.dragHandleProps}
                            expanded
                            onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                          >
                            <QueryCardContent
                              dispatch={dispatch}
                              field={component}
                              name={item}
                              classes={classes}
                              onValidateQuery={onValidateQuery}
                              isValidQuery={isValidQuery}
                              disabled={isInternal}
                            />
                          </ScriptCard>
                        </div>
                      )}
                    </Draggable>
                  );
                }
                case "Message": {
                  return (
                    <Draggable key={component.id} draggableId={index + component.id} index={index} isDragDisabled={isInternal}>
                      {provided => (
                        <div ref={provided.innerRef} {...provided.draggableProps}>
                          <ScriptCard
                            heading="Message"
                            className="mb-3"
                            onDelete={!isInternal ? e => onDelete(e, index, fields, showConfirm) : null}
                            dragHandlerProps={provided.dragHandleProps}
                            expanded
                            onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                          >
                            <MessageCardContent
                              dispatch={dispatch}
                              field={component}
                              name={item}
                              classes={classes}
                              onValidateQuery={onValidateQuery}
                              isValidQuery={isValidQuery}
                              emailTemplates={emailTemplates}
                              renderVariables={renderVariables}
                              form={form}
                              disabled={isInternal}
                            />
                          </ScriptCard>
                        </div>
                      )}
                    </Draggable>
                  );
                }

                case "Report": {
                  return (
                    <Draggable key={component.id} draggableId={index + component.id} index={index} isDragDisabled={isInternal}>
                      {provided => (
                        <div ref={provided.innerRef} {...provided.draggableProps}>
                          <ScriptCard
                            heading="Report"
                            className="mb-3"
                            onDelete={!isInternal ? e => onDelete(e, index, fields, showConfirm) : null}
                            dragHandlerProps={provided.dragHandleProps}
                            expanded
                            onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                          >
                            <ReportCardContent
                              dispatch={dispatch}
                              field={component}
                              name={item}
                              classes={classes}
                              form={form}
                              renderVariables={renderVariables}
                            />
                          </ScriptCard>
                        </div>
                      )}
                    </Draggable>
                  );
                }
                default:
                  return null;
              }
            })}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
};

export default CardsRenderer;
