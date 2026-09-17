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
            key={i.name}
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
