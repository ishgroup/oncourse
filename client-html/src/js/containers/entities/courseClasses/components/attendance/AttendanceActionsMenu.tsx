/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AttendanceType, TutorAttendanceType } from "@api/model";
import ExpandMore from "@mui/icons-material/ExpandMore";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import React, { useCallback } from "react";
import { AttendanceGridType } from "../../../../../model/entities/CourseClass";
import AttendanceIcon from "./AttendanceIcon";
import { getStudentAttendanceLabel, getTrainingPlanAttendanceLabel } from "./utils";

interface Props {
  onChange: (value: AttendanceType) => void;
  label: string;
  className?: string;
  type: AttendanceGridType;
}

const getMenuItemsBaseByType = (type: AttendanceGridType) => {
  switch (type) {
    default:
    case "Student":
      return AttendanceType;
  }
};

const AttendanceActionsMenu: React.FC<Props> = ({
 onChange, label, className, type
}) => {
  const [anchorEl, setAnchorEl] = React.useState(null);

  const openMenu = useCallback(e => {
    setAnchorEl(e.currentTarget);
  }, []);

  const closeMenu = useCallback(() => {
    setAnchorEl(null);
  }, []);

  // No callback hook is used to not cache onChange handler
  const onItemClick = e => {
    onChange(e.currentTarget.getAttribute("role"));
    closeMenu();
  };

  const menuItems = type === "Training plan"
      ? (["Unmarked", "Attended"] as AttendanceType[]).map((t, i) => (
        <MenuItem key={i} role={t} onClick={onItemClick}>
          <AttendanceIcon type={t} />
          &nbsp;
          {getTrainingPlanAttendanceLabel(t)}
        </MenuItem>
        ))
      : Object.keys(getMenuItemsBaseByType(type)).map((k: AttendanceType | TutorAttendanceType) => (
        <MenuItem key={k} role={k} onClick={onItemClick}>
          <AttendanceIcon type={k} />
          &nbsp;
          {getStudentAttendanceLabel(k)}
        </MenuItem>
        ));

  return (
    <div className={className}>
      <IconButton
        onClick={openMenu}
        aria-controls="attendance-action-menu"
        aria-haspopup="true"
        className="p-0"
      >
        <ExpandMore className="addButtonColor" />
      </IconButton>
      <div className="centeredFlex">
        <Menu id="attendance-action-menu" anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={closeMenu}>
          <MenuItem disabled>{label}</MenuItem>
          {menuItems}
        </Menu>
      </div>
    </div>
  );
};

export default AttendanceActionsMenu;
