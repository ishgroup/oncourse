import { DataResponse, TableModel } from '@api/model';
import React, { useEffect, useMemo, useState } from 'react';
import { DragDropContext, Droppable } from 'react-beautiful-dnd-next';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { SPECIAL_TYPES_DISPLAY_KEY } from '../../../../../../constants/Config';
import { FormMenuTag } from '../../../../../../model/tags';
import { State } from '../../../../../../reducers/state';
import { IAction } from '../../../../../actions/IshAction';
import { useAppSelector } from '../../../../../utils/hooks';
import { updateTableModel } from '../../../actions';
import { COLUMN_WITH_COLORS } from '../../list/constants';
import ListTagGroup from './ListTagGroup';

const styles = theme =>
  ({
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
  const specialTypesEnabled = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] === 'true');

  const showColoredDots = records.columns.find(c => c.attribute === COLUMN_WITH_COLORS)?.visible;
  
  const [tagsForRender, setTagsForRender] = useState([]);

  useEffect(() => {
    const savedTagsOrder = records.tagsOrder;
    const filteredTags = tags.filter((tag: FormMenuTag) => tag.children.length && (specialTypesEnabled
      ? !tag.tagBody.system && tag.tagBody.name !== 'Subjects'
      : true));

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
  
  const updateActive = (updated: FormMenuTag) => {
    const updatedTags = tags.map(t => {
      if (t.tagBody.id === updated.tagBody.id && t.prefix === updated.prefix) {
        return updated;
      }

      return t;
    });
    onChangeTagGroups(updatedTags, "tags");
  };

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
  
  const subjects = useMemo(() => {
    if (specialTypesEnabled) {
      return tags.filter(tag => tag.tagBody.system && tag.tagBody.name === 'Subjects')[0];
    }
    return null;
  }, [tags, specialTypesEnabled]);

  return (
    <>
      {specialTypesEnabled && subjects &&
        <ListTagGroup
          key={subjects.prefix + subjects.tagBody.id.toString()}
          rootTag={subjects}
          classes={classes}
          updateActive={updateActive}
          showColoredDots={false}
          dndEnabled={false}
        />
      }
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
    </>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.list.menuTags,
  records: state.list.records,
});

const mapDispatchToProps = (dispatch: Dispatch<IAction>, ownProps) => ({
  updateTableModel: (model: TableModel, listUpdate?: boolean) => dispatch(updateTableModel(ownProps.rootEntity, model, listUpdate)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(ListTagGroups, styles));
