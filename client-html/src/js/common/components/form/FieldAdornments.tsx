/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import IconButton from "@mui/material/IconButton";
import Launch from "@mui/icons-material/Launch";
import SettingsIcon from "@mui/icons-material/Settings";
import { IconButtonTypeMap } from "@mui/material";

interface Props {
  link?: string | number;
  linkHandler?: (arg: string | number) => void;
  linkColor?: IconButtonTypeMap["props"]["color"];
  clickHandler?: any;
  className?: string;
  disabled?: boolean;
}

export const LinkAdornment: React.FC<Props> = ({
  link,
  linkColor = "secondary",
  linkHandler,
  clickHandler,
  className,
  disabled
}) => {
  const onClick = useCallback(() => (clickHandler ? clickHandler() : linkHandler(link)), [
    link,
    linkHandler,
    clickHandler
  ]);

  return (
    <span className={className}>
      <IconButton
        disabled={disabled || (!link && link !== 0)}
        onClick={onClick}
        color={linkColor}
        classes={{
          root: "inputAdornmentButton"
        }}
      >
        <Launch className="inputAdornmentIcon" color="inherit" />
      </IconButton>
    </span>
  );
};

export const SettingsAdornment: React.FC<Props> = ({
 link, linkHandler, clickHandler, className, disabled 
}) => {
  const onClick = useCallback(e => (clickHandler ? clickHandler(e) : linkHandler(link)), [
    link,
    linkHandler,
    clickHandler
  ]);

  return (
    <span className={className}>
      <IconButton
        disabled={disabled}
        onClick={onClick}
        classes={{
          root: "inputAdornmentButton"
        }}
      >
        <SettingsIcon className="inputAdornmentIcon" />
      </IconButton>
    </span>
  );
};
