/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import debounce from "lodash.debounce";
import Input from "@material-ui/core/Input";
import Search from "@material-ui/icons/Search";
import IconButton from "@material-ui/core/IconButton";
import Close from "@material-ui/icons/Close";

const UserSearch: React.FC<any> = ({ getSearchResults, placeholder = "Find anything..." }) => {
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
  }, []);

  return (
    <div className="pl-2 pr-2 pt-2 mb-1">
      <Input
        name="sidebar_user_search"
        value={userSearch}
        onChange={onUserSearchChange}
        placeholder={placeholder}
        startAdornment={<Search className="inputAdornmentIcon textSecondaryColor mr-1" />}
        endAdornment={
          userSearch && (
            <IconButton className="closeAndClearButton" onClick={clear}>
              <Close className="inputAdornmentIcon" />
            </IconButton>
          )
        }
        className="w-100"
      />
    </div>
  );
};

export default UserSearch;
