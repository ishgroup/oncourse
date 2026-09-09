import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import * as React from 'react';
import { CoreFilter, FilterGroup } from '../../../../../../model/common/ListView';
import FilterItem from './FilterItem';

interface Props {
  title?: string;
  filters?: CoreFilter[];
  groupIndex?: number;
  rootEntity?: string;
  deleteFilter?: (id: number, rootEntity: string, checked: boolean) => void;
  onUpdate?: (filter: string, checked: boolean) => void;
  filterGroups?: FilterGroup[];
}

const getIndex = (groupIndex, index) => groupIndex + "/" + index;

const FilterGroupComp = ({
 title, onUpdate, filters, groupIndex, deleteFilter, rootEntity
}: Props) => {

  return (
    <>
      <div className="heading mt-2">{title}</div>
      <SimpleTreeView
        multiSelect
        checkboxSelection
        onItemSelectionToggle={(e, id, selected) => onUpdate(id, selected)}
        selectedItems={filters.reduce((p, c, index) => {
          if (c.active) {
            p.push(getIndex( groupIndex, index));
          }
          return p;
        }, [])}
        sx={{
          marginLeft: -1
        }}
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
            deletable={title === "Custom Filters"}
          />
        ))}
      </SimpleTreeView>
    </>
  );
};

export default FilterGroupComp;
