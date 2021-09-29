/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Tag } from "@api/model";
import Chip from "@material-ui/core/Chip";
import { InputAdornment } from "@material-ui/core";
import CreateIcon from "@material-ui/icons/Create";
import ClickAwayListener from '@material-ui/core/ClickAwayListener';
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import TextField from "@material-ui/core/TextField";
import Autocomplete from "@material-ui/lab/Autocomplete";
import clsx from "clsx";
import { WrappedFieldProps } from "redux-form";
import { getAllMenuTags } from "../../../../containers/tags/utils";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { MenuTag } from "../../../../model/tags";
import { stubComponent } from "../../../utils/common";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import { getMenuTags } from "../../list-view/utils/listFiltersUtils";
import { selectStyles } from "../formFields/SelectCustomComponents";
import AddTagMenu from "./AddTagMenu";

const styles = theme =>
  createStyles({
    listContainer: {
      marginLeft: "-2px"
    },
    inputEndAdornment: {
      fontSize: "18px",
      color: theme.palette.primary.main,
      opacity: 0.5,
      display: "none",
    },
    inputWrapper: {
      paddingRight: "0",
      "&:hover $inputEndAdornment": {
        display: "flex",
      },
    },
    isEditing: {
      "& $inputEndAdornment": {
        display: "flex",
        borderBottom: "none",
        opacity: 1,
      },
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
    noTagsLabel: {},
    input: {
      minWidth: "150px",
    },
    inputRoot: {
      "& $input": {
        minWidth: "150px",
      },
      flexWrap: "wrap",
      "& $tagInput": {
        color: "inherit",
        maxWidth: theme.spacing(30),
      }
    },
    tagInput: {},
    hoverIcon: {
      visibility: "hidden",
    },
    editable: {
      color: theme.palette.text.primaryEditable,
      fontWeight: 400,
      "&$editable &:hover $hoverIcon": {
        visibility: "visible",
        color: theme.palette.primary.main,
        fontSize: "1.2rem",
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
    chipLabel: {
      fontSize: "16px",
      fontWeight: 400,
      paddingLeft: theme.spacing(0.5),
    },
    hasPopupIcon: {
      "&$hasClearIcon $inputRoot": {
        paddingRight: 0
      }
    },
    hasClearIcon: {},
  });

interface Props extends WrappedFieldProps {
  showConfirm: ShowConfirmCaller;
  tags: Tag[];
  classes?: any;
  fieldClasses?: any;
  disabled?: boolean;
  className?: string;
  label?: string;
}

const endTagRegex = /#\s*[^\w\d]*$/;

const getCurrentInputString = (input, formTags: Tag[]) => {
  let substr = input;

  formTags && formTags.forEach(t => {
    substr = substr.replace("#" + t.name, "").trim();
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

const SimpleTagList: React.FC<Props> = props => {
  const {
   input, tags, classes, meta, label = "Tags", disabled, className, fieldClasses = {}
  } = props;

  const [menuIsOpen, setMenuIsOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [currentInputString, setCurrentInputString] = useState("");

  useEffect(() => {
    if (meta.invalid && !isEditing) {
      setIsEditing(true);
    }
  }, [meta.invalid]);

  const inputNode = useRef<any>();
  const tagMenuNode = useRef<any>();

  const menuTags = useMemo(
    () => (tags && input.value ? getMenuTags(tags.filter(t => t.childrenCount > 0), input.value) : []),
    [tags, input.value]
  );

  const allMenuTags = useMemo(
    () => getAllMenuTags(menuTags),
    [menuTags]
  );

  const onTagAdd = (tag: MenuTag) => {
    const updated = [...input.value];

    const index = updated.findIndex(t => t.id === tag.tagBody.id);

    if (index !== -1) {
      updated.splice(index, 1);
    } else {
      updated.push(tag.tagBody);
    }

    input.onChange(updated);

    if (inputValue && inputValue !== "#") setInputValue("#");

    setTimeout(() => {
      inputNode?.current?.focus();
    }, 100);
  };

  const onDeleteTag = (tag: Tag) => {
    const updated = [...input.value];

    const index = updated.findIndex(t => t.id === tag.id);

    if (index !== -1) updated.splice(index, 1);

    input.onChange(updated);

    setTimeout(() => {
      inputNode?.current?.focus();
    }, 100);

    if (menuIsOpen) {
      setTimeout(() => setMenuIsOpen(false), 0);
      setTimeout(() => setMenuIsOpen(true), 0);
    }
  };

  const filterOptions = item => !item.children.length && !input.value.some(v => v.id === item.tagBody.id) && item.tagBody.name
    .toLowerCase()
    .trim()
    .startsWith(currentInputString.toLowerCase().trim());

  const filterOptionsInner = options => options.filter(filterOptions);

  const filteredOptions = allMenuTags.filter(filterOptions);

  const getOptionText = (option, label) => (
    <span>
      {`${option.path ? option.path + " / " : ""}`}
      {' '}
      {label}
    </span>
  );

  const getOptionLabel = op => op.path;

  const renderOption = option => {
    const label = option.tagBody.name;
    const highlightedLabel = getHighlightedPartLabel(label, currentInputString);
    return getOptionText(option, highlightedLabel);
  };

  const handleInputChange = e => {
    setInputValue(e.target.value);
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
    if (!isEditing) setIsEditing(true);
    if (!endTagRegex.test(inputValue)) {
      setInputValue(inputValue + " #");
    }
  };

  const onBlur = () => {
    if (currentInputString) {
      exit();
    }
  };

  const onTagListBlur = () => {
    if (document.activeElement !== inputNode.current) {
      exit();
    }
  };

  const handleChange = (e, value, action) => {
    if (action === "select-option") {
      const newItem = value.splice(-1, 1)[0];

      onTagAdd(newItem);
    }
  };

  useEffect(() => {
    setCurrentInputString(getCurrentInputString(inputValue, input.value));
  }, [inputValue, input.value]);

  const popperAdapter = useCallback(params => {
    if (currentInputString) {
      return <div {...params} />;
    }

    const position = tagMenuNode.current && tagMenuNode.current.clientWidth
    && inputNode.current.clientWidth < tagMenuNode.current.clientWidth
      ? { right: 0 }
      : { left: inputNode.current.offsetLeft };

    return (
      <div id="popper" style={{ ...position, position: "absolute", zIndex: 2 }}>
        <ClickAwayListener onClickAway={onTagListBlur}>
          {params.children}
        </ClickAwayListener>
      </div>
      );
  }, [allMenuTags, inputValue, currentInputString, inputNode.current, tagMenuNode.current, input.value, input.value.length]);

  const listboxAdapter = useCallback(params => (
    <div className={params.className} ref={tagMenuNode}>
      <AddTagMenu
        tags={menuTags}
        handleAdd={onTagAdd}
      />
    </div>
  ), [menuTags, input.value, onTagAdd, input.value.length]);

  const renderTags = (arrayOfTags: Tag[], getTagProps: any) => {
    if (!arrayOfTags || !tags || !tags.length) return "";

    const arrayOfFullTags = input?.value?.length
      && input.value.map((tag: Tag) => getFullTag(tag.id, tags)).filter(t => t);

    if (!arrayOfFullTags?.length) return "";

    return arrayOfFullTags.map((tag: Tag, index) => (
      <Chip
        {...getTagProps({ index })}
        key={tag.id}
        avatar={(
          <span className={clsx("d-flex align-items-center", index !== arrayOfTags.length - 1 ? "pr-1" : "")}>
            <div key={tag.id} className={clsx(classes.tagColorDotSmall, "mr-0-5")} style={{ background: "#" + tag.color }} />
          </span>
        )}
        label={`#${tag.name} `}
        onDelete={() => onDeleteTag(tag)}
        classes={{
          label: classes.chipLabel,
        }}
      />
    ));
  };

  return (
    <div className={className} id={input.name}>
      <div
        className={clsx("relative", {
        "pointer-events-none": disabled
      })}
      >
        <Autocomplete
          multiple
          value={[...input.value]}
          open={menuIsOpen}
          options={filteredOptions}
          onChange={handleChange}
          renderOption={renderOption}
          renderTags={renderTags}
          filterOptions={filterOptionsInner}
          getOptionLabel={getOptionLabel}
          PopperComponent={popperAdapter}
          ListboxComponent={currentInputString ? undefined : listboxAdapter}
          classes={{
            // @ts-ignore
            hasPopupIcon: classes.hasPopupIcon,
            hasClearIcon: classes.hasClearIcon,
            inputRoot: classes.inputRoot,
            input: classes.input,
            listbox: fieldClasses.listbox,
          }}
          renderInput={params => (
            <TextField
              multiline
              {...params}
              margin="none"
              InputLabelProps={{
                classes: {
                  root: fieldClasses.label
                }
              }}
              InputProps={{
                ...params.InputProps,
                onFocus,
                classes: {
                  root: clsx(fieldClasses.text, classes.inputWrapper, isEditing && classes.isEditing),
                  underline: fieldClasses.underline
                },
                endAdornment: !disabled && (
                  <InputAdornment position="end" className={classes.inputEndAdornment}>
                    <CreateIcon />
                  </InputAdornment>
                )
              }}
              // eslint-disable-next-line react/jsx-no-duplicate-props
              inputProps={{
              ...params.inputProps,
              value: inputValue
            }}
              error={meta && meta.invalid}
              helperText={(
                <span className="shakingError">
                  {meta.error}
                </span>
            )}
              onChange={handleInputChange}
              onFocus={onFocus}
              onBlur={onBlur}
              inputRef={inputNode}
              label={label}
            />
        )}
          popupIcon={stubComponent()}
          disableListWrap
          openOnFocus
        />
      </div>
    </div>
  );
};

export default withStyles(theme => ({ ...selectStyles(theme), ...styles(theme) }))(
  SimpleTagList
) as any;
