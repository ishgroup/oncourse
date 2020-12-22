/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { ScriptComponent } from "../../../../../../model/scripts";
import ScriptCard from "./CardBase";
import EmailCardContent from "./EmailCardContent";
import QueryCardContent from "./QueryCardContent";
import MessageCardContent from "./MessageCardContent";
import ReportCardContent from "./ReportCardContent";
import Grid from "@material-ui/core/Grid";
import { getType } from "../../utils";
import { FormControlLabel } from "@material-ui/core";

const onDragEnd = ({ destination, source, fields }) => {
  if (destination && destination.index !== source.index) {
    fields.swap(source.index, destination.index);
  }
};

const CardsRenderer = props => {
  const {
    fields,
    dispatch,
    onValidateQuery,
    classes,
    showConfirm,
    hasUpdateAccess,
    isInternal,
    isValidQuery,
    onInternalSaveClick,
    emailTemplates,
    pdfReports,
    pdfBackgrounds,
  } = props;

  const onDelete = (e, index, fields, showConfirm) => {
    e.stopPropagation();
    showConfirm(() => {
      const component = fields.get(index);
      if (component && component.type === "Query") {
        const allComponents = fields.getAll();
        allComponents.splice(index, 1);
        if (!allComponents.some(c => c.type === "Query")) {
          onValidateQuery(true);
        }
      }
      fields.remove(index);
    }, "Script component will be deleted permanently");
  };

  const renderVariables = (variables, name) => {
    return (<>
      {variables.map(elem => (
        elem.type === "Checkbox" ? (
          <Grid key={getType(elem.type) + elem.label} item xs={12}>
            <FormControlLabel
              control={
                <FormField
                  type={elem.type.toLowerCase()}
                  name={`${name}.${elem.name}`}
                  label={elem.label}
                />
              }
              label={elem.label}
            />
          </Grid>
        ) : (
          <Grid key={getType(elem.type) + elem.label} item xs={12}>
            <FormField
              type={getType(elem.type)}
              name={`${name}.${elem.name}`}
              label={elem.label}
              required
            />
          </Grid>
        )
      ))}
    </>)
  }

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
                              disabled={!hasUpdateAccess}
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
                              pdfReports={pdfReports}
                              pdfBackgrounds={pdfBackgrounds}
                              renderVariables={renderVariables}
                            />
                          </ScriptCard>
                        </div>
                      )}
                    </Draggable>
                  );
                }

                case "Email": {
                  return (
                    <Draggable key={component.id} draggableId={index + component.id} index={index} isDragDisabled={isInternal}>
                      {provided => (
                        <div ref={provided.innerRef} {...provided.draggableProps}>
                          <ScriptCard
                            heading="Email"
                            className="mb-3"
                            onDelete={!isInternal ? e => onDelete(e, index, fields, showConfirm) : null}
                            onAddItem={() => null}
                            dragHandlerProps={provided.dragHandleProps}
                            expanded
                            onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                          >
                            <EmailCardContent name={item} field={component} classes={classes} />
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
