/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton";
import Collapse from "@material-ui/core/Collapse/Collapse";
import Typography from "@material-ui/core/Typography";
import AddCircle from "@material-ui/icons/AddCircle";
import Close from "@material-ui/icons/Close";
import SearchInput from "./SearchInput";
import { Switch } from "../../form-fields/Switch";

export const InputSection = React.memo<any>(props => {
  const {
    classes,
    title,
    searchExpression,
    searchPlaceholder,
    aqlPlaceholderPrefix,
    searchValuesToShow,
    onSearchChange,
    onAqlSearchChange,
    onSearchEscape,
    onFocus,
    onBlur,
    onAddEvent,
    toggleSearch,
    validateAql,
    inputRef,
    aqlComponentRef,
    titleCaption,
    formError,
    disabled,
    setSelectedEntity,
    aqlEntity,
    isValidAqlQuery,
    searchTags,
    secondaryHeading,
    disableAddAll,
    searchEnabled,
    hideAddButton,
    aqlEntities,
    onAqlSearchClear
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
          {searchEnabled
            ? (
              <IconButton
                className={clsx(
                secondaryHeading && "p-0 ml-1",
                  hideAddButton && "invisible"
              )}
                onClick={toggleSearch}
                disabled={disabled}
              >
                <Close className="inherit" />
              </IconButton>
            )
            : (
              <IconButton
                className={clsx(
                  "addButtonColor",
                  secondaryHeading && "p-0 ml-1",
                  hideAddButton && "invisible"
                )}
                onClick={toggleSearch}
                disabled={disabled}
              >
                <AddCircle className="inherit" />
              </IconButton>
            )}
        </div>

        {titleCaption && (
          <Typography variant="caption" color="textSecondary" component="div" gutterBottom>
            {titleCaption}
          </Typography>
        )}

        <Collapse in={searchEnabled && !disabled} unmountOnExit mountOnEnter>
          <SearchInput
            onAqlSearchClear={onAqlSearchClear}
            searchExpression={searchExpression}
            searchPlaceholder={searchPlaceholder}
            aqlPlaceholderPrefix={aqlPlaceholderPrefix}
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
            className={classes.inputMargin}
            searchTags={searchTags}
            disableAddAll={disableAddAll}
            setSelectedEntity={setSelectedEntity}
            aqlEntities={aqlEntities}
            autoFocus
          />
        </Collapse>
      </div>

      {formError && (
        <Typography className="shakingError" variant="subtitle2" color="error" paragraph>
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
    aqlPlaceholderPrefix,
    searchValuesToShow,
    onSearchChange,
    onAqlSearchChange,
    onSearchEscape,
    onFocus,
    onBlur,
    onAddEvent,
    toggleSearch,
    onSwitchToggle,
    validateAql,
    inputRef,
    aqlComponentRef,
    titleCaption,
    toggleEnabled,
    formError,
    disabled,
    setSelectedEntity,
    aqlEntity,
    isValidAqlQuery,
    searchTags,
    secondaryHeading,
    disableAddAll,
    aqlEntities,
    onAqlSearchClear
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
        <Typography className="shakingError" variant="subtitle2" color="error" paragraph>
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
            onAqlSearchClear={onAqlSearchClear}
            searchExpression={searchExpression}
            searchPlaceholder={searchPlaceholder}
            aqlPlaceholderPrefix={aqlPlaceholderPrefix}
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
            className={clsx("w-100", classes.topOffset)}
            aqlEntity={aqlEntity}
            setSelectedEntity={setSelectedEntity}
            validateAql={validateAql}
            isValidAqlQuery={isValidAqlQuery}
            aqlComponentRef={aqlComponentRef}
            searchTags={searchTags}
            disableAddAll={disableAddAll}
            aqlEntities={aqlEntities}
          />
        </div>
      </Collapse>
    </div>
  );
});
