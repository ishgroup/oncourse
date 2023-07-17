import React, { useCallback, useEffect, useState } from "react";
import { DragDropContext, Droppable } from "react-beautiful-dnd-next";
import { connect } from "react-redux";
import { DataResponse, TableModel } from "@api/model";
import { Dispatch } from "redux";
import { createStyles, withStyles } from "@mui/styles";
import { FormMenuTag } from "../../../../../../model/tags";
import ListTagGroup from "./ListTagGroup";
import { State } from "../../../../../../reducers/state";
import { updateTableModel } from "../../../actions";
import { COLUMN_WITH_COLORS } from "../../list/constants";

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
  tags: FormMenuTag[];
  classes: any;
  records: DataResponse;
  onChangeTagGroups: (tags: FormMenuTag[], type: string) => void;
  rootEntity: string;
  updateTableModel: (model: TableModel, listUpdate?: boolean) => void;
}

const ListTagGroups = ({
 tags, classes, onChangeTagGroups, updateTableModel, records 
}: Props) => {
  const showColoredDots = records.columns.find(c => c.attribute === COLUMN_WITH_COLORS)?.visible;
  
  const [tagsForRender, setTagsForRender] = useState([]);

  useEffect(() => {
    const savedTagsOrder = records.tagsOrder;
    const filteredTags = tags.filter((tag: FormMenuTag) => tag.children.length);

    const filteredSortedTags = [];

    if (savedTagsOrder && savedTagsOrder.length) {
      savedTagsOrder.forEach((tagId: number) => {
        const tag = filteredTags.find(elem => elem.tagBody.id === tagId);
        if (tag) {
          const indexOfTag = filteredTags.indexOf(tag);

          const [foundElement] = filteredTags.splice(indexOfTag, 1);
          filteredSortedTags.push(foundElement);
        }
      });
    }

    setTagsForRender(filteredSortedTags.concat(filteredTags));
  }, [records, tags]);
  
  const updateActive = useCallback(
    (updated: FormMenuTag) => {
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
    if (!result.destination || result.destination.index === result.source.index) {
      return;
    }

    const [removed] = tagsForRender.splice(result.source.index, 1);
    tagsForRender.splice(result.destination.index, 0, removed);
    setTagsForRender(tagsForRender);

    const tagsOrder = tagsForRender.map(tag => tag.tagBody.id);

    if (records.columns.length) updateTableModel({ tagsOrder }, true);
  };

  return (
    <DragDropContext
      onDragEnd={args => onDragEnd(args)}
    >
      <Droppable droppableId="ROOT" style={{ transform: "none" }}>
        {provided => (
          <div
            {...provided.droppableProps}
            ref={provided.innerRef}
          >
            {tagsForRender.map((t, index) => {
              if (!t.children.length) {
                return null;
              }
              return (
                <ListTagGroup
                  key={t.prefix + t.tagBody.id.toString()}
                  dndKey={index}
                  rootTag={t}
                  classes={classes}
                  updateActive={updateActive}
                  showColoredDots={showColoredDots}
                />
              );
            })}
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

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(ListTagGroups));
