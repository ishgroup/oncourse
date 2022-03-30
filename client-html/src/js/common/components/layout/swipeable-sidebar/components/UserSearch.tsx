/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import debounce from "lodash.debounce";
import clsx from "clsx";
import { createStyles, withStyles } from "@mui/styles";
import Input from "@mui/material/Input";
import Search from "@mui/icons-material/Search";
import IconButton from "@mui/material/IconButton";
import Close from "@mui/icons-material/Close";
import { AppTheme } from "../../../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
  inputRoot: {
    "&::before": {
      borderBottom: "2px solid #bfbfbf !important",
    }
  },
  input: {
    padding: "13px 0",
  },
  inputStartAdornment: {
    color: theme.palette.primary.main,
  },
});

const UserSearch: React.FC<any> = props => {
  const {
    classes,
    getSearchResults,
    placeholder = "Find anything...",
    setFocusOnSearchInput,
    focusOnSearchInput
  } = props;

  const [userSearch, setUserSearch] = React.useState("");
  const searchRef = React.useRef("");

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
    setFocusOnSearchInput(false);
  }, []);

  const onFocus = React.useCallback(() => {
   setFocusOnSearchInput(true);
  }, []);

  const onBlur = React.useCallback(() => {
    setFocusOnSearchInput(false);
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
            focusOnSearchInput ? classes.inputStartAdornment : "textGreyColor700")}
          />
        )}
        endAdornment={
          focusOnSearchInput && (
            <IconButton className="closeAndClearButton" onClick={clear}>
              <Close className="inputAdornmentIcon" />
            </IconButton>
          )
        }
        className="w-100"
        classes={{ root: classes.inputRoot, input: classes.input }}
        onFocus={onFocus}
        onBlur={onBlur}
      />
    </div>
  );
};

export default withStyles(styles)(UserSearch);
