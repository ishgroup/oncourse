/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import IconButton from "@mui/material/IconButton";
import Launch from "@mui/icons-material/Launch";
import EmojiPeopleIcon from '@mui/icons-material/EmojiPeople';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import { IconButtonTypeMap } from "@mui/material";
import { useAppDispatch } from "../../utils/hooks";
import { setSwipeableDrawerSelection, toggleSwipeableDrawer } from "../layout/swipeable-sidebar/actions";

interface Props {
  link?: string | number;
  linkHandler?: (arg: string | number) => void;
  linkColor?: IconButtonTypeMap["props"]["color"];
  clickHandler?: any;
  className?: string;
  disabled?: boolean;
}

interface HeaderContactLinkProps {
  id: number;
  name?: string;
}

export const HeaderContactTitle: React.FC<HeaderContactLinkProps> = (
  {
   id,
   name
 }
) => {
  const dispatch = useAppDispatch();
  
  const setSelectedContact = () => {
    dispatch(toggleSwipeableDrawer());
    dispatch(setSwipeableDrawerSelection(id));
  };

  return (
    <div className="d-inline-flex-center">
      {name}
      <IconButton disabled={!id} size="small" color="primary" onClick={setSelectedContact}>
        <EmojiPeopleIcon fontSize="inherit" />
      </IconButton>
    </div>
  );
};

interface ContactLinkAdornmentProps {
  id: number;
}

export const ContactLinkAdornment: React.FC<ContactLinkAdornmentProps> = ({
  id
}) => {
  const dispatch = useAppDispatch();
  const setSelectedContact = () => {
    dispatch(toggleSwipeableDrawer());
    dispatch(setSwipeableDrawerSelection(id));
  };

  return (
    <span>
      <IconButton
        disabled={!id}
        onClick={setSelectedContact}
        color="primary"
        classes={{
          root: "inputAdornmentButton"
        }}
      >
        <EmojiPeopleIcon className="inputAdornmentIcon" color="inherit" />
      </IconButton>
    </span>
  );
};

export const LinkAdornment: React.FC<Props> = ({
  link,
  linkColor = "primary",
  linkHandler,
  clickHandler,
  className,
  disabled
}) => {
  const onClick = () => (clickHandler ? clickHandler() : linkHandler(link));

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
  const onClick = e => (clickHandler ? clickHandler(e) : linkHandler(link));

  return (
    <span className={className}>
      <IconButton
        disabled={disabled}
        onClick={onClick}
        classes={{
          root: "inputAdornmentButton ml-0-5"
        }}
        color="inherit"
        size="small"
      >
        <SettingsOutlinedIcon color="inherit" />
      </IconButton>
    </span>
  );
};
