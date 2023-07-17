/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import clsx from "clsx";
import KeyboardArrowRight from "@mui/icons-material/KeyboardArrowRight";
import Checkbox from "@mui/material/Checkbox";
import MenuItem from "@mui/material/MenuItem";
import { MenuTag, TagLike } from "../../../ish-ui/model/Fields";

interface Props<T> {
  classes: any;
  setActiveTag: (tag: MenuTag<T>) => void;
  handleAdd: (tag: MenuTag<T>) => void;
  tag: MenuTag<T>;
}

function AddTagMenuItem<T extends TagLike>({ setActiveTag, tag, classes, handleAdd }: Props<T>) {
  const openSubmenu = () => {
    setActiveTag(tag);
  };

  const toggleChecked = () => {
    handleAdd(tag);
  };
  
  const hasChildren = Boolean(tag.children.length);

  return (
    <MenuItem
      classes={{ root: classes.listItem }}
      onClick={hasChildren ? openSubmenu : toggleChecked}
    >
      <div className={clsx(classes.tagColorDotSmall, "mr-1")} style={{ background: "#" + tag.tagBody.color }} />
      <span className="mr-2 flex-fill">{tag.tagBody.name}</span>
      {hasChildren ? (
        <KeyboardArrowRight className={clsx("d-flex textSecondaryColor", classes.proceedIcon)} />
      ) : (
        <Checkbox classes={{ root: "smallIconButton" }} checked={tag.active} />
      )}
    </MenuItem>
  );
}

export default AddTagMenuItem;
