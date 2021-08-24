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

import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import makeStyles from "@material-ui/core/styles/makeStyles";
import clsx from "clsx";
import { State } from "../../../../../../reducers/state";

const useStyles = makeStyles(theme => ({
  tagColorDotExtraSmall: {
    width: theme.spacing(1),
    minWidth: theme.spacing(1),
    height: theme.spacing(1),
    minHeight: theme.spacing(1),
    borderRadius: "100%"
  },
}));

const getSortedArrayOfColors = (menuTags, colors, result) => {
  let i = 0;
  while (i <= menuTags.length - 1) {
    if (result.length >= 3) break;
    if (colors.includes(menuTags[i].tagBody.id.toString())) {
      result.push(menuTags[i].tagBody.color);
    }

    if (menuTags[i].children.length) {
      getSortedArrayOfColors(menuTags[i].children, colors, result);
    }
    ++i;
  }

  return result;
};

const TagDotRenderer = ({ colors = [], dotsWrapperStyle, menuTags }) => {
  const classes = useStyles();

  const getSortedColors = useCallback(() => getSortedArrayOfColors(menuTags, colors, []), [colors, menuTags]);

  const sortedColors = useMemo(() => (colors && colors.length
    ? getSortedColors()
    : []), [colors, menuTags]);

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
});

export default connect(mapStateToPops, null)(TagDotRenderer);