/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Filter } from '@api/model';
import { Theme } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import { darken } from '@mui/material/styles';
import Toolbar from '@mui/material/Toolbar';
import { AnyArgFunction, StringArgFunction } from 'ish-ui';
import React, { useCallback, useMemo, useRef } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import {
  SearchInputBase
} from '../../../../common/components/list-view/components/bottom-app-bar/components/SearchInput';
import { APP_BAR_HEIGHT, SIMPLE_SEARCH_REGEX } from '../../../../constants/Config';
import { Fetch } from '../../../../model/common/Fetch';
import { FilterGroup, SavingFilterState } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import { clearTimetableMonths, setTimetableSavingFilter, setTimetableSearch } from '../../actions';

interface Props {
  classes?: any;
  fetch?: Fetch;
  filters?: Filter[];
  setTimetableSearch?: StringArgFunction;
  clearTimetableMonths?: AnyArgFunction;
  setSavingFilter?: (savingFilter?: SavingFilterState) => void;
  searchUrlParameter?: boolean;
  savingFilter?: SavingFilterState;
  search?: string;
  searchServerError?: boolean;
}

const styles = ({
 palette, spacing, shadows, shape 
}: Theme) =>
  ({
    root: {
      boxShadow: shadows[0],
      backgroundColor: palette.background.default
    },
    field: {
      height: 36,
      borderRadius: shape.borderRadius,
      display: "flex",
      alignItems: "center",
      "&:focus": {
        borderRadius: shape.borderRadius
      }
    },
    toolbar: {
      backgroundColor: palette.mode === "light" ? palette.primary.main : darken(palette.background.default, 0.4),
      color: palette.text.primary,
      height: `${APP_BAR_HEIGHT}px`,
      bottom: 0,
      width: "100%",
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      padding: spacing(2),
      position: "relative",
      zIndex: 1
    }
  });

const SearchBar = React.memo<Props>(
  ({
    classes,
    search,
    setSavingFilter,
    setTimetableSearch,
    clearTimetableMonths,
    savingFilter,
    filters,
    searchServerError
  }) => {

    const searchNode = useRef(null);

    const searchComponent = useRef(null);

    const filterGroups = useMemo(() => (filters.length ? [{ filters } as FilterGroup] : []), [filters]);

    const onQuerySearch = useCallback(
      searchString => {
        if (searchString.match(SIMPLE_SEARCH_REGEX)) {
          return;
        }

        clearTimetableMonths();
        setTimetableSearch(searchString);
      },
      []
    );

    return (
      <AppBar position="static">
        <Toolbar className={classes.toolbar}>
          <SearchInputBase
            querySearch
            queryComponentRef={searchComponent}
            queryInputRef={searchNode}
            rootEntity="Session"
            userAQLSearch={search}
            savingFilter={savingFilter}
            tags={[]}
            filterGroups={filterGroups}
            setListUserAQLSearch={setTimetableSearch}
            onQuerySearch={onQuerySearch}
            setListSavingFilter={setSavingFilter}
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
  filters: state.timetable.filters,
  search: state.timetable.search,
  savingFilter: state.timetable.savingFilter,
  searchServerError: state.timetable.searchError
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setTimetableSearch: (search: string) => dispatch(setTimetableSearch(search)),
  setSavingFilter: (savingFilter?: SavingFilterState) => dispatch(setTimetableSavingFilter(savingFilter)),
  clearTimetableMonths: () => dispatch(clearTimetableMonths())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(SearchBar, styles));