/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import makeStyles from "@material-ui/core/styles/makeStyles";
import clsx from "clsx";
import { State } from "../../../../../../reducers/state";
import { MenuTag } from "../../../../../../model/tags";

const useStyles = makeStyles(theme => ({
  tagColorDotExtraSmall: {
    width: theme.spacing(1),
    minWidth: theme.spacing(1),
    height: theme.spacing(1),
    minHeight: theme.spacing(1),
    borderRadius: "100%"
  },
}));

const getSortedArrayOfColors = (menuTags, colors, colorsLength, result) => {
  let i = 0;
  while (i <= menuTags.length - 1 && colorsLength.length !== 0) {
    if (result.length >= 3 || colorsLength.length === 0) break;
    if (colors.includes(menuTags[i].tagBody.id.toString())) {
      result.push(menuTags[i].tagBody.color);
      colorsLength.length = colorsLength.length - 1;
    }

    if (result.length >= 3 || colorsLength.length === 0) break;

    if (menuTags[i].children.length) {
      getSortedArrayOfColors(menuTags[i].children, colors, colorsLength, result);
    }
    ++i;
  }

  return result;
};

const TagDotRenderer = ({ colors = [], dotsWrapperStyle, menuTags, tagsOrder }) => {
  const classes = useStyles();

  const [sortedTags, setSortedTags] = useState([]);

  useEffect(() => {
    const savedTagsOrder = tagsOrder;
    const filteredTags = menuTags.filter((tag: MenuTag) => tag.children.length);

    const filteredSortedTags = [];

    if (savedTagsOrder.length) {
      savedTagsOrder.forEach((tagId: number) => {
        const tag = filteredTags.find(elem => elem.tagBody.id === tagId);
        if (tag) {
          const indexOfTag = filteredTags.indexOf(tag);

          const [foundElement] = filteredTags.splice(indexOfTag, 1);
          filteredSortedTags.push(foundElement);
        }
      });
    }

    setSortedTags(filteredSortedTags.concat(filteredTags));
  }, [menuTags, tagsOrder]);

  const colorsLength = colors.length > 3 ? 3 : colors.length;
  const getSortedColors = useCallback(
    () => getSortedArrayOfColors(sortedTags || menuTags, colors, { length: colorsLength }, []), [colors, menuTags, sortedTags]
  );

  const sortedColors = useMemo(() => (colors && colors.length
    ? getSortedColors()
    : []), [colors, menuTags, sortedTags]);

  return (
    <div className={clsx("centeredFlex", dotsWrapperStyle)}>
      {sortedColors.map((color: string) => (
        <div key={color} className={clsx(classes.tagColorDotExtraSmall, "mr-0-5")} style={{ background: "#" + color }} />
      ))}
    </div>
  );
};

const mapStateToPops = (state: State) => ({
  menuTags: state.list.menuTags,
  tagsOrder: state.list.records.tagsOrder,
});

export default connect(mapStateToPops, null)(TagDotRenderer);