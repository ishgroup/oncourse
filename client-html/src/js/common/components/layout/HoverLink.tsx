/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { ReactNode } from "react";
import clsx from "clsx";
import IconButton from "@mui/material/IconButton";
import Launch from "@mui/icons-material/Launch";
import { openInternalLink } from "../../utils/links";
import { makeAppStyles } from  "ish-ui";

const useStyles = makeAppStyles(theme => ({
  root: {
    display: "inline-flex",
    alignItems: "center",
    position: "relative",
    overflow: "hidden",
    willChange: "padding-right",
    "&:hover": {
      paddingRight: theme.spacing(3),
      cursor: "pointer",
      "& $link": {
        transition: "transform 0.2s ease-in-out, opacity 0.2s ease-in-out",
        transform: "translateX(0)",
        opacity: 1
      }
    }
  },
  link: {
    fontSize: "inherit",
    opacity: 0,
    right: 0,
    position: "absolute",
    padding: theme.spacing(0.5),
    transform: "translateX(-16px)",
    willChange: "transform, opacity"
  }
}));

interface Props {
  className?: string;
  link: string;
  children: ReactNode;
}

const HoverLink = ({children, link, className}: Props) => {
  const classes = useStyles();

  return link ? (
    <div className={clsx(className, classes.root)} onClick={() => openInternalLink(link)}>
      {children}
      <IconButton className={classes.link} color="primary">
        <Launch fontSize="inherit"/>
      </IconButton>
    </div>
  ) : <>{children}</>;
};

export default HoverLink;