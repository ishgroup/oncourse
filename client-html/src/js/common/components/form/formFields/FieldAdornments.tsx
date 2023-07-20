/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import IconButton from "@mui/material/IconButton";
import Launch from "@mui/icons-material/Launch";
import EmojiPeopleIcon from '@mui/icons-material/EmojiPeople';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import { IconButtonTypeMap } from "@mui/material";
import { useAppDispatch } from "../../../utils/hooks";
import { setSwipeableDrawerSelection, toggleSwipeableDrawer } from "../../layout/swipeable-sidebar/actions";

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
