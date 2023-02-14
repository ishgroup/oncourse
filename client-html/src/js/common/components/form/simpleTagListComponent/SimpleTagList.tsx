/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState 
} from "react";
import { Tag } from "@api/model";
import {
  InputAdornment, MenuItem, Select
} from "@mui/material";
import ClickAwayListener from '@mui/material/ClickAwayListener';
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Autocomplete from "@mui/material/Autocomplete";
import clsx from "clsx";
import { Edit } from "@mui/icons-material";
import { getAllMenuTags } from "../../../../containers/tags/utils";
import { MenuTag } from "../../../../model/tags";
import { stubComponent } from "../../../utils/common";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import getCaretCoordinates from "../../../utils/getCaretCoordinates";
import { getMenuTags } from "../../list-view/utils/listFiltersUtils";
import { selectStyles } from "../formFields/SelectCustomComponents";
import AddTagMenu from "./AddTagMenu";
import { IS_JEST } from "../../../../constants/EnvironmentConstants";
import EditInPlaceFieldBase from "../formFields/EditInPlaceFieldBase";
import { TagsFieldProps } from "../../../../model/common/Fields";

const styles = theme =>
  createStyles({
    inputWrapper: {
      "&:hover $inputEndAdornment": {
        visibility: "visible",
      }
    },
    inputEndAdornment: {
      visibility: 'hidden',
      display: "flex",
      color: theme.palette.primary.main,
      alignItems: "flex-end",
      alignSelf: "flex-end",
      marginBottom: "4px",
      opacity: 0.5
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
    invalid: {}
  });

const endTagRegex = /#\s*[^\w\d]*$/;

const getCurrentInputString = (input, formTagIds: number[] = [], allMenuTags: MenuTag[] = []) => {
  let substr = input;

  formTagIds?.forEach(id => {
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

const SimpleTagList = ({
   input,
   tags,
   classes,
   placeholder,
   labelAdornment,
   meta,
   label = "Tags",
   disabled,
   className,
   warning,
   fieldClasses = {}
}: TagsFieldProps) => {
  const [menuIsOpen, setMenuIsOpen] = useState(false);
  const [activeTag, setActiveTag] = useState<MenuTag>(null);
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
    setActiveTag(null);

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
    setTimeout(() => {
      if (document.activeElement !== inputNode.current) {
        exit();
        synchronizeTags();
      }
    }, 60);
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

  useEffect(() => {
    if (meta.invalid && !isEditing) {
      setIsEditing(true);
    }
  }, [meta.invalid]);

  useEffect(() => {
    if (activeTag) {
      const activeUpdated = allMenuTags.find(t => t.tagBody.id === activeTag.tagBody.id);
      if (activeUpdated) {
        setActiveTag(activeUpdated);
      }
    }
  }, [allMenuTags]);

  const popperAdapter = useCallback(params => {
    if (currentInputString || !inputNode.current) {
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
        activeTag={activeTag}
        setActiveTag={setActiveTag}
      />
    </div>
  ), [menuTags, activeTag, setActiveTag, onTagAdd]);

  return (
    <div className={className} id={input.name}>
      <div
        className={clsx("relative", {
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
          PopperComponent={currentInputString ? undefined : popperAdapter}
          ListboxComponent={currentInputString ? undefined : listboxAdapter}
          classes={{
            root: classes.root,
            hasPopupIcon: classes.hasPopup,
            hasClearIcon: classes.hasClear,
            listbox: clsx(classes.listbox, fieldClasses.listbox),
            inputRoot: classes.inputWrapper
          }}
          renderInput={({
            InputLabelProps, InputProps, inputProps, ...params
          }) => (
            <EditInPlaceFieldBase
              {...params}
              name={input.name}
              value={input.value}
              error={meta.error}
              invalid={meta?.invalid}
              label={label}
              warning={warning}
              disabled={disabled}
              fieldClasses={fieldClasses}
              shrink={Boolean(label || input.value)}
              labelAdornment={labelAdornment}
              placeholder={placeholder}
              editIcon={<Edit fontSize="inherit" />}
              InputProps={{
                ...InputProps,
                onChange: handleInputChange,
                onFocus,
                onBlur,
                inputProps: {
                  ...inputProps,
                  ref: ref => {
                    (inputProps as any).ref.current = ref;
                    inputNode.current = ref;
                  },
                  value: inputValue
                },
                multiline: !IS_JEST
              }}
              CustomInput={!isEditing
                ? <Select
                  inputRef={(inputProps as any).ref}
                  onFocus={edit}
                  value="stub"
                  className={classes.inputWrapper}
                  classes={{ select: "d-flex flex-wrap cursor-text" }}
                  endAdornment={
                    <InputAdornment
                      position="end"
                      className={classes.inputEndAdornment}>
                        <Edit />
                      </InputAdornment>
                  }
                  IconComponent={null}
                >
                  <MenuItem value="stub">
                    {InputValueForRender || <span className="placeholderContent">No value</span>}
                  </MenuItem>
                </Select>
                : null}
            />)
          }
          popupIcon={stubComponent()}
          disableListWrap
          openOnFocus
        />
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
