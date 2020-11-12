/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useState, useCallback, useRef, useMemo, useContext } from "react";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import { withStyles, darken, createStyles } from "@material-ui/core/styles";
import { Theme } from "@material-ui/core";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import { SearchInputBase } from "../../../../common/components/list-view/components/bottom-app-bar/components/SearchInput";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { Fetch } from "../../../../model/common/Fetch";
import { State } from "../../../../reducers/state";
import { APP_BAR_HEIGHT, SIMPLE_SEARCH_REGEX } from "../../../../constants/Config";
import {
  clearTimetableMonths,
  findTimetableSessions,
  getTimetableSessionsDays,
  setTimetableFilters,
  setTimetableSavingFilter,
  setTimetableSearch,
  setTimetableUsersSearch
} from "../../actions";
import { AnyArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import { FilterGroup, SavingFilterState } from "../../../../model/common/ListView";
import { Filter, SearchRequest } from "@api/model";
import { TimetableContext } from "../../Timetable";
import { addMonths, endOfMonth, startOfMonth } from "date-fns";
import { stubFunction } from "../../../../common/utils/common";

interface Props {
  classes?: any;
  fetch?: Fetch;
  filters?: Filter[];
  setUsersSearch?: StringArgFunction;
  setTimetableSearch?: StringArgFunction;
  clearTimetableMonths?: AnyArgFunction;
  getSessions: (request: SearchRequest) => void;
  setSavingFilter?: (savingFilter?: SavingFilterState) => void;
  setTimetableFilters?: (filterGroups?: FilterGroup[]) => void;
  getTimetableSessionsDays?: (month: number, year: number) => void;
  searchUrlParameter?: boolean;
  savingFilter?: SavingFilterState;
  usersSearch?: string;
  searchServerError?: boolean;
}
interface SearchBarState {
  searchType: { key: string; entity: string };
}

const styles = ({ palette, spacing, shadows, shape }: Theme) =>
  createStyles({
    root: {
      boxShadow: shadows[0],
      backgroundColor: palette.background.default
    },
    field: {
      padding: spacing(0, 1),
      height: 36,
      borderRadius: shape.borderRadius,
      display: "flex",
      alignItems: "center",
      "&:focus": {
        borderRadius: shape.borderRadius
      }
    },
    toolbar: {
      backgroundColor: palette.type === "light" ? palette.primary.main : darken(palette.background.default, 0.4),
      color: palette.text.primary,
      height: `${APP_BAR_HEIGHT}px`,
      bottom: 0,
      width: "100%",
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      padding: spacing(2),
      position: "relative"
    },
    select: {
      paddingRight: spacing(3)
    }
  });

const selectValues = [
  { key: "session.", entity: "Session" },
  { key: "session.tutor.", entity: "Tutor" },
  { key: "session.tutor.contact.", entity: "Contact" },
  { key: "session.courseClass.", entity: "CourseClass" },
  { key: "session.courseClass.course.", entity: "Course" },
  { key: "session.courseClass.enrolments.student.", entity: "Student" },
  { key: "session.room.", entity: "Room" },
  { key: "session.room.site.", entity: "Site" }
];

const SearchBarSelect = React.memo<any>(({ classes, searchType, selectHandle }) => (
  <FormControl className={classes.formControl}>
    <Select
      name="entities"
      IconComponent={KeyboardArrowDown}
      value={searchType.key}
      onChange={selectHandle}
      disableUnderline
      displayEmpty
      classes={{
        root: `${classes.field} ${classes.select}`
      }}
    >
      {selectValues.map((el, id) => (
        <MenuItem key={id} value={el.key}>
          {el.key}
        </MenuItem>
      ))}
    </Select>
  </FormControl>
));

const SearchBar = React.memo<Props>(
  ({
    classes,
    fetch,
    usersSearch,
    getSessions,
    getTimetableSessionsDays,
    setTimetableFilters,
    setUsersSearch,
    setSavingFilter,
    setTimetableSearch,
    clearTimetableMonths,
    savingFilter,
    filters,
    searchServerError
  }) => {
    const { selectedMonth } = useContext(TimetableContext);

    const [searchType, setSearchType] = useState<SearchBarState["searchType"]>(selectValues[0]);

    const searchNode = useRef(null);

    const searchComponent = useRef(null);

    const selectHandle = useCallback(
      e => {
        setSearchType(selectValues.find(f => f.key === e.target.value));
        setUsersSearch("");

        searchComponent.current.reset();

        setTimeout(() => {
          searchNode.current.focus();
        }, 200);
      },
      [searchNode.current, searchComponent.current]
    );

    const filterGroups = useMemo(() => (filters.length ? [{ filters } as FilterGroup] : []), [filters]);

    const onQuerySearch = useCallback(
      searchString => {
        if (searchString.match(SIMPLE_SEARCH_REGEX)) {
          return;
        }

        clearTimetableMonths();

        const search = searchString ? searchType.key.replace("session.", "") + searchString : "";

        setTimetableSearch(search);

        const startMonth = startOfMonth(selectedMonth);

        const endMonth = endOfMonth(addMonths(startMonth, 1));

        getSessions({ from: startMonth.toISOString(), to: endMonth.toISOString(), search });
        getTimetableSessionsDays(selectedMonth.getMonth(), selectedMonth.getFullYear());
      },
      [selectedMonth, searchType]
    );

    const setSavingFilterCustom = useCallback(
      filter => {
        filter.aqlSearch = searchType.key.replace("session.", "") + filter.aqlSearch;
        setSavingFilter(filter);
      },
      [searchType]
    );

    return (
      <AppBar position="static">
        <Toolbar className={classes.toolbar}>
          <SearchInputBase
            querySearch
            queryComponentRef={searchComponent}
            queryInputRef={searchNode}
            startAdornment={<SearchBarSelect classes={classes} searchType={searchType} selectHandle={selectHandle} />}
            rootEntity={searchType.entity}
            userAQLSearch={usersSearch}
            savingFilter={savingFilter}
            tags={[]}
            filterGroups={filterGroups}
            setListUserAQLSearch={setUsersSearch}
            onQuerySearch={onQuerySearch}
            setListMenuTags={stubFunction}
            setFilterGroups={setTimetableFilters}
            setListSavingFilter={setSavingFilterCustom}
            searchServerError={searchServerError}
            placeholder="Find..."
            alwaysExpanded
          />
        </Toolbar>
      </AppBar>
    );
  }
);

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  filters: state.timetable.filters,
  usersSearch: state.timetable.usersSearch,
  savingFilter: state.timetable.savingFilter,
  searchServerError: state.timetable.searchError
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSessions: (request: SearchRequest) => dispatch(findTimetableSessions(request)),
  setUsersSearch: (usersSearch: string) => dispatch(setTimetableUsersSearch(usersSearch)),
  setTimetableSearch: (search: string) => dispatch(setTimetableSearch(search)),
  setSavingFilter: (savingFilter?: SavingFilterState) => dispatch(setTimetableSavingFilter(savingFilter)),
  setTimetableFilters: (filterGroups?: FilterGroup[]) => {
    if (filterGroups.length) {
      dispatch(setTimetableFilters(filterGroups[0].filters));
    }
  },
  clearTimetableMonths: () => dispatch(clearTimetableMonths()),
  getTimetableSessionsDays: (month: number, year: number) => dispatch(getTimetableSessionsDays(month, year))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(SearchBar));
