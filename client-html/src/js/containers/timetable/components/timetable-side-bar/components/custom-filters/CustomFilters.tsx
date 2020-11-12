/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useContext, useEffect } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import clsx from "clsx";
import { Filter, SearchRequest } from "@api/model";
import { addMonths, endOfMonth, startOfMonth } from "date-fns";
import FilterGroup from "../../../../../../common/components/list-view/components/side-bar/components/FilterGroup";
import { CoreFilter, SavingFilterState } from "../../../../../../model/common/ListView";
import { State } from "../../../../../../reducers/state";
import {
  clearTimetableMonths,
  deleteTimetableFilter,
  findTimetableSessions,
  getTimetableFilters,
  getTimetableSessionsDays,
  saveTimetableFilter,
  setTimetableFilters,
  setTimetableSavingFilter,
  setTimetableSearch
} from "../../../../actions";
import { StubFilterItemBase } from "../../../../../../common/components/list-view/components/side-bar/components/StubFilterItem";
import { AnyArgFunction, StringArgFunction } from "../../../../../../model/common/CommonFunctions";
import { TimetableContext } from "../../../../Timetable";
import { Fetch } from "../../../../../../model/common/Fetch";
import { getFiltersString } from "../../../../../../common/components/list-view/utils/listFiltersUtils";

interface Props {
  classes?: any;
  filters?: CoreFilter[];
  savingFilter?: Filter;
  fetch?: Fetch;
  getSessions?: (request: SearchRequest) => void;
  saveFilter?: (filter: Filter) => void;
  setSavingFilter?: (savingFilter?: SavingFilterState) => void;
  setTimetableFilters?: (filters?: CoreFilter[]) => void;
  getTimetableSessionsDays?: (month: number, year: number) => void;
  setTimetableSearch?: StringArgFunction;
  getTimetableFilters?: AnyArgFunction;
  clearTimetableMonths?: AnyArgFunction;
  deleteTimetableFilter?: (id: number, currentMonth: Date) => void;
}

const CustomFilters = React.memo<Props>(
  ({
    filters,
    savingFilter,
    setSavingFilter,
    saveFilter,
    getTimetableFilters,
    getTimetableSessionsDays,
    setTimetableFilters,
    deleteTimetableFilter,
    getSessions,
    setTimetableSearch,
    clearTimetableMonths,
    fetch
  }) => {
    useEffect(() => {
      getTimetableFilters();
    }, [getTimetableFilters]);

    const { selectedMonth } = useContext(TimetableContext);

    const onFiltersUpdate = useCallback(
      (filter, active) => {
        clearTimetableMonths();
        const filterIndex = Number(filter.split("/")[1]);

        const updatedFilters = filters.map((f, i) => ({ ...f, active: i === filterIndex ? active : f.active }));

        const search = getFiltersString([{ filters: updatedFilters }]);

        setTimetableFilters(updatedFilters);
        setTimetableSearch(search);

        const startMonth = startOfMonth(selectedMonth);

        const endMonth = endOfMonth(addMonths(startMonth, 1));

        getSessions({ from: startMonth.toISOString(), to: endMonth.toISOString(), search });
        getTimetableSessionsDays(selectedMonth.getMonth(), selectedMonth.getFullYear());
      },
      [filters, selectedMonth]
    );

    const onDeleteFilter = useCallback(
      id => {
        deleteTimetableFilter(id, selectedMonth);
      },
      [filters, selectedMonth]
    );

    return (
      <div className={clsx("p-2", fetch.pending && "disabled")}>
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
  getSessions: (request: SearchRequest) => dispatch(findTimetableSessions(request)),
  setTimetableSearch: (search: string) => dispatch(setTimetableSearch(search)),
  saveFilter: (filter: Filter) => dispatch(saveTimetableFilter(filter)),
  setSavingFilter: (savingFilter?: SavingFilterState) => dispatch(setTimetableSavingFilter(savingFilter)),
  deleteTimetableFilter: (id: number, currentMonth: Date) => dispatch(deleteTimetableFilter(id, currentMonth)),
  setTimetableFilters: (filters?: CoreFilter[]) => dispatch(setTimetableFilters(filters)),
  getTimetableSessionsDays: (month: number, year: number) => dispatch(getTimetableSessionsDays(month, year)),
  clearTimetableMonths: () => dispatch(clearTimetableMonths()),
  getTimetableFilters: () => dispatch(getTimetableFilters())
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CustomFilters);
