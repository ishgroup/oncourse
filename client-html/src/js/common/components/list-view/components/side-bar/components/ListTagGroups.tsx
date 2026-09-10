import { TableModel } from '@api/model';
import useEventCallback from '@mui/utils/useEventCallback';
import React, { memo, useMemo } from 'react';
import { DragDropContext, Droppable, DropResult } from 'react-beautiful-dnd-next';
import { SPECIAL_TYPES_DISPLAY_KEY } from '../../../../../../constants/Config';
import { FormMenuTag } from '../../../../../../model/tags';
import { useAppDispatch, useAppSelector } from '../../../../../utils/hooks';
import { updateTableModel } from '../../../actions';
import { getActiveTags } from '../../../utils/listFiltersUtils';
import { COLUMN_WITH_COLORS } from '../../list/constants';
import ListTagGroup from './ListTagGroup';

interface Props {
  onChangeTagGroups: (tags: FormMenuTag[], type: string) => void;
  rootEntity: string;
}

const SUBJECTS = 'Subjects';

const ListTagGroups = memo<Props>(({ onChangeTagGroups, rootEntity }) => {
  const dispatch = useAppDispatch();

  const tags = useAppSelector(state => state.list.menuTags);

  // only the slices of `records` this tree actually depends on, so an unrelated list fetch
  // does not re-render every tag group
  const tagsOrder = useAppSelector(state => state.list.records.tagsOrder);
  const hasColumns = useAppSelector(state => state.list.records.columns.length > 0);
  const showColoredDots = useAppSelector(
    state => state.list.records.columns.find(c => c.attribute === COLUMN_WITH_COLORS)?.visible
  );

  const specialTypesEnabled = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] === 'true');

  // the checkboxes read the same state the request is built from, so they can never show a
  // selection the list is not actually filtered by
  const activeTags = useMemo(
    () => tags.flatMap(t => getActiveTags(t.children)).map(t => t.tagBody.id.toString()),
    [tags]
  );

  const tagsForRender = useMemo(() => {
    const filteredTags = tags.filter((tag: FormMenuTag) => tag.children.length && (specialTypesEnabled
      ? !tag.tagBody.system && tag.tagBody.name !== SUBJECTS
      : true));

    const filteredSortedTags = [];

    if (tagsOrder && tagsOrder.length) {
      tagsOrder.forEach((tagId: number) => {
        const indexOfTag = filteredTags.findIndex(elem => elem.tagBody.id === tagId);
        if (indexOfTag !== -1) {
          const [foundElement] = filteredTags.splice(indexOfTag, 1);
          filteredSortedTags.push(foundElement);
        }
      });
    }

    return filteredSortedTags.concat(filteredTags);
  }, [tags, tagsOrder, specialTypesEnabled]);

  const updateActive = useEventCallback((updated: FormMenuTag) => {
    const updatedTags = tags.map(t => {
      if (t.tagBody.id === updated.tagBody.id && t.prefix === updated.prefix) {
        return updated;
      }

      return t;
    });
    onChangeTagGroups(updatedTags, "tags");
  });

  const onDragEnd = useEventCallback((result: DropResult) => {
    if (!result.destination || result.destination.index === result.source.index) {
      return;
    }

    const reordered = [...tagsForRender];
    const [removed] = reordered.splice(result.source.index, 1);
    reordered.splice(result.destination.index, 0, removed);

    const model: TableModel = { tagsOrder: reordered.map(tag => tag.tagBody.id) };

    if (hasColumns) dispatch(updateTableModel(rootEntity, model, true));
  });

  const subjects = useMemo(
    () => (specialTypesEnabled
      ? tags.find(tag => tag.tagBody.system && tag.tagBody.name === SUBJECTS)
      : null),
    [tags, specialTypesEnabled]
  );

  return (
    <>
      {specialTypesEnabled && subjects &&
        <ListTagGroup
          activeTags={activeTags}
          key={subjects.prefix + subjects.tagBody.id.toString()}
          rootTag={subjects}
          updateActive={updateActive}
          showColoredDots={false}
          dndEnabled={false}
        />
      }
      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="ROOT" style={{ transform: "none" }}>
          {provided => (
            <div
              {...provided.droppableProps}
              ref={provided.innerRef}
            >
              {tagsForRender.map((t, index) => (
                <ListTagGroup
                  activeTags={activeTags}
                  key={t.prefix + t.tagBody.id.toString()}
                  dndKey={index}
                  rootTag={t}
                  updateActive={updateActive}
                  showColoredDots={showColoredDots}
                />
              ))}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    </>
  );
});

export default ListTagGroups;
