import React, { useCallback, useRef, useState } from "react";
import IconButton from "@mui/material/IconButton";
import Close from "@mui/icons-material/Close";
import Search from "@mui/icons-material/Search";
import Input from "@mui/material/Input";
import debounce from "lodash.debounce";

const SidebarSearch = React.memo<any>(({ placeholder = "Filter items", setParentSearch }) => {
  const [search, setSearch] = useState("");

  const searchRef = useRef("");

  const debounseSearch = useCallback(
    debounce(() => {
      setParentSearch(searchRef.current);
    }, 500),
    []
  );

  const onChange = useCallback(e => {
    setSearch(e.target.value);
    searchRef.current = e.target.value;
    debounseSearch();
  }, []);

  const clear = useCallback(() => {
    setSearch("");
    setParentSearch("");
  }, []);

  return (
    <div className="pl-3 pr-3 pt-1">
      <Input
        value={search}
        onChange={onChange}
        placeholder={placeholder}
        startAdornment={<Search className="inputAdornmentIcon textSecondaryColor mr-1" />}
        endAdornment={
          search && (
            <IconButton className="closeAndClearButton" onClick={clear}>
              <Close className="inputAdornmentIcon" />
            </IconButton>
          )
        }
        className="w-100"
      />
    </div>
  );
});

export default SidebarSearch;
