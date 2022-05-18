/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState 
} from "react";
import { Tag } from "@api/model";
import {
 FormControl, FormHelperText, Input, InputAdornment, InputLabel, Typography 
} from "@mui/material";
import ClickAwayListener from '@mui/material/ClickAwayListener';
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Autocomplete from "@mui/material/Autocomplete";
import clsx from "clsx";
import { WrappedFieldProps } from "redux-form";
import { Edit } from "@mui/icons-material";
import { getAllMenuTags } from "../../../../containers/tags/utils";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { MenuTag } from "../../../../model/tags";
import { stubComponent } from "../../../utils/common";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import getCaretCoordinates from "../../../utils/getCaretCoordinates";
import { getMenuTags } from "../../list-view/utils/listFiltersUtils";
import { selectStyles } from "../formFields/SelectCustomComponents";
import AddTagMenu from "./AddTagMenu";

const styles = theme =>
  createStyles({
    listContainer: {
      marginLeft: "-2px"
    },
    inputWrapper: {
      "&:hover $inputEndAdornment": {
        visibility: "visible",
      }
    },
    inputEndAdornment: {
      visibility: 'hidden',
      display: "flex",
      fontSize: "18px",
      color: theme.palette.primary.main,
      alignItems: "flex-end",
      alignSelf: "flex-end",
      marginBottom: "4px"
    },
    tagBody: {
      color: theme.palette.text.primary,
      cursor: "pointer",
      borderRadius: `${theme.shape.borderRadius}px`,
      "&:hover": {
        color: theme.palette.error.main
      },
      "&:hover $tagDeleteButton": {
        visibility: "visible"
      }
    },
    tagBodyTypography: {
      color: "inherit"
    },
    tagDeleteButton: {
      margin: "4px 0 0 2px",
      visibility: "hidden"
    },
    tagDeleteIcon: {
      fontSize: "16px",
      color: "inherit",
      marginRight: theme.spacing(1)
    },
    hoverIcon: {
      opacity: 0.5,
      visibility: "hidden",
      marginLeft: theme.spacing(1)
    },
    editable: {
      position: "relative",
      display: "inline-flex",
      color: theme.palette.text.primaryEditable,
      minHeight: "32px",
      padding: "4px 0 4px",
      marginTop: theme.spacing(2),
      fontWeight: 400,
      justifyContent: "space-between",
      alignItems: "flex-end",
      "&:hover $hoverIcon": {
        visibility: "visible"
      },
      "&:before": {
        borderBottom: '1px solid transparent',
        left: 0,
        bottom: 0,
        content: "' '",
        position: "absolute",
        right: 0,
        transition: theme.transitions.create("border-bottom-color", {
          duration: theme.transitions.duration.standard,
          easing: theme.transitions.easing.easeInOut
        }),
        pointerEvents: "none"
      },
      "&:hover:before": {
        borderBottom: `1px solid ${theme.palette.primary.main}`
      },
    },
    tagColorDotSmall: {
      width: theme.spacing(2),
      minWidth: theme.spacing(2),
      height: theme.spacing(2),
      minHeight: theme.spacing(2),
      background: "red",
      borderRadius: "100%"
    },
    listbox: {
      whiteSpace: 'break-spaces'
    },
    hasPopup: {
      "&$root $inputWrapper": {
        padding: 0
      },
      "&$root$hasClear $inputWrapper": {
        padding: 0
      }
    },
    placeholder: {
      opacity: 0.15
    }
  });

interface Props extends WrappedFieldProps {
  showConfirm: ShowConfirmCaller;
  tags: Tag[];
  classes?: any;
  fieldClasses?: any;
  disabled?: boolean;
  className?: string;
  label?: string;
  placeholder?: string;
}

const endTagRegex = /#\s*[^\w\d]*$/;

const getCurrentInputString = (input, formTagIds: number[] = [], allMenuTags: MenuTag[] = []) => {
  let substr = input;

  formTagIds.forEach(id => {
    const tag = allMenuTags.find(t => t.tagBody.id === id);
    if (tag) {
      substr = substr.replace("#" + tag.tagBody.name, "").trim();
    }
  });

  if (substr) {
    return substr.trim().replace(/#/g, "");
  }

  return substr;
};

const getFullTag = (tagId: number, tags: Tag[]) => {
  let i = 0;
  let result;

  while (i < tags.length) {
    if (tagId === tags[i].id) {
      return tags[i];
    }

    if (result) break;

    if (tags[i].childTags.length) {
      result = getFullTag(tagId, tags[i].childTags);
      if (result) return result;
    }

    ++i;
  }
};

const getInputString = (tagIds: number[], allTags: Tag[]) => (tagIds?.length && allTags?.length
  ? tagIds.reduce((acc, id) => {
    const tag = getFullTag(id, allTags);
    return (tag ? `${acc}#${tag.name} ` : acc);
  }, "")
  : "");

const SimpleTagList: React.FC<Props> = props => {
  const {
    input,
    tags,
    classes,
    placeholder,
    meta,
    label = "Tags",
    disabled,
    className,
    fieldClasses = {}
  } = props;

  const [menuIsOpen, setMenuIsOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [currentInputString, setCurrentInputString] = useState("");

  const InputValueForRender = useMemo(() => {
    if (!inputValue || !tags || !tags.length) return "";

    const arrayOfTags = input?.value?.length
      && input.value.map(id => getFullTag(id, tags)).filter(t => t);

    if (!arrayOfTags?.length) return "";

    return arrayOfTags.map((tag: Tag, index) => (
      <span key={tag.id} className={clsx("centeredFlex", index !== arrayOfTags.length - 1 ? "pr-1" : "")}>
        <div key={tag.id} className={clsx(classes.tagColorDotSmall, "mr-0-5")} style={{ background: "#" + tag.color }} />
        <span className="text-nowrap">
          {`#${tag.name} `}
        </span>
      </span>
    ));
  }, [tags, input.value, inputValue]);

  useEffect(() => {
    if (meta.invalid && !isEditing) {
      setIsEditing(true);
    }
  }, [meta.invalid]);

  const inputNode = useRef<any>();
  const tagMenuNode = useRef<any>();

  const menuTags = useMemo(
    () => (tags && input.value ? getMenuTags(tags.filter(t => t.childrenCount > 0), input.value.map(id => getFullTag(id, tags)).filter(t => t)) : []),
    [tags, input.value]
  );

  const allMenuTags = useMemo(
    () => getAllMenuTags(menuTags),
    [menuTags]
  );

  const synchronizeTags = () => {
    const inputString = getInputString(input.value, tags);

    if (inputString.trim() === inputValue.replace(endTagRegex, "").trim()) {
      return;
    }

    const updated = [];
    const current = inputValue.split("#").filter(i => i).map(i => i.trim());

    allMenuTags.forEach(t => {
      if (!t.children.length) {
        const index = current.findIndex(c => c === t.tagBody.name);
        if (index !== -1) {
          const addedTagsMatch = input.value.find(id => getFullTag(id, tags)?.name === t.tagBody.name);
          if (addedTagsMatch && addedTagsMatch.id !== t.tagBody.id) {
            return;
          }
          updated.push(t.tagBody.id);
          current.splice(index, 1);
        }
      }
    });

    input.onChange(updated);
    setInputValue(getInputString(updated, tags));
  };

  const onTagAdd = (tag: MenuTag) => {
    const updated = [...input.value];

    const index = updated.findIndex(id => id === tag.tagBody.id);

    if (index !== -1) {
      updated.splice(index, 1);
    } else {
      updated.push(tag.tagBody.id);
    }

    input.onChange(updated);

    setTimeout(() => {
      inputNode?.current?.focus();
    }, 100);
  };

  const filterOptions = item => !item.children.length && !input.value.some(id => id === item.tagBody.id) && item.tagBody.name
    .toLowerCase()
    .trim()
    .startsWith(currentInputString.toLowerCase().trim());

  const filterOptionsInner = options => options.filter(filterOptions);

  const filteredOptions = allMenuTags.filter(filterOptions);

  const getOptionText = (option, optionProps, label) => (
    <div {...optionProps}>
      {`${option.path ? option.path + " /" : ""}`}
      {' '}
      {label}
    </div>
  );

  const getOptionLabel = op => op.path;

  const renderOption = (optionProps, option) => {
    const label = option?.tagBody?.name;
    const highlightedLabel = getHighlightedPartLabel(label, currentInputString);
    return getOptionText(option, optionProps, highlightedLabel);
  };

  const handleInputChange = e => {
    setInputValue(e.target.value);
  };

  const edit = () => {
    setIsEditing(true);
    setTimeout(() => {
      inputNode?.current?.focus();
    }, 50);
  };

  const exit = () => {
    setMenuIsOpen(false);
    setIsEditing(false);

    if (endTagRegex.test(inputValue)) {
      setTimeout(() => {
        setInputValue(inputValue.replace(endTagRegex, "").trim());
      }, 100);
    }
  };

  const onFocus = () => {
    setMenuIsOpen(true);
    if (!endTagRegex.test(inputValue)) {
      setInputValue(inputValue + " #");
    }
  };

  const onBlur = () => {
    if (currentInputString) {
      exit();
      synchronizeTags();
    }
  };

  const onTagListBlur = () => {
    if (document.activeElement !== inputNode.current) {
      exit();
      synchronizeTags();
    }
  };

  const handleChange = (e, value, action) => {
    if (action === "selectOption") {
      onTagAdd(value);
    }
  };

  useEffect(() => {
    let inputString = getInputString(input.value, tags);

    if (document.activeElement === inputNode.current && !endTagRegex.test(inputString)) {
      inputString += " #";
    }

    setInputValue(inputString);
  }, [input.value, tags]);

  useEffect(() => {
    setCurrentInputString(getCurrentInputString(inputValue, input.value || [], allMenuTags));
  }, [inputValue, input.value, allMenuTags]);

  const popperAdapter = useCallback(params => {
    if (currentInputString) {
      return <div {...params} />;
    }

    const coords = getCaretCoordinates(inputNode.current, inputValue.length);
    const position = tagMenuNode.current
    && coords.left > inputNode.current.clientWidth - tagMenuNode.current.clientWidth
      ? { right: 0 }
      : { left: coords.left };

    return (
      <div id="popper" style={{ ...position, position: "absolute", zIndex: 2 }}>
        <ClickAwayListener onClickAway={onTagListBlur}>
          {params.children}
        </ClickAwayListener>
      </div>
    );
  }, [allMenuTags, inputValue, currentInputString, inputNode.current, tagMenuNode.current, input.value]);

  const listboxAdapter = useCallback(params => (
    <div className={params.className} ref={tagMenuNode}>
      <AddTagMenu
        tags={menuTags}
        handleAdd={onTagAdd}
      />
    </div>
  ), [menuTags, onTagAdd]);

  return (
    <div className={className} id={input.name}>
      <div
        className={clsx("relative", {
          "d-none": !isEditing,
          "pointer-events-none": disabled
        })}
      >
        <Autocomplete
          fullWidth
          value={null}
          open={menuIsOpen}
          options={filteredOptions}
          onChange={handleChange}
          renderOption={renderOption}
          filterOptions={filterOptionsInner}
          getOptionLabel={getOptionLabel}
          PopperComponent={currentInputString ? undefined : popperAdapter}
          ListboxComponent={currentInputString ? undefined : listboxAdapter}
          classes={{
            root: clsx("d-inline-flex", classes.root),
            hasPopupIcon: classes.hasPopup,
            hasClearIcon: classes.hasClear,
            listbox: clsx(classes.listbox, fieldClasses.listbox),
            inputRoot: classes.inputWrapper
          }}
          renderInput={({
            InputLabelProps, InputProps, inputProps, ...params
          }) => (
            <FormControl
              {...params}
              variant="standard"
              error={meta?.invalid}
              focused={menuIsOpen}
            >
              {label
              && (
                <InputLabel
                  shrink
                  error={meta?.invalid}
                  classes={{
                    root: fieldClasses.label
                  }}
                >
                  {label}
                </InputLabel>
              )}
              <Input
                {...InputProps}
                disabled={disabled}
                placeholder={placeholder}
                onChange={handleInputChange}
                onFocus={onFocus}
                onBlur={onBlur}
                inputRef={inputNode}
                classes={{
                  underline: fieldClasses.underline,
                  input: clsx(disabled && classes.readonly, fieldClasses.text),
                }}
                inputProps={{
                  ...inputProps,
                  value: inputValue
                }}
                endAdornment={!disabled && (
                  <InputAdornment className={classes.inputEndAdornment} position="end">
                    <Edit color="primary" />
                  </InputAdornment>
                )}
                multiline
              />
              <FormHelperText
                classes={{
                  error: "shakingError"
                }}
              >
                {meta?.error}
              </FormHelperText>
            </FormControl>
          )}
          popupIcon={stubComponent()}
          disableListWrap
          openOnFocus
        />
      </div>
      <div
        className={clsx(classes.inputWrapper, {
          "d-none": isEditing,
          "pointer-events-none": disabled || !tags || !tags.length
        })}
      >
        <FormControl error={meta && meta.invalid} variant="standard" fullWidth>
          <InputLabel
            shrink
            classes={{
              root: fieldClasses.label
            }}
          >
            {label}
          </InputLabel>
          <Typography
            variant="body1"
            component="div"
            onClick={edit}
            className={clsx( classes.editable, {
              [fieldClasses.text]: inputValue,
            })}
          >
            <span className={clsx("centeredFlex flex-wrap", {
              [fieldClasses.placeholder]: !inputValue,
              [classes.placeholder]: !inputValue,
            })}
            >
              {InputValueForRender || "No value"}
            </span>
            {!disabled
            && Boolean(!tags || tags.length)
            && <Edit color="primary" className={classes.hoverIcon} />}
          </Typography>
          <FormHelperText>
            <span className="shakingError">
              {meta.error}
            </span>
          </FormHelperText>
        </FormControl>
      </div>
    </div>
  );
};

export default withStyles(theme => ({
  ...selectStyles(theme),
  ...styles(theme)
}))(
  SimpleTagList
) as any;
