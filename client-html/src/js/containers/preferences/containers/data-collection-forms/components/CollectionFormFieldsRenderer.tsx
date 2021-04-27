import React from "react";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import { change } from "redux-form";
import CollectionFormField from "./CollectionFormField";
import CollectionFormHeading from "./CollectionFormHeading";

const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
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

  dispatch(change("DataCollectionForm", "items", reordered));
};

const renderCollectionFormFields = props => {
  const {
    fields, classes, deleteField, dispatch, errors
  } = props;

  return (
    <DragDropContext onDragEnd={result => onDragEnd(result, fields.getAll(), dispatch)}>
      <Droppable droppableId="droppable">
        {provided => (
          <div ref={provided.innerRef}>
            {fields.map((item, index) => {
              const field = fields.get(index);

              if (field.baseType === "field") {
                return (
                  <Draggable key={field.type.uniqueKey} draggableId={field.type.uniqueKey} index={index}>
                    {(provided, snapshot) => (
                      <div
                        id={`data-collection-form-${index}`}
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                      >
                        <CollectionFormField
                          item={field}
                          field={item}
                          className={snapshot.isDragging ? classes.boxShadow : classes.boxShadowHover}
                          onDelete={() => deleteField(index)}
                        />
                      </div>
                    )}
                  </Draggable>
                );
              }

              return (
                <Draggable key={index} draggableId={field.name + index} index={index}>
                  {(provided, snapshot) => (
                    <div
                      className={classes.heading}
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                      {...provided.dragHandleProps}
                      id={`data-collection-form-${index}`}
                    >
                      <CollectionFormHeading
                        item={field}
                        index={index}
                        field={item}
                        errors={errors}
                        className={snapshot.isDragging ? classes.boxShadow : classes.boxShadowHover}
                        onDelete={() => deleteField(index)}
                      />
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

export default renderCollectionFormFields;
