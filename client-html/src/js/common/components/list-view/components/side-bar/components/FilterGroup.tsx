import useEventCallback from '@mui/utils/useEventCallback';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import * as React from 'react';
import { memo, useMemo } from 'react';
import { CoreFilter } from '../../../../../../model/common/ListView';
import FilterItem, { DeleteFilterHandler } from './FilterItem';

interface Props {
  title?: string;
  filters?: CoreFilter[];
  groupIndex?: number;
  rootEntity?: string;
  deleteFilter?: DeleteFilterHandler;
  onUpdate?: (filter: string, checked: boolean) => void;
}

const treeSx = { marginLeft: -1 };

/**
 * Addresses one filter inside the tree view, and is also its react key.
 *
 * The two have to be the same string: the tree view only releases an item id when the component
 * holding it unmounts, never when its `itemId` changes. Keying by anything else - the filter name -
 * lets an instance survive a reorder and carry a new id while it still owns the old one, and the
 * next filter to land on that id is rejected as a duplicate.
 */
const getIndex = (groupIndex, index) => groupIndex + "/" + index;

const FilterGroupComp = memo<Props>(({
 title, onUpdate, filters, groupIndex, deleteFilter, rootEntity
}) => {
  const selectedItems = useMemo(
    () => filters.reduce((p, c, index) => {
      if (c.active) {
        p.push(getIndex(groupIndex, index));
      }
      return p;
    }, []),
    [filters, groupIndex]
  );

  const onItemSelectionToggle = useEventCallback(
    (e: React.SyntheticEvent, id: string, selected: boolean) => onUpdate(id, selected)
  );

  const deletable = title === "Custom Filters";

  return (
    <>
      <div className="heading mt-2">{title}</div>
      <SimpleTreeView
        multiSelect
        checkboxSelection
        onItemSelectionToggle={onItemSelectionToggle}
        selectedItems={selectedItems}
        sx={treeSx}
      >
        {filters.map((i, index) => (
          <FilterItem
            key={getIndex(groupIndex, index)}
            label={i.name}
            customLabel={i.customLabel}
            id={getIndex(groupIndex, index)}
            checked={i.active}
            expression={i.expression}
            isPrivate={i.showForCurrentOnly}
            onDelete={deleteFilter}
            rootEntity={rootEntity}
            deletable={deletable}
          />
        ))}
      </SimpleTreeView>
    </>
  );
});

export default FilterGroupComp;
