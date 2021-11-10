/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import Close from "@mui/icons-material/Close";
import Input from "@mui/material/Input";
import EditInPlaceQuerySelect from "../../formFields/EditInPlaceQuerySelect";
import { mapSelectItems, stubFunction } from "../../../../utils/common";
import EditInPlaceField from "../../formFields/EditInPlaceField";

const getAqlLabel = entity => {
  switch (entity) {
    case "CourseClass":
      return "class";
    default:
      return entity.toLowerCase();
  }
};

const mapAqlEntitiesItems = entity => {
  switch (entity) {
    case "CourseClass":
      return {
        value: entity,
        label: "Class"
      };
    default:
      return mapSelectItems(entity);
  }
};

const SearchInput = React.memo<any>(props => {
  const {
    classes,
    searchExpression,
    searchPlaceholder,
    aqlPlaceholderPrefix = "Find",
    searchValuesToShow,
    onSearchChange,
    onAqlSearchChange,
    onSearchEscape,
    onBlur,
    onAddEvent,
    onAqlSearchClear,
    inputRef,
    className,
    onFocus,
    autoFocus,
    aqlEntity,
    aqlEntities,
    validateAql,
    isValidAqlQuery,
    aqlComponentRef,
    searchTags,
    searchType,
    disableAddAll,
    setSelectedEntity,
    toggleSearch
  } = props;

  const multipleAql = aqlEntities && aqlEntities.length > 1;

  const InputAdornment = (
    <>
      {searchValuesToShow.length > 0 && !disableAddAll && (
        <Button size="small" className={classes.button} onClick={onAddEvent}>
          Add all
        </Button>
      )}
      {(searchExpression || searchType === "immediate") && (
        <IconButton
          className="closeAndClearButton"
          onClick={e => {
            if (multipleAql) {
              onAqlSearchClear();
            } else {
              toggleSearch(e);
            }
        }}
        >
          <Close className="inputAdornmentIcon" />
        </IconButton>
      )}
    </>
  );

  const entityItems = useMemo(() => (aqlEntities && aqlEntities.length > 1 ? aqlEntities.map(mapAqlEntitiesItems) : []), [aqlEntities]);

  return aqlEntity ? (
    <div>
      {aqlEntities && aqlEntities.length > 1
        && (
        <EditInPlaceField
          className="mt-2"
          label="Entity"
          meta={{}}
          input={{
            onChange: value => setSelectedEntity(value),
            onFocus: stubFunction,
            onBlur: stubFunction,
            value: aqlEntity
          }}
          items={entityItems}
          select
        />
      )}

      <EditInPlaceQuerySelect
        inline
        tags={searchTags}
        filterTags={[]}
        ref={aqlComponentRef}
        setInputNode={node => {
          inputRef.current = node;
        }}
        rootEntity={aqlEntity}
        className={className}
        placeholder={searchPlaceholder || (aqlEntity ? `${aqlPlaceholderPrefix} ${getAqlLabel(aqlEntity)}` : null)}
        input={{
          value: searchExpression,
          meta: {
            invalid: !isValidAqlQuery,
            error: !isValidAqlQuery && "Expression is invalid"
          }
        }}
        performSearch={onAqlSearchChange}
        onValidateQuery={validateAql}
        onFocus={onFocus}
        onBlur={onBlur}
        endAdornment={InputAdornment}
        menuHeight={300}
      />
    </div>
  )
  : (
    <Input
      inputRef={inputRef}
      value={searchExpression}
      placeholder={searchPlaceholder}
      className={className}
      onChange={onSearchChange}
      onKeyDown={onSearchEscape}
      onBlur={onBlur}
      onFocus={onFocus}
      endAdornment={InputAdornment}
      autoFocus={searchExpression ? false : autoFocus}
      fullWidth
    />
  );
});

export default SearchInput;
