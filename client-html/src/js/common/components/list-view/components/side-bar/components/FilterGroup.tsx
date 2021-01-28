import * as React from "react";
import FilterItem from "./FilterItem";
import { CoreFilter, FilterGroup } from "../../../../../../model/common/ListView";

interface Props {
  title?: string;
  filters?: CoreFilter[];
  groupIndex?: number;
  rootEntity?: string;
  deleteFilter?: (id: number, rootEntity: string, checked: boolean) => void;
  onUpdate?: (filterGroups: FilterGroup[], type: string) => void;
  filterGroups?: FilterGroup[];
}

const FilterGroupComp = (props: Props) => {
  const {
   title, onUpdate, filters, groupIndex, deleteFilter, rootEntity
  } = props;

  return (
    <>
      <div className="heading mt-2">{title}</div>
      {filters.map((i, index) => (
        <FilterItem
          key={i.name}
          label={i.name}
          customLabel={i.customLabel}
          id={i.id}
          checked={i.active}
          expression={i.expression}
          isPrivate={i.showForCurrentOnly}
          onDelete={deleteFilter}
          rootEntity={rootEntity}
          index={groupIndex + "/" + index}
          onChange={onUpdate}
          deletable={title === "Custom Filters"}
        />
      ))}
    </>
  );
};

export default FilterGroupComp;
