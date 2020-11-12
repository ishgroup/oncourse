/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useMemo, useRef, useState
} from "react";
import Button from "@material-ui/core/Button";
import IconButton from "@material-ui/core/IconButton";
import Close from "@material-ui/icons/Close";
import Input from "@material-ui/core/Input";
import clsx from "clsx";
import EditInPlaceQuerySelect from "../../form-fields/EditInPlaceQuerySelect";

const DoubleAqlSearchInput = React.memo<any>(
  ({
    classes,
    searchValuesToShow,
    onAddEvent,
    aqlComponentRef,
    additionalAqlComponentRef,
    inputRef,
    aqlEntity,
    className,
    searchPlaceholder,
    searchExpression,
    isValidAqlQuery,
    onAqlSearchChange,
    validateAql,
    onFocus,
    onBlur,
    additionalSearchPlaceholder,
    additionalSearchExpression,
    additionalAqlEntity,
    clearDoubleSearch,
    searchTags,
    additionalSearchTags,
    disableAddAll
  }) => {
    const [firstExpanded, setFirsExpanded] = useState(false);
    const [secondExpanded, setSecondExpanded] = useState(false);

    const firstInputRef = useRef<any>();
    const secondInputRef = useRef<any>();
    const isAdornmentHovered = useRef<boolean>(false);

    const onAdornmentOver = useCallback(() => {
      isAdornmentHovered.current = true;
    }, []);

    const onAdornmentOut = useCallback(() => {
      isAdornmentHovered.current = false;
    }, []);

    const firstOnFocusHandler = useCallback(() => {
      setFirsExpanded(true);
      inputRef.current = firstInputRef.current;
      onFocus();
    }, [onFocus]);

    const firstOnBlurHandler = useCallback(() => {
      if (isAdornmentHovered.current) {
        return;
      }

      setFirsExpanded(false);
      onBlur();
    }, [onBlur, isAdornmentHovered.current]);

    const secondOnFocusHandler = useCallback(() => {
      inputRef.current = secondInputRef.current;
      setSecondExpanded(true);
      onFocus();
    }, [onFocus]);

    const secondOnBlurHandler = useCallback(() => {
      setSecondExpanded(false);
      onBlur();
    }, [onBlur]);

    const searchHandler = useCallback(() => onAqlSearchChange(secondExpanded), [secondExpanded]);

    const onFirsSearchClick = useCallback(() => {
      clearDoubleSearch();

      if (isAdornmentHovered.current) {
        isAdornmentHovered.current = false;

        firstOnBlurHandler();
      }
    }, [isAdornmentHovered.current]);

    const AddButton = useMemo(
      () =>
        searchValuesToShow.length > 0 && disableAddAll && (
          <Button size="small" className={classes.button} onClick={onAddEvent}>
            Add all
          </Button>
        ),
      [searchValuesToShow.length, disableAddAll]
    );

    const FirstClearButton = useMemo(
      () =>
        searchExpression && (
          <IconButton
            className="closeAndClearButton"
            onClick={onFirsSearchClick}
            onMouseEnter={onAdornmentOver}
            onMouseLeave={onAdornmentOut}
          >
            <Close className="inputAdornmentIcon" />
          </IconButton>
        ),

      [searchExpression]
    );

    const SecondClearButton = useMemo(
      () =>
        additionalSearchExpression && (
          <IconButton className="closeAndClearButton" onClick={() => clearDoubleSearch(true)}>
            <Close className="inputAdornmentIcon" />
          </IconButton>
        ),

      [additionalSearchExpression]
    );

    return (
      <div className="centeredFlex">
        <div className={clsx(classes.expandableInput, secondExpanded ? classes.collapsed : "pr-2")}>
          <EditInPlaceQuerySelect
            inline
            tags={searchTags}
            filterTags={[]}
            ref={aqlComponentRef}
            setInputNode={node => (firstInputRef.current = node)}
            rootEntity={aqlEntity}
            className={className}
            placeholder={searchPlaceholder}
            input={{
              value: searchExpression,
              meta: {
                invalid: !isValidAqlQuery,
                error: !isValidAqlQuery && "Expression is invalid"
              }
            }}
            performSearch={searchHandler}
            onValidateQuery={validateAql}
            onFocus={firstOnFocusHandler}
            onBlur={firstOnBlurHandler}
            endAdornment={FirstClearButton}
            menuHeight={300}
          />
        </div>
        <div className={clsx(classes.expandableInput, firstExpanded && classes.collapsed)}>
          <EditInPlaceQuerySelect
            inline
            tags={additionalSearchTags}
            filterTags={[]}
            ref={additionalAqlComponentRef}
            setInputNode={node => (secondInputRef.current = node)}
            rootEntity={additionalAqlEntity}
            className={className}
            placeholder={additionalSearchPlaceholder}
            input={{
              value: additionalSearchExpression,
              meta: {
                invalid: !isValidAqlQuery,
                error: !isValidAqlQuery && "Expression is invalid"
              }
            }}
            performSearch={searchHandler}
            onValidateQuery={validateAql}
            onFocus={secondOnFocusHandler}
            onBlur={secondOnBlurHandler}
            endAdornment={SecondClearButton}
            menuHeight={300}
          />
        </div>
        <div>{AddButton}</div>
      </div>
    );
  }
);

const SearchInput = React.memo<any>(props => {
  const {
    classes,
    searchExpression,
    searchPlaceholder,
    searchValuesToShow,
    onSearchChange,
    onAqlSearchChange,
    onSearchEscape,
    onBlur,
    onAddEvent,
    toggleSearch,
    inputRef,
    className,
    onFocus,
    autoFocus,
    aqlEntity,
    validateAql,
    isValidAqlQuery,
    aqlComponentRef,
    additionalAqlEntity,
    searchTags,
    searchType,
    disableAddAll
  } = props;

  const InputAdornment = (
    <>
      {searchValuesToShow.length > 0 && !disableAddAll && (
        <Button size="small" className={classes.button} onClick={onAddEvent}>
          Add all
        </Button>
      )}
      {(searchExpression || searchType === "immediate") && (
        <IconButton className="closeAndClearButton" onClick={toggleSearch}>
          <Close className="inputAdornmentIcon" />
        </IconButton>
      )}
    </>
  );

  if (additionalAqlEntity) {
    return <DoubleAqlSearchInput {...props} />;
  }

  return aqlEntity ? (
    <EditInPlaceQuerySelect
      inline
      tags={searchTags}
      filterTags={[]}
      ref={aqlComponentRef}
      setInputNode={node => (inputRef.current = node)}
      rootEntity={aqlEntity}
      className={className}
      placeholder={searchPlaceholder}
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
  ) : (
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
