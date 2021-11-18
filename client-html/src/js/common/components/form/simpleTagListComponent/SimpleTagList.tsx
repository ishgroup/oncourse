/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Tag } from "@api/model";
import { Typography } from "@mui/material";
import ButtonBase from "@mui/material/ButtonBase";
import ClickAwayListener from '@mui/material/ClickAwayListener';
import ListItemText from "@mui/material/ListItemText";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import TextField from "@mui/material/TextField";
import Edit from "@mui/icons-material/Edit";
import Autocomplete from "@mui/lab/Autocomplete";
import clsx from "clsx";
import { WrappedFieldProps } from "redux-form";
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
    inputRoot: {
      "& $tagInput": {
        width: "auto",
        color: "inherit",
        maxWidth: theme.spacing(30)
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

const getInputString = (tags: Tag[], allTags: Tag[]) => (tags?.length && allTags?.length
  ? tags.reduce((acc, tag) => (getFullTag(tag.id, allTags) ? `${acc}#${tag.name} ` : acc), "")
  : "");

const SimpleTagList: React.FC<Props> = props => {
  const {
    input, tags, classes, meta, label = "Tags", disabled, className, fieldClasses = {}
  } = props;

  const [menuIsOpen, setMenuIsOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [currentInputString, setCurrentInputString] = useState("");

  const InputValueForRender = useMemo(() => {
    if (!inputValue || !tags || !tags.length) return "";

    const arrayOfTags = input?.value?.length
      && input.value.map((tag: Tag) => getFullTag(tag.id, tags)).filter(t => t);

    if (!arrayOfTags?.length) return "";

    return arrayOfTags.map((tag: Tag, index) => (
      <span key={tag.id} className={clsx("d-flex align-items-center", index !== arrayOfTags.length - 1 ? "pr-1" : "")}>
        <div key={tag.id} className={clsx(classes.tagColorDotSmall, "mr-0-5")} style={{ background: "#" + tag.color }} />
        {`#${tag.name} `}
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
    () => (tags && input.value ? getMenuTags(tags.filter(t => t.childrenCount > 0), input.value) : []),
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
          const addedTagsMatch = input.value.find(v => v.name === t.tagBody.name);
          if (addedTagsMatch && addedTagsMatch.id !== t.tagBody.id) {
            return;
          }
          updated.push(t.tagBody);
          current.splice(index, 1);
        }
      }
    });

    updated.sort((a, b) => current.indexOf(a.name) - current.indexOf(b.name));
    input.onChange(updated);
    setInputValue(getInputString(updated, tags));
  };

  const onTagAdd = (tag: MenuTag) => {
    const updated = [...input.value];

    const index = updated.findIndex(t => t.id === tag.tagBody.id);

    if (index !== -1) {
      updated.splice(index, 1);
    } else {
      updated.push(tag.tagBody);
    }

    input.onChange(updated);

    setTimeout(() => {
      inputNode?.current?.focus();
    }, 100);
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

  const renderOption = (optionProps, option) => {
    const label = option?.tagBody?.name;
    const highlightedLabel = getHighlightedPartLabel(label, currentInputString);
    return getOptionText(option, highlightedLabel);
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
    if (action === "select-option") {
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
    setCurrentInputString(getCurrentInputString(inputValue, input.value));
  }, [inputValue, input.value]);

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
          value={null}
          open={menuIsOpen}
          options={filteredOptions}
          onChange={handleChange}
          renderOption={renderOption}
          filterOptions={filterOptionsInner}
          getOptionLabel={getOptionLabel}
          PopperComponent={popperAdapter}
          ListboxComponent={currentInputString ? undefined : listboxAdapter}
          classes={{
            listbox: fieldClasses.listbox
          }}
          renderInput={params => (
            <TextField
              {...params}
              margin="none"
              InputLabelProps={{
                classes: {
                  root: fieldClasses.label
                }
              }}
              InputProps={{
                ...params.InputProps,
                classes: {
                  underline: fieldClasses.underline
                },
              }}
              // eslint-disable-next-line react/jsx-no-duplicate-props
              inputProps={{
                ...params.inputProps,
                value: inputValue,
                className: fieldClasses.text
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
              variant="standard"
              multiline
            />
          )}
          popupIcon={stubComponent()}
          disableListWrap
          openOnFocus
        />
      </div>
      <div
        className={clsx({
          "d-none": isEditing,
          "pointer-events-none": disabled || !tags || !tags.length
        })}
      >
        <div className="mw-100 text-truncate">
          <Typography variant="caption" className={fieldClasses.label} color="textSecondary">
            {label}
          </Typography>

          <ListItemText
            classes={{
              root: "pl-0 mb-0",
              primary: "d-flex"
            }}
            primary={(
              <ButtonBase
                onClick={edit}
                className={clsx("overflow-hidden hoverIconContainer", classes.editable)}
                component="div"
              >
                <span
                  className={clsx("overflow-hidden d-flex align-items-center", classes.editable, {
                    [fieldClasses.placeholder ? fieldClasses.placeholder : "placeholderContent"]: !inputValue,
                    [fieldClasses.text]: inputValue,
                  })}
                >
                  {InputValueForRender || "No value"}
                  {!disabled
                  && Boolean(!tags || tags.length)
                  && <Edit className={clsx("editInPlaceIcon hoverIcon", classes.hoverIcon, fieldClasses.placeholder, "mt-0-5")} />}
                </span>
              </ButtonBase>
            )}
          />
        </div>
      </div>
    </div>
  );
};

export default withStyles(theme => ({ ...selectStyles(theme), ...styles(theme) }))(
  SimpleTagList
) as any;
