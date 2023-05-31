/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import CircularProgress from "@mui/material/CircularProgress";
import Popper from "@mui/material/Popper";
import { Autocomplete, IconButton, InputAdornment, Select } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import React, { useContext, useEffect, useMemo, useRef, useState } from "react";
import CloseIcon from '@mui/icons-material//Close';
import clsx from "clsx";
import ExpandMore from "@mui/icons-material/ExpandMore";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import { usePrevious } from "../../../utils/hooks";
import { ListboxComponent, selectStyles } from "./SelectCustomComponents";
import EditInPlaceFieldBase from "./EditInPlaceFieldBase";
import { EditInPlaceSearchSelectFieldProps } from "../../../../model/common/Fields";
import { stubComponent } from "../../../utils/common";

const searchStyles = theme => createStyles({
  selectAdornment: {
    marginBottom: "6px",
    color: theme.palette.primary.main
  },
  endAdornment: {
    opacity: 0.5
  },
  popper: {
    zIndex: 1400
  }
});

const SelectContext = React.createContext<any>({});

const ListBoxAdapter = React.forwardRef<any, any>(({ children, ...other }, ref) => {
  const {
    rowHeight,
    remoteRowCount,
    loadMoreRows,
    classes,
    fieldClasses = {},
    loading,
    selectAdornment
  } = useContext(SelectContext);

  return (
    <ListboxComponent
      rowHeight={rowHeight}
      remoteRowCount={remoteRowCount}
      loadMoreRows={loadMoreRows}
      classes={classes}
      fieldClasses={fieldClasses}
      loading={loading}
      selectAdornment={selectAdornment}
      ref={ref}
      {...other}
    >
      {children}
    </ListboxComponent>
  );
});

const PopperAdapter = React.memo<any>(params => {
  const {
    hideMenuOnNoResults,
    items,
    popperAnchor,
    inline
  } = useContext(SelectContext);

  return (hideMenuOnNoResults && !items.length
    ? null
    : popperAnchor
      // @ts-ignore
      ? <Popper
         {...params}
         style={{ ...params.style, width: popperAnchor.clientWidth }}
         anchorEl={popperAnchor}
      />
      // @ts-ignore
      : <Popper
        {...params}
        style={inline ? null : params.style}
      />);
});

const EditInPlaceSearchSelect = ({
    classes,
    label,
    disabled,
    className,
    labelAdornment,
    inline,
    loading,
    creatable,
    allowEmpty,
    fieldClasses = {},
    selectAdornment,
    selectValueMark = "value",
    selectLabelMark = "label",
    selectLabelCondition,
    selectFilterCondition,
    defaultValue,
    disableUnderline,
    items = [],
    rowHeight,
    remoteRowCount,
    loadMoreRows,
    onCreateOption,
    itemRenderer,
    onInputChange,
    onClearRows,
    onInnerValueChange,
    hideMenuOnNoResults,
    hideEditIcon,
    remoteData,
    createLabel,
    returnType = "string",
    alwaysDisplayDefault,
    valueRenderer,
    popperAnchor,
    input,
    meta: { error, invalid },
    placeholder,
    sort,
    sortPropKey,
    inputRef,
    warning,
    multiple,
    rightAligned,
    hasError,
    categoryKey
  }: EditInPlaceSearchSelectFieldProps) => {

  const sortedItems = useMemo(() => {
    const sorted = items && (sort
        ? [...items].sort(typeof sort === "function"
          ? sort
          : (aOption, bOption) => {
            const aLabel = selectLabelCondition ? selectLabelCondition(aOption) : aOption[sortPropKey || selectLabelMark];
            const bLabel = selectLabelCondition ? selectLabelCondition(bOption) : bOption[sortPropKey || selectLabelMark];

            return (aLabel.toLowerCase()).localeCompare(bLabel.toLowerCase());
          })
        : [...items]
    );
    
    if (categoryKey) {
      sorted.sort((aOption, bOption) => (aOption[categoryKey].toLowerCase()).localeCompare(bOption[categoryKey].toLowerCase()));
    }
    
    return sorted;
    
  }, [items, selectLabelCondition, selectLabelMark, sortPropKey, categoryKey]);

  const inputNode = useRef<any>(null);

  const [searchValue, setSearchValue] = useState<string>("");
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [formattedDisplayValue, setFormattedDisplayValue] = useState<any>("");

  const prevDefaultDisplayValue = usePrevious(defaultValue);

  useEffect(() => {
    if (formattedDisplayValue && !input.value) {
      setFormattedDisplayValue(null);
    }
  }, [formattedDisplayValue, input.value, input.name]);

  useEffect(() => {
    if (selectLabelCondition && formattedDisplayValue && defaultValue !== prevDefaultDisplayValue) {
      setFormattedDisplayValue(null);
    }
    if (selectLabelCondition && !defaultValue) {
      const selected = sortedItems.find(i => getOptionSelected(i, input.value));
      if (selected) {
        setFormattedDisplayValue(selectLabelCondition(selected));
      }
    }
  }, [selectLabelCondition, formattedDisplayValue, defaultValue, sortedItems, input.value]);

  const onBlur = () => {
    setIsEditing(false);

    if (!hideMenuOnNoResults) {
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

    if (!searchValue || remoteData)  return items;

    const searchRegexp = new RegExp(searchValue.replace(",", "")
      // eslint-disable-next-line no-useless-escape
      .replace(/[\[\]\{\}\(\)\*]/g, "\\$&")
      .trim().toLowerCase());

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
    setTimeout(() => inputNode?.current?.focus(), 50);
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

    let newValue = value[selectValueMark];

    if (returnType === "object") {
      newValue = (Object.keys(value).length ? value : null);
    }

    if (multiple) {
      newValue = value.map(v => v[selectValueMark]);
      if (!newValue.length) {
        newValue = null;
      }
    }

    input.onChange(newValue);

    setTimeout(() => {
      onBlur();
      input?.onBlur(newValue);
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

  const getOptionLabel = option => (selectLabelCondition ? selectLabelCondition(option) : option && option[selectLabelMark]) || "";

  const getOptionSelected = (option: any, value: any) => {
    if (multiple || returnType === "object") {
      return option[selectValueMark] === value[selectValueMark];
    }
    return option[selectValueMark] === value;
  };

  const renderOption = (optionProps, data) => {
    if (typeof itemRenderer === "function") {
      return itemRenderer(getHighlightedPartLabel(getOptionLabel(data), searchValue), data, searchValue, optionProps) as any;
    }
    return getHighlightedPartLabel(getOptionLabel(data), searchValue, optionProps);
  };

  const selectedOption = useMemo(() => sortedItems.find(i => multiple
      ? (input.value || []).includes(i[selectValueMark])
      : getOptionSelected(i, input.value)),
    [sortedItems, multiple, selectValueMark, input.value]);

  const displayedValue = useMemo(() => {
    let response;

    if (selectLabelCondition) {
      response = returnType === "object" ? selectLabelCondition(input.value) : formattedDisplayValue || defaultValue;
    } else if (returnType === "object") {
      response = input.value && input.value[selectLabelMark];
    } else {
      response = getOptionLabel(selectedOption) || defaultValue || input.value;
    }

    if (alwaysDisplayDefault) {
      response = defaultValue;
    }

    return (
      ![null, undefined].includes(input.value)
      ? response
      : null
    );
  }, [
    formattedDisplayValue,
    selectedOption,
    selectLabelCondition,
    alwaysDisplayDefault,
    returnType,
    defaultValue,
    selectLabelMark,
    input.value,
    classes
  ]);

  const renderValue = useMemo(() => {
    const valueForRender = returnType === "object"
      ? input.value
      : selectedOption;
    
    return valueRenderer
      ? valueRenderer(
        displayedValue,
        valueForRender,
        searchValue,
        { value: valueForRender && valueForRender[selectValueMark] }
      )
      : null;  
  },
  [selectedOption, searchValue, displayedValue, selectValueMark, returnType, valueRenderer, input.value]);

  const renderIcons = useMemo(() => !disabled && (
    loading
      ? <CircularProgress size={24} thickness={4} className={fieldClasses.loading} />
      : (
        <div className={classes.inputEndAdornment}>
          {allowEmpty && input.value && (
            <IconButton
              size="small"
              onClick={onClear}
              color="inherit"
            >
              <CloseIcon className={clsx("cursor-pointer", inline && "fsInherit")} />
            </IconButton>
          )}
          <IconButton
            size="small"
            color="inherit"
            className="pl-0 pr-0"
            disableRipple
          >
            <ExpandMore className={inline && "fsInherit"} />
          </IconButton>
        </div>
      )
  ), [disabled, loading, inline, allowEmpty, input.value]);

  const inputValue = useMemo(() => {
    if (multiple) {
      return (input.value || []).map(v => sortedItems.find(s => s[selectValueMark] === v));
    }
    return input.value || null;
  }, [input.value, multiple, selectValueMark, sortedItems]);

  const renderedPlaceholder = useMemo(() => {
    const rendered = placeholder || (!isEditing ? "No value" : "");
    return multiple && inputValue.length ? null : rendered;
  }, [multiple, placeholder, isEditing, inputValue]);

  return (
    <div
      className={clsx(className, "outline-none")}
      id={input?.name}
    >
      <SelectContext.Provider value={{
        rowHeight,
        remoteRowCount,
        loadMoreRows,
        classes,
        fieldClasses,
        hideMenuOnNoResults,
        inline,
        items,
        popperAnchor,
        loading,
        selectAdornment
      }}
      >
        <Autocomplete
          blurOnSelect={!multiple}
          disableCloseOnSelect={multiple}
          multiple={multiple}
          value={inputValue}
          options={sortedItems}
          loading={loading}
          freeSolo={creatable}
          disabled={disabled}
          disableClearable={!allowEmpty}
          isOptionEqualToValue={getOptionSelected}
          onChange={handleChange}
          classes={{
            root: clsx("d-inline-flex", classes.root, inline && classes.inline),
            hasPopupIcon: classes.hasPopup,
            hasClearIcon: classes.hasClear,
            inputRoot: clsx(classes.inputWrapper, multiple && classes.multiple),
            option: "w-100 text-pre",
            popper: classes.popper,
            groupUl: "m-0"
          }}
          groupBy={categoryKey && (option => option[categoryKey])}
          renderOption={renderOption}
          getOptionLabel={getOptionLabel}
          filterOptions={filterItems}
          ListboxComponent={inline || categoryKey ? null : ListBoxAdapter as any}
          PopperComponent={categoryKey ? null : PopperAdapter as any}
          renderInput={({
           InputLabelProps, InputProps, inputProps, ...params
          }) => (
            <EditInPlaceFieldBase
              {...params}
              name={input.name}
              value={displayedValue}
              error={error}
              invalid={hasError || invalid}
              inline={inline}
              label={label}
              warning={warning}
              fieldClasses={fieldClasses}
              endAdornmentClass={classes.endAdornment}
              rightAligned={rightAligned}
              shrink={Boolean(label || input.value)}
              labelAdornment={labelAdornment}
              placeholder={renderedPlaceholder}
              editIcon={!hideEditIcon && renderIcons}
              InputProps={{
                ...InputProps,
                onChange: handleInputChange,
                onFocus: edit,
                onClick: onEditButtonFocus,
                onBlur,
                disableUnderline,
                classes: {
                  underline: fieldClasses.underline,
                  input: clsx(disabled && classes.readonly, fieldClasses.text),
                },
                inputProps: {
                  ...inputProps,
                  className: clsx(fieldClasses.text, "mr-auto"),
                  ref: ref => {
                    (inputProps as any).ref.current = ref;
                    inputNode.current = ref;
                    if (inputRef) inputRef.current = ref;
                  },
                  value: (isEditing ? searchValue : multiple ? "" : (typeof displayedValue === "string" ? displayedValue : "")),
                }
              }}
              CustomInput={(valueRenderer && !multiple && !isEditing && input.value)
                ?
                <Select
                  {...InputProps}
                  inputRef={ref => {
                    (inputProps as any).ref.current = ref?.node;
                  }}
                  classes={{ select: "cursor-text" }}
                  onFocus={edit}
                  value={(returnType === "object" ? input.value[selectValueMark] : input.value) || ""}
                  endAdornment={
                    <InputAdornment
                      position="end"
                      className={clsx(classes.endAdornment, classes.selectAdornment, "d-none")}>
                      {renderIcons}
                    </InputAdornment>
                  }
                  IconComponent={stubComponent}
                >
                  {renderValue}
                </Select>
                : null}
            />)
          }
          disableListWrap
          openOnFocus
          fullWidth
        />
      </SelectContext.Provider>
    </div>
  );
};

export default withStyles(theme => ({ ...selectStyles(theme), ...searchStyles(theme) } as any), { withTheme: true })(
  EditInPlaceSearchSelect
) as React.FC<EditInPlaceSearchSelectFieldProps>;