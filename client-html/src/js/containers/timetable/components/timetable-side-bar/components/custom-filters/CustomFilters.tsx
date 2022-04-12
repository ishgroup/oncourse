/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useContext } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import clsx from "clsx";
import { Filter } from "@api/model";
import FilterGroup from "../../../../../../common/components/list-view/components/side-bar/components/FilterGroup";
import { CoreFilter, SavingFilterState } from "../../../../../../model/common/ListView";
import { State } from "../../../../../../reducers/state";
import {
  deleteTimetableFilter,
  saveTimetableFilter,
  setTimetableFilters,
  setTimetableSavingFilter,
} from "../../../../actions";
import { StubFilterItemBase } from "../../../../../../common/components/list-view/components/side-bar/components/StubFilterItem";
import { TimetableContext } from "../../../../Timetable";
import { Fetch } from "../../../../../../model/common/Fetch";

interface Props {
  filters?: CoreFilter[];
  savingFilter?: Filter;
  saveFilter?: (filter: Filter) => void;
  setSavingFilter?: (savingFilter?: SavingFilterState) => void;
  setTimetableFilters?: (filters?: CoreFilter[]) => void;
  fetch: Fetch;
  deleteTimetableFilter?: (id: number, currentMonth: Date) => void;
}

const CustomFilters = React.memo<Props>(
  ({
    filters,
    savingFilter,
    setSavingFilter,
    saveFilter,
    setTimetableFilters,
    deleteTimetableFilter,
    fetch
  }) => {
    const { selectedMonth } = useContext(TimetableContext);

    const onFiltersUpdate = (filter, active) => {
      const filterIndex = Number(filter.split("/")[1]);
      const updatedFilters = filters.map((f, i) => ({ ...f, active: i === filterIndex ? active : f.active }));
      setTimetableFilters(updatedFilters);
    };

    const onDeleteFilter = useCallback(
      id => {
        deleteTimetableFilter(id, selectedMonth);
      },
      [filters, selectedMonth]
    );

    return (
      <div className={clsx("p-2", fetch.pending && "pointer-events-none")}>
        {savingFilter && !filters.length && <div className="heading mt-2">Custom Filters</div>}

        {filters.length ? (
          <FilterGroup
            groupIndex={0}
            deleteFilter={onDeleteFilter}
            rootEntity="Session"
            onUpdate={onFiltersUpdate}
            title="Custom Filters"
            filters={filters}
          />
        ) : null}

        {savingFilter && (
          <StubFilterItemBase
            rootEntity="Session"
            savingFilter={savingFilter}
            saveFilter={saveFilter}
            setSavingFilter={setSavingFilter}
          />
        )}
      </div>
    );
  }
);

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  filters: state.timetable.filters,
  savingFilter: state.timetable.savingFilter
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  saveFilter: (filter: Filter) => dispatch(saveTimetableFilter(filter)),
  setSavingFilter: (savingFilter?: SavingFilterState) => dispatch(setTimetableSavingFilter(savingFilter)),
  deleteTimetableFilter: (id: number, currentMonth: Date) => dispatch(deleteTimetableFilter(id, currentMonth)),
  setTimetableFilters: (filters?: CoreFilter[]) => dispatch(setTimetableFilters(filters))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CustomFilters);
