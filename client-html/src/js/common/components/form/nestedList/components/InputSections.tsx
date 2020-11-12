/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Typography } from "@material-ui/core";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton";
import { AddCircle } from "@material-ui/icons";
import Collapse from "@material-ui/core/Collapse/Collapse";
import { Switch } from "../../form-fields/Switch";
import SearchInput from "./SearchInput";

export const InputSection = React.memo<any>(props => {
  const {
    classes,
    searchEnabled,
    title,
    searchExpression,
    searchPlaceholder,
    searchValuesToShow,
    onSearchChange,
    onAqlSearchChange,
    onSearchEscape,
    onBlur,
    onFocus,
    onAddEvent,
    inputRef,
    toggleSearch,
    hideAddButton,
    formError,
    disabled,
    aqlEntity,
    additionalAqlEntity,
    aqlComponentRef,
    validateAql,
    isValidAqlQuery,
    additionalSearchPlaceholder,
    additionalSearchExpression,
    additionalAqlComponentRef,
    clearDoubleSearch,
    searchTags,
    additionalSearchTags,
    searchType,
    titleCaption,
    secondaryHeading,
    disableAddAll
  } = props;

  return (
    <>
      <div className={clsx(!secondaryHeading && classes.root__search)}>
        <div className="centeredFlex">
          <Typography className={clsx(secondaryHeading
            ? "secondaryHeading"
            : "heading pt-1 pb-1",
            { "errorColor": formError })}
          >
            {title}
          </Typography>
          <IconButton
            className={clsx(
              "addButtonColor",
              secondaryHeading && "p-0 ml-1",
              (searchEnabled || hideAddButton) && "invisible"
)}
            onClick={toggleSearch}
            disabled={disabled}
          >
            <AddCircle className="inherit" />
          </IconButton>
        </div>

        {titleCaption && (
          <Typography variant="caption" color="textSecondary" component="div" gutterBottom>
            {titleCaption}
          </Typography>
        )}

        <Collapse in={searchEnabled && !disabled} unmountOnExit mountOnEnter>
          <SearchInput
            searchExpression={searchExpression}
            searchPlaceholder={searchPlaceholder}
            searchValuesToShow={searchValuesToShow}
            onSearchChange={onSearchChange}
            onAqlSearchChange={onAqlSearchChange}
            onSearchEscape={onSearchEscape}
            onFocus={onFocus}
            onBlur={onBlur}
            onAddEvent={onAddEvent}
            inputRef={inputRef}
            classes={classes}
            toggleSearch={toggleSearch}
            aqlEntity={aqlEntity}
            validateAql={validateAql}
            isValidAqlQuery={isValidAqlQuery}
            aqlComponentRef={aqlComponentRef}
            additionalAqlEntity={additionalAqlEntity}
            additionalSearchPlaceholder={additionalSearchPlaceholder}
            additionalSearchExpression={additionalSearchExpression}
            additionalAqlComponentRef={additionalAqlComponentRef}
            clearDoubleSearch={clearDoubleSearch}
            className={classes.inputMargin}
            searchTags={searchTags}
            additionalSearchTags={additionalSearchTags}
            searchType={searchType}
            disableAddAll={disableAddAll}
            autoFocus
          />
        </Collapse>
      </div>

      {formError && (
        <Typography variant="subtitle2" color="error" paragraph>
          {formError}
        </Typography>
      )}
    </>
  );
});

export const InputSectionWithToggle = React.memo<any>(props => {
  const {
    classes,
    title,
    searchExpression,
    searchPlaceholder,
    searchValuesToShow,
    onSearchChange,
    onSearchEscape,
    onBlur,
    onFocus,
    onAddEvent,
    toggleSearch,
    inputRef,
    titleCaption,
    toggleEnabled,
    onSwitchToggle,
    formError,
    disabled,
    aqlEntity,
    additionalAqlEntity,
    aqlComponentRef,
    onAqlSearchChange,
    validateAql,
    isValidAqlQuery,
    additionalSearchPlaceholder,
    additionalSearchExpression,
    additionalAqlComponentRef,
    clearDoubleSearch,
    searchTags,
    additionalSearchTags,
    secondaryHeading,
    disableAddAll
  } = props;

  return (
    <div className={disabled ? "disabled" : undefined}>
      <div className="centeredFlex">
        <Typography className={clsx(secondaryHeading
          ? "secondaryHeading"
          : "heading pt-1 pb-1",
          { "errorColor": formError })}
        >
          {title}
        </Typography>
        <Switch onChange={onSwitchToggle} checked={toggleEnabled} disabled={disabled} />
      </div>

      {formError && (
        <Typography variant="subtitle2" color="error" paragraph>
          {formError}
        </Typography>
      )}

      <Collapse in={toggleEnabled && !disabled} unmountOnExit mountOnEnter>
        <div className={clsx(classes.topOffset, "centeredFlex")}>
          <Typography variant="caption" color="textSecondary">
            {titleCaption}
          </Typography>
        </div>

        <div className={clsx(!secondaryHeading && classes.root__search)}>
          <SearchInput
            searchExpression={searchExpression}
            searchPlaceholder={searchPlaceholder}
            searchValuesToShow={searchValuesToShow}
            onSearchChange={onSearchChange}
            onAqlSearchChange={onAqlSearchChange}
            onSearchEscape={onSearchEscape}
            onAddEvent={onAddEvent}
            inputRef={inputRef}
            classes={classes}
            toggleSearch={toggleSearch}
            onFocus={onFocus}
            onBlur={onBlur}
            className={`w-100 ${classes.topOffset}`}
            aqlEntity={aqlEntity}
            validateAql={validateAql}
            isValidAqlQuery={isValidAqlQuery}
            aqlComponentRef={aqlComponentRef}
            additionalAqlEntity={additionalAqlEntity}
            additionalSearchPlaceholder={additionalSearchPlaceholder}
            additionalSearchExpression={additionalSearchExpression}
            additionalAqlComponentRef={additionalAqlComponentRef}
            clearDoubleSearch={clearDoubleSearch}
            searchTags={searchTags}
            additionalSearchTags={additionalSearchTags}
            disableAddAll={disableAddAll}
          />
        </div>
      </Collapse>
    </div>
  );
});
