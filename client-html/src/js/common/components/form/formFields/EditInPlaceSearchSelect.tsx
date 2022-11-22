/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import CircularProgress from "@mui/material/CircularProgress";
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import Popper from "@mui/material/Popper";
import { Autocomplete, IconButton, InputAdornment } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import React, { useContext, useEffect, useMemo, useRef, useState } from "react";
import CloseIcon from '@mui/icons-material//Close';
import clsx from "clsx";
import Typography from "@mui/material/Typography";
import ListItemText from "@mui/material/ListItemText";
import ButtonBase from "@mui/material/ButtonBase";
import ExpandMore from "@mui/icons-material/ExpandMore";
import { WrappedFieldProps } from "redux-form";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import { usePrevious } from "../../../utils/hooks";
import { ListboxComponent, selectStyles } from "./SelectCustomComponents";
import { SelectItemRendererProps } from "../../../../model/common/Fields";
import WarningMessage from "../fieldMessage/WarningMessage";

const searchStyles = theme => createStyles({
  inputEndAdornment: {
    fontSize: "18px",
    color: theme.palette.primary.main,
    display: "flex",
    visibility: 'hidden'
  },
  validUnderline: {
    "&:after": {
      borderBottomColor: theme.palette.primary.main
    }
  },
  editingSelect: {
    paddingBottom: theme.spacing(1) + 1
  },
  selectorMenuBackground: {
    background: theme.palette.background.paper,
    borderRadius: "inherit"
  },
  label: {
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    paddingBottom: "4px",
    right: "-46%",
    maxWidth: "100%"
  },
  inline: {
    fontSize: "inherit"
  },
  labelShrink: {},
  editable: {
    color: theme.palette.text.primaryEditable,
    fontWeight: 400,
  },
  editableInHeader: {
    color: theme.palette.primary.contrastText,
  }
});

interface Props extends WrappedFieldProps {
  items: any[];
  classes?: any;
  label?: string;
  disabled?: boolean;
  className?: string;
  labelAdornment?: any;
  inline?: boolean;
  loading?: boolean;
  hideLabel?: boolean;
  colors?: any;
  creatable?: boolean;
  endAdornment?: any;
  formatting?: string;
  allowEmpty?: boolean;
  fieldClasses?: any;
  helperText?: string;
  selectLabelCondition?: any;
  selectFilterCondition?: any;
  defaultDisplayValue?: any;
  rowHeight?: number;
  remoteRowCount?: number;
  loadMoreRows?: AnyArgFunction;
  onCreateOption?: AnyArgFunction;
  itemRenderer?: AnyArgFunction<React.FC<SelectItemRendererProps>>;
  onInputChange?: AnyArgFunction;
  onClearRows?: AnyArgFunction;
  onInnerValueChange?: AnyArgFunction;
  selectValueMark?: string;
  remoteData?: boolean;
  createLabel?: string;
  selectLabelMark?: string;
  returnType?: "object" | "value";
  alwaysDisplayDefault?: boolean;
  popperAnchor?: any;
  placeholder?: string;
  sort?: (a: any, b: any) => number | boolean;
  sortPropKey?: string;
  inHeader?: boolean;
  hasError?: boolean;
  inputRef?: any;
  warning?: string;
}

const SelectContext = React.createContext<any>({});

const ListBoxAdapter = React.forwardRef<any, any>(({ children, ...other }, ref) => {
  const {
    rowHeight,
    remoteRowCount,
    loadMoreRows,
    classes,
    fieldClasses = {},
    loading
  } = useContext(SelectContext);

  return (
    <ListboxComponent
      rowHeight={rowHeight}
      remoteRowCount={remoteRowCount}
      loadMoreRows={loadMoreRows}
      classes={classes}
      fieldClasses={fieldClasses}
      loading={loading}
      ref={ref}
      {...other}
    >
      {children}
    </ListboxComponent>
  );
});

const PopperAdapter = React.memo<any>(params => {
  const {
    inline,
    items,
    popperAnchor
  } = useContext(SelectContext);

  return (inline && !items.length
    ? null
    : popperAnchor
      // @ts-ignore
      ? <Popper {...params} style={{ ...params.style, width: popperAnchor.clientWidth }} anchorEl={popperAnchor} />
      // @ts-ignore
      : <Popper {...params} />);
});

const EditInPlaceSearchSelect: React.FC<Props & WrappedFieldProps> = ({
    classes,
    label,
    disabled,
    className,
    labelAdornment,
    inline,
    loading,
    hideLabel,
    colors,
    creatable,
    endAdornment,
    formatting,
    allowEmpty,
    fieldClasses = {},
    helperText,
    selectValueMark = "value",
    selectLabelMark = "label",
    selectLabelCondition,
    selectFilterCondition,
    defaultDisplayValue,
    items = [],
    rowHeight,
    remoteRowCount,
    loadMoreRows,
    onCreateOption,
    itemRenderer,
    onInputChange,
    onClearRows,
    onInnerValueChange,
    remoteData,
    createLabel,
    returnType = "value",
    alwaysDisplayDefault,
    popperAnchor,
    input,
    meta,
    placeholder,
    sort,
    sortPropKey,
    inHeader,
    hasError,
    inputRef,
    warning
  }) => {
  const sortedItems = useMemo(() => items && (sort
    ? [...items].sort(typeof sort === "function"
      ? sort
      : (aOption, bOption) => {
        const aLabel = selectLabelCondition ? selectLabelCondition(aOption) : aOption[sortPropKey || selectLabelMark];
        const bLabel = selectLabelCondition ? selectLabelCondition(bOption) : bOption[sortPropKey || selectLabelMark];

        return (aLabel.toLowerCase()).localeCompare(bLabel.toLowerCase());
      })
      : [...items]
  ), [items, selectLabelCondition, selectLabelMark, sortPropKey]);

  const inputNode = useRef<any>(null);

  const [searchValue, setSearchValue] = useState<string>("");
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [formattedDisplayValue, setFormattedDisplayValue] = useState<any>("");

  const prevDefaultDisplayValue = usePrevious(defaultDisplayValue);

  useEffect(() => {
    if (selectLabelCondition && formattedDisplayValue && defaultDisplayValue !== prevDefaultDisplayValue) {
      setFormattedDisplayValue(null);
    }
    if (selectLabelCondition && !defaultDisplayValue) {
      const selected = sortedItems.find(i => getOptionSelected(i, input.value));
      if (selected) {
        setFormattedDisplayValue(selectLabelCondition(selected));
      }
    }
  }, [selectLabelCondition, formattedDisplayValue, defaultDisplayValue, sortedItems, input.value]);

  const onBlur = () => {
    setIsEditing(false);

    if (!inline) {
      setSearchValue("");
    }

    if (remoteData) {
      onInputChange("");
      onClearRows();
    }
  };

  const formatCreateLabel = inputValue => `${createLabel} "${inputValue}"`;

  const filterItems = items => {
    let filtered = [];

    if (!Array.isArray(items)) return filtered;

    if (!searchValue) {
      return items;
    }

    const searchRegexp = new RegExp(searchValue.replace(",", "")
      // eslint-disable-next-line no-useless-escape
      .replace(/[\[\]\{\}\(\)\*]/g, "\\$&")
      .trim().toLowerCase()
      .replace(/\s+/g, "|"));

    if (typeof selectFilterCondition === "function") {
      filtered = items.filter(item => {
        const label = selectFilterCondition(item);

        if (!label) {
          return false;
        }

        return searchRegexp.test(label.trim().toLowerCase());
      });
    } else if (typeof selectLabelCondition === "function") {
      filtered = items.filter(item => {
        const label = selectLabelCondition(item);

        if (!label) {
          return false;
        }

        return searchRegexp.test(label.trim().toLowerCase());
      });
    } else {
      filtered = items.filter( item => item[selectLabelMark]
        && searchRegexp.test(item[selectLabelMark].toLowerCase().trim()));
    }

    if (creatable) {
      filtered.push({
        [selectLabelMark]: formatCreateLabel(searchValue),
        [selectValueMark]: searchValue,
        createdOption: true
      });
    }

    return filtered;
  };

  const edit = () => {
    setIsEditing(true);
    setTimeout(() => {
      inputNode?.current?.focus();
    }, 50);
  };

  const onClear = () => {
    if (remoteData) {
      onInputChange("");
      onClearRows();
    }

    if (typeof onInnerValueChange === "function") {
      onInnerValueChange(null);
    }

    if (input && input.onChange) {
      input.onChange(null);
    }

    setSearchValue("");
    setIsEditing(false);
    setFormattedDisplayValue(null);
  };

  const onFocus = e => {
    if (!inline) {
      input.onFocus(e);
    }
  };

  const onEditButtonFocus = () => {
    if (disabled) {
      return;
    }
    edit();
  };

  const onCreateOptionInner = value => {
    delete value.createdOption;
    onCreateOption(value[selectValueMark]);
    onBlur();
  };

  const handleChange = (e, value, reason) => {
    if (reason === "clear") {
      return;
    }

    if (creatable && value && value.createdOption) {
      onCreateOptionInner(value);
    }

    if (typeof onInnerValueChange === "function") {
      onInnerValueChange(value);
    }

    if (selectLabelCondition && returnType !== "object") {
      setFormattedDisplayValue(selectLabelCondition(value));
    }

    const newValue = returnType === "object" ? (Object.keys(value).length ? value : null) : value[selectValueMark];

    input.onChange(newValue);

    setTimeout(() => {
      onBlur();
      input.onBlur(newValue);
    }, 100);
  };

  const handleInputChange = e => {
    setSearchValue(e.target.value);

    if (onInputChange) {
      onInputChange(e.target.value);
    }

    if (remoteData && !e.target.value) {
      onClearRows();
    }
  };

  const getOptionLabel = option => (selectLabelCondition ? selectLabelCondition(option) : option[selectLabelMark]) || "";

  const getOptionSelected = (option: any, value: any) => {
    if (returnType === "object") {
      return option === value;
    }
    return option[selectValueMark] === value;
  };

  const renderOption = (optionProps, data) => {
    if (typeof itemRenderer === "function") {
      return itemRenderer(getHighlightedPartLabel(getOptionLabel(data), searchValue), data, searchValue, optionProps) as any;
    }

    return getHighlightedPartLabel(getOptionLabel(data), searchValue, optionProps);
  };

  const displayedValue = useMemo(() => {
    let response;

    if (selectLabelCondition) {
      response = returnType === "object" ? selectLabelCondition(input.value) : formattedDisplayValue || defaultDisplayValue;
    } else if (returnType === "object") {
      response = input.value && input.value[selectLabelMark];
    } else {
      const selected = sortedItems.find(i => getOptionSelected(i, input.value));
      response = selected ? selected[selectLabelMark] : defaultDisplayValue || input.value;
    }

    if (alwaysDisplayDefault) {
      response = defaultDisplayValue;
    }

    return (
      ![null, undefined, ''].includes(input.value)
      ? response
      : <span className={clsx("overflow-hidden placeholderContent", classes.editable)}>No value</span>
    );
  }, [formattedDisplayValue, selectLabelCondition, alwaysDisplayDefault, returnType, defaultDisplayValue, selectLabelMark, input, classes]);

  const labelContent = useMemo(() => (labelAdornment ? (
    <span>
      {label}
      {' '}
      <span>{labelAdornment}</span>
    </span>
  ) : (
    label
  )), [classes, label, labelAdornment]);

  return (
    <div
      className={clsx(className, "outline-none")}
      id={inline ? undefined : (input && input.name)}
    >
      <div
        className={clsx({
          "d-none": (inHeader && !(inline || isEditing || (meta && meta.invalid))),
          [classes.editingSelect]: !inline && formatting !== "inline"
        })}
      >
        <SelectContext.Provider value={{
          rowHeight,
          remoteRowCount,
          loadMoreRows,
          classes,
          fieldClasses,
          inline,
          items,
          popperAnchor,
          loading
        }}
        >
          <Autocomplete
            value={input.value || ""}
            options={sortedItems}
            loading={loading}
            freeSolo={creatable}
            disableClearable={!allowEmpty}
            isOptionEqualToValue={getOptionSelected}
            onChange={handleChange}
            classes={{
              root: clsx("d-inline-flex", classes.root),
              hasPopupIcon: classes.hasPopup,
              hasClearIcon: classes.hasClear,
              inputRoot: classes.inputWrapper,
              option: "w-100 text-pre"
            }}
            renderOption={renderOption}
            getOptionLabel={getOptionLabel}
            filterOptions={filterItems}
            ListboxComponent={ListBoxAdapter as any}
            PopperComponent={PopperAdapter as any}
            renderInput={({
             InputLabelProps, InputProps, inputProps, ...params
            }) => (
              <FormControl
                {...params}
                variant="standard"
                error={meta?.invalid}
              >
                {labelContent && (
                  <InputLabel shrink={true} error={meta?.invalid || hasError} htmlFor={`input-${input.name}`}>
                    {labelContent}
                  </InputLabel>
                )}
                <Input
                  {...InputProps}
                  id={`input-${input.name}`}
                  name={input.name}
                  disabled={disabled}
                  placeholder={placeholder || (!isEditing ? "No value" : "")}
                  autoFocus={inline}
                  onChange={handleInputChange}
                  onFocus={onFocus}
                  onBlur={onBlur}
                  onClick={onEditButtonFocus}
                  inputRef={rf => {
                    inputNode.current = rf;
                    if (inputRef) {
                      inputRef.current = rf;
                    }
                  }}
                  disableUnderline={inline}
                  classes={{
                    underline: fieldClasses.underline,
                    input: clsx(inHeader && classes.editableInHeader, disabled && classes.readonly, fieldClasses.text),
                  }}
                  inputProps={{
                    ...inputProps,
                    value: (isEditing ? searchValue : (typeof displayedValue === "string" ? displayedValue : "")),
                  }}
                  endAdornment={!disabled && (
                    loading
                      ? <CircularProgress size={24} thickness={4} className={fieldClasses.loading} />
                      : (
                        <InputAdornment position="end" className={classes.inputEndAdornment}>
                          {allowEmpty && input.value && (
                            <IconButton
                              size="small"
                              onClick={onClear}
                              color="inherit"
                            >
                              <CloseIcon className={clsx(fieldClasses.editIcon, classes.clearIcon)} />
                            </IconButton>
                          ) }
                          {!inline && <ExpandMore className={clsx("hoverIcon", fieldClasses.editIcon)} />}
                        </InputAdornment>
                      )
                  )}
                />
                <FormHelperText
                  classes={{
                    error: "shakingError"
                  }}
                >
                  {(meta && meta.error) || helperText}
                  {warning && <WarningMessage warning={warning} />}
                </FormHelperText>
              </FormControl>
            )}
            fullWidth
            disableListWrap
            openOnFocus
            blurOnSelect
          />
        </SelectContext.Provider>
      </div>
      {formatting === "inline" && (
        <div
          className={clsx({
          "d-none": !inHeader || (inHeader && (inline || isEditing || (meta && meta.invalid)))
        })}
        >
          <div className="mw-100 text-truncate">
            {!hideLabel && label && (
              <Typography
                variant="caption"
                color="textSecondary"
                style={colors ? { color: `${colors.subheader}` } : {}}
                noWrap
              >
                {label}
                {' '}
                {labelAdornment && <span>{labelAdornment}</span>}
              </Typography>
          )}

            <ListItemText
              classes={{
              root: "pl-0 mb-0 mt-0",
              primary: clsx("d-flex", formatting === "inline" && classes.inline)
              }}
              primary={(
                <>
                  <ButtonBase
                    onFocus={onEditButtonFocus}
                    className={clsx(classes.editable, fieldClasses.text, "overflow-hidden d-flex hoverIconContainer", {
                    [classes.readonly]: disabled
                  })}
                    component="div"
                  >
                    <span className={clsx("text-truncate", fieldClasses.text)}>
                      {displayedValue}
                    </span>
                    {!disabled && (
                      <ExpandMore className={clsx("hoverIcon", classes.editIcon, fieldClasses.editIcon)} />
                  )}
                  </ButtonBase>
                  {endAdornment}
                </>
              )}
            />
          </div>
        </div>
    )}
    </div>
  );
};

export default withStyles(theme => ({ ...selectStyles(theme), ...searchStyles(theme) } as any), { withTheme: true })(
  EditInPlaceSearchSelect
) as any;