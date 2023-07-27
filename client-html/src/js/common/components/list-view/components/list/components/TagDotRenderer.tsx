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

import React from "react";
import clsx from "clsx";
import { makeAppStyles } from  "ish-ui";

const useStyles = makeAppStyles(theme => ({
  tagColorDotExtraSmall: {
    width: theme.spacing(1),
    minWidth: theme.spacing(1),
    height: theme.spacing(1),
    minHeight: theme.spacing(1),
    borderRadius: "100%"
  },
}));

const TagDotRenderer = ({
                          colors, className = null
                        }) => {
  const classes = useStyles();

  return (
    <div className={clsx("centeredFlex", className)}>
      {colors.map((color: string, index) => (
        <div key={color + index} className={clsx(classes.tagColorDotExtraSmall, "mr-0-5")}
             style={{background: "#" + color}}/>
      ))}
    </div>
  );
};

export default TagDotRenderer;