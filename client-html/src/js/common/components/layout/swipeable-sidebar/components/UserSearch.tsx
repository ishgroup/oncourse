/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Close from '@mui/icons-material/Close';
import Search from '@mui/icons-material/Search';
import IconButton from '@mui/material/IconButton';
import Input from '@mui/material/Input';
import clsx from 'clsx';
import { BooleanArgFunction, makeAppStyles, StringArgFunction } from 'ish-ui';
import debounce from 'lodash.debounce';
import React from 'react';

const useStyles = makeAppStyles()(theme => ({
  inputRoot: {
    "&::before": {
      borderBottom: "2px solid #bfbfbf",
      borderWidth: "2px"
    }
  },
  input: {
    padding: "13px 0",
  },
  inputStartAdornment: {
    color: theme.palette.primary.main,
  },
}));

interface Props {
  getSearchResults: StringArgFunction;
  placeholder?: string;
  setFocusOnSearchInput?: BooleanArgFunction;
}

const UserSearch = ({
                      getSearchResults,
                      placeholder = "Find anything...",
                      setFocusOnSearchInput
                    }: Props) => {
  const [userSearch, setUserSearch] = React.useState("");
  const [focused, setFocused] = React.useState(false);
  const searchRef = React.useRef("");

  const { classes } = useStyles();

  const debounseSearch = React.useCallback(
    debounce(() => {
      getSearchResults(searchRef.current);
    }, 600),
    []
  );

  const onUserSearchChange = React.useCallback(e => {
    setUserSearch(e.target.value);
    searchRef.current = e.target.value;
    debounseSearch();
  }, []);

  const clear = React.useCallback(() => {
    setUserSearch("");
    getSearchResults("");
    setFocusOnSearchInput && setFocusOnSearchInput(false);
    setFocused(false);
  }, []);

  const onFocus = React.useCallback(() => {
    setFocusOnSearchInput && setFocusOnSearchInput(true);
    setFocused(true);
  }, []);

  const onBlur = React.useCallback(() => {
    setFocusOnSearchInput && setFocusOnSearchInput(false);
    setFocused(false);
  }, []);

  return (
    <div className="pl-2 pr-2 pt-2 mb-1">
      <Input
        name="sidebar_user_search"
        value={userSearch}
        onChange={onUserSearchChange}
        placeholder={placeholder}
        startAdornment={(
          <Search className={clsx("inputAdornmentIcon mr-1 fs3",
            focused ? classes.inputStartAdornment : "textGreyColor700")}
          />
        )}
        endAdornment={
          (
            <IconButton className={clsx("closeAndClearButton", !userSearch && "invisible")} onClick={clear}>
              <Close className="inputAdornmentIcon"/>
            </IconButton>
          )
        }
        className="w-100"
        classes={{root: classes.inputRoot, input: classes.input}}
        onFocus={onFocus}
        onBlur={onBlur}
      />
    </div>
  );
};

export default UserSearch;
