/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import CircularProgress from "@material-ui/core/CircularProgress";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import Popper from "@material-ui/core/Popper";
import React, {
  useContext, useEffect, useMemo, useRef, useState
} from "react";
import Autocomplete from '@material-ui/lab/Autocomplete';
import { withStyles } from "@material-ui/core";
import clsx from "clsx";
import Typography from "@material-ui/core/Typography";
import ListItemText from "@material-ui/core/ListItemText";
import ButtonBase from "@material-ui/core/ButtonBase";
import ExpandMore from "@material-ui/icons/ExpandMore";
import createStyles from "@material-ui/core/styles/createStyles";
import { WrappedFieldProps } from "redux-form";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import { usePrevious } from "../../../utils/hooks";
import {
  selectStyles,
  ListboxComponent
} from "./SelectCustomComponents";
import { stubComponent } from "../../../utils/common";

const searchStyles = theme => createStyles({
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
    maxWidth: "100%",
    "& $labelAdornment": {
      position: "absolute",
      transform: "scale(1.33) translate(5px,0)"
    },
    "&$labelShrink": {
      maxWidth: "calc(100% * 1.4)"
    }
  },
  option: {
    whiteSpace: "nowrap",
    "& > span:last-child": {
      overflow: "hidden",
      textOverflow: "ellipsis"
    }
  },
  inline: {
    fontSize: "inherit"
  },
  labelShrink: {},
  labelAdornment: {}
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
  itemRenderer?: AnyArgFunction;
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
}

const SelectContext = React.createContext<any>({});

const ListBoxAdapter = React.forwardRef<any, any>(({ children, ...other }, ref) => {
  const {
    rowHeight,
    remoteRowCount,
    loadMoreRows,
    classes,
    fieldClasses,
  } = useContext(SelectContext);

  return (
    <ListboxComponent
      rowHeight={rowHeight}
      remoteRowCount={remoteRowCount}
      loadMoreRows={loadMoreRows}
      classes={classes}
      fieldClasses={fieldClasses}
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
  items,
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
  sortPropKey
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

  const isAdornmentHovered = useRef<boolean>(false);
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
    if (isAdornmentHovered.current) {
      return;
    }

    setIsEditing(false);

    if (!inline) {
      setSearchValue("");
    }

    if (remoteData) {
      onInputChange("");
      onClearRows();
    }
  };

  const onAdornmentOver = () => {
    isAdornmentHovered.current = true;
  };

  const onAdornmentOut = () => {
    isAdornmentHovered.current = false;
  };

  const onAdornmentClick = e => {
    if (isAdornmentHovered.current) {
      e.preventDefault();
    }
    setTimeout(() => {
      isAdornmentHovered.current = false;
      onBlur();
    }, 1000);
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
      inputNode.current.focus();
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
      onClear();
      return;
    }

    if (creatable && value.createdOption) {
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
    const searchValue = e.target.value;

    if (onInputChange) {
      onInputChange(searchValue);
    }

    if (remoteData && !searchValue) {
      onClearRows();
    }

    setSearchValue(searchValue);
  };

  const getOptionLabel = option => (selectLabelCondition ? selectLabelCondition(option) : option[selectLabelMark]) || "";

  const getOptionSelected = (option: any, value: any) => {
    if (returnType === "object") {
      return option === value;
    }
    return option[selectValueMark] === value;
  };

  const renderOption = data => {
    const option = getHighlightedPartLabel(getOptionLabel(data), searchValue);

    if (typeof itemRenderer === "function") {
      return itemRenderer(option, data, searchValue) as any;
    }

    return option as any;
  };

  const displayedValue = useMemo(() => {
    let response;

    if (selectLabelCondition) {
      response = returnType === "object" ? selectLabelCondition(input.value) : formattedDisplayValue || defaultDisplayValue;
    } else if (returnType === "object") {
      response = input.value[selectLabelMark];
    } else {
      const selected = sortedItems.find(i => getOptionSelected(i, input.value));

      response = selected ? selected[selectLabelMark] : defaultDisplayValue || input.value;
    }

    if (alwaysDisplayDefault) {
      response = defaultDisplayValue;
    }

    return ![null, undefined, ''].includes(input.value) ? response : <span className={clsx("overflow-hidden placeholderContent", classes.editable)}>No value</span>;
  }, [formattedDisplayValue, selectLabelCondition, alwaysDisplayDefault, returnType, defaultDisplayValue, selectLabelMark, input, classes]);

  const labelContent = useMemo(() => (labelAdornment ? (
    <span onMouseEnter={onAdornmentOver} onMouseLeave={onAdornmentOut} onMouseDown={onAdornmentClick}>
      {label}
      {' '}
      <span className={classes.labelAdornment}>{labelAdornment}</span>
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
        className={clsx("pr-2", {
          "d-none": !(inline || isEditing || (meta && meta.invalid)),
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
          popperAnchor
        }}
        >
          <Autocomplete
            value={input.value || null}
            options={sortedItems}
            loading={loading}
            freeSolo={creatable}
            disableClearable={!allowEmpty}
            getOptionSelected={getOptionSelected}
            onChange={handleChange}
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
                error={meta && meta.invalid}
              >
                {labelContent && <InputLabel>{labelContent}</InputLabel>}
                <Input
                  {...InputProps}
                  placeholder={placeholder}
                  autoFocus={inline}
                  onChange={handleInputChange}
                  onFocus={onFocus}
                  onBlur={onBlur}
                  inputRef={inputNode}
                  disableUnderline={inline}
                  classes={{
                    root: fieldClasses.text,
                    underline: fieldClasses.underline
                  }}
                  inputProps={{
                    ...inputProps,
                    value: searchValue
                  }}
                />
                <FormHelperText
                  classes={{
                    error: "shakingError"
                  }}
                >
                  {(meta && meta.error) || helperText}
                </FormHelperText>
              </FormControl>
          )}
            popupIcon={
              loading
                ? <CircularProgress size={24} thickness={4} className={fieldClasses ? fieldClasses.loading : undefined} />
                : stubComponent()
            }
            classes={{
              option: itemRenderer ? null : classes.option
            }}
            disableListWrap
            openOnFocus
            blurOnSelect
          />
        </SelectContext.Provider>
      </div>
      <div
        className={clsx(formatting !== "inline" && "textField", {
          "d-none": inline || isEditing || (meta && meta.invalid)
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
                  className={clsx(classes.editable, "overflow-hidden d-flex hoverIconContainer", {
                    "pointer-events-none": disabled
                  })}
                  component="div"
                >
                  <span className={clsx("text-truncate", classes.editable, fieldClasses && fieldClasses.text)}>
                    {displayedValue}
                  </span>
                  {!disabled && (
                    <ExpandMore className={clsx("hoverIcon", classes.editIcon, fieldClasses && fieldClasses.editIcon)} />
                  )}
                </ButtonBase>
                {endAdornment}
              </>
            )}
          />
        </div>
      </div>
    </div>
  );
};

export default withStyles(theme => ({ ...selectStyles(theme), ...searchStyles(theme) } as any), { withTheme: true })(
  EditInPlaceSearchSelect
) as any;
