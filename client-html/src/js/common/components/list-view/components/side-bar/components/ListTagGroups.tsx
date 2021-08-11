import React, { useCallback, useEffect, useState } from "react";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import { connect } from "react-redux";
import { DataResponse, TableModel } from "@api/model";
import { Dispatch } from "redux";
import { createStyles, withStyles } from "@material-ui/core";
import { MenuTag } from "../../../../../../model/tags";
import ListTagGroup from "./ListTagGroup";
import { State } from "../../../../../../reducers/state";
import { updateTableModel } from "../../../actions";

const styles = theme =>
  createStyles({
    container: {
      marginLeft: theme.spacing(-0.5)
    },
    noTransform: {
      transform: "none !important"
    },
  });

interface Props {
  tags: MenuTag[];
  classes: any;
  records: DataResponse;
  onChangeTagGroups: (tags: MenuTag[], type: string) => void;
  rootEntity: string;
  updateTableModel: (model: TableModel, listUpdate?: boolean) => void;
}

const ListTagGroups: React.FC<Props> = ({ tags, classes, onChangeTagGroups, updateTableModel, records }) => {
  const [hasDraggingGroup, setGroupIsDragging] = useState(false);
  const [tagsForRender, setTagsForRender] = useState([]);

  useEffect(() => {
    const savedTagsOrder = records.tagsOrder;
    const filteredTags = tags.filter((tag: MenuTag) => tag.children.length);

    const filteredSortedTags = [];

    if (savedTagsOrder.length) {
      savedTagsOrder.forEach((tagId: number) => {
        const tag = filteredTags.find(elem => elem.tagBody.id === tagId);
        const indexOfTag = filteredTags.indexOf(tag);

        const [foundElement] = filteredTags.splice(indexOfTag, 1);
        filteredSortedTags.push(foundElement);
      });
    }

    setTagsForRender(filteredSortedTags.concat(filteredTags));
  }, [tags]);

  const updateActive = useCallback(
    (updated: MenuTag) => {
      const updatedTags = tags.map(t => {
        if (t.tagBody.id === updated.tagBody.id && t.prefix === updated.prefix) {
          return updated;
        }

        return t;
      });
      onChangeTagGroups(updatedTags, "tags");
    },
    [tags]
  );

  const onDragEnd = result => {
    setGroupIsDragging(false);

    if (!result.destination) {
      return;
    }

    const [removed] = tagsForRender.splice(result.source.index, 1);
    tagsForRender.splice(result.destination.index, 0, removed);
    setTagsForRender(tagsForRender);

    const tagsOrder = tagsForRender.map(tag => tag.tagBody.id);

    updateTableModel({ tagsOrder });
  };

  const onBeforeCapture = () => {
    setGroupIsDragging(true);
  };

  return (
    <DragDropContext
      onBeforeCapture={onBeforeCapture}
      onDragEnd={args => onDragEnd(args)}
    >
      <Droppable droppableId="ROOT" style={{ transform: "none" }}>
        {provided => (
          <div
            {...provided.droppableProps}
            ref={provided.innerRef}
          >
            {tagsForRender.map((t, index) => (
              <ListTagGroup
                key={t.prefix + t.tagBody.id.toString()}
                dndKey={index}
                rootTag={t}
                classes={classes}
                updateActive={updateActive}
                hasDraggingGroup={hasDraggingGroup}
              />
            ))}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.list.menuTags,
  records: state.list.records,
});

const mapDispatchToProps = (dispatch: Dispatch, ownProps) => ({
  updateTableModel: (model: TableModel, listUpdate?: boolean) => dispatch(updateTableModel(ownProps.rootEntity, model, listUpdate)),
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(ListTagGroups));
