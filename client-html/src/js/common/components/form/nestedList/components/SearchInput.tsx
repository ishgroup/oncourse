/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Close from '@mui/icons-material/Close';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Input from '@mui/material/Input';
import $t from '@t';
import { EditInPlaceSearchSelect, mapSelectItems, stubFunction } from 'ish-ui';
import React, { useMemo } from 'react';
import EditInPlaceQuerySelect from '../../formFields/EditInPlaceQuerySelect';

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

const SearchInput = React.memo<{
  classes?,
  searchExpression?,
  searchPlaceholder?,
  aqlPlaceholderPrefix?,
  searchValuesToShow?,
  onSearchChange?,
  onAqlSearchChange?,
  onSearchEscape?,
  onAddEvent?,
  onAqlSearchClear?,
  inputRef?,
  className?,
  onFocus?,
  autoFocus?,
  aqlEntity?,
  aqlEntities?,
  aqlQueryError?,
  aqlComponentRef?,
  searchTags?,
  searchType?,
  disableAddAll?,
  setSelectedEntity?,
  toggleSearch?
}>(props => {
  const {
    classes,
    searchExpression,
    searchPlaceholder,
    aqlPlaceholderPrefix = "Find",
    searchValuesToShow,
    onSearchChange,
    onAqlSearchChange,
    onSearchEscape,
    onAddEvent,
    onAqlSearchClear,
    inputRef,
    className,
    onFocus,
    autoFocus,
    aqlEntity,
    aqlEntities,
    aqlQueryError,
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
          {$t('add_all')}
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
          <Close className="inputAdornmentIcon"/>
        </IconButton>
      )}
    </>
  );

  const entityItems = useMemo(() => (aqlEntities && aqlEntities.length > 1 ? aqlEntities.map(mapAqlEntitiesItems) : []), [aqlEntities]);

  return aqlEntity ? (
      <div>
        {aqlEntities && aqlEntities.length > 1
          && (
            <EditInPlaceSearchSelect
              className="mt-2 mb-2"
              label={$t('entity')}
              meta={{}}
              input={{
                onChange: value => setSelectedEntity(value),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: aqlEntity
              }}
              items={entityItems}
            />
          )}

        <EditInPlaceQuerySelect
          inline
          tagSuggestions={searchTags}
          filterTags={[]}
          ref={aqlComponentRef}
          setInputNode={node => {
            inputRef.current = node;
          }}
          rootEntity={aqlEntity}
          className={className}
          placeholder={searchPlaceholder || (aqlEntity ? `${aqlPlaceholderPrefix} ${getAqlLabel(aqlEntity)}` : null)}
          input={{
            value: searchExpression
          }}
          meta={{
            invalid: aqlQueryError,
            error: aqlQueryError && "Expression is invalid"
          }}
          performSearch={onAqlSearchChange}
          onFocus={onFocus}
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
        onFocus={onFocus}
        endAdornment={InputAdornment}
        autoFocus={searchExpression ? false : autoFocus}
      />
    );
});

export default SearchInput;
