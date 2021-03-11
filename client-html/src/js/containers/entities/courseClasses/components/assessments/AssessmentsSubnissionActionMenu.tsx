/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import MenuItem from "@material-ui/core/MenuItem";
import IconButton from "@material-ui/core/IconButton";
import ExpandMore from "@material-ui/icons/ExpandMore";
import Menu from "@material-ui/core/Menu";
import { CourseClassTutor } from "@api/model";
import { getStudentAttendanceLabel } from "../attendance/utils";
import AttendanceIcon from "../attendance/AttendanceIcon";

interface Props {
  onChange: (tutorName: string, studentName: string) => void;
  student: any;
  items: CourseClassTutor[];
  className?: string;
}

const AssesmentsSubmissionActionsMenu: React.FC<Props> = ({
    onChange, className, items, student
  }) => {
  const [anchorEl, setAnchorEl] = React.useState(null);

  const openMenu = useCallback(e => {
    setAnchorEl(e.currentTarget);
  }, []);

  const closeMenu = useCallback(() => {
    setAnchorEl(null);
  }, []);

  const onItemClick = (e, tutor) => {
    e.preventDefault();
    onChange(tutor.tutorName, student.studentName);
    closeMenu();
  };

  const menuItems = items.map((tutor: CourseClassTutor) => (
    <MenuItem
      key={tutor.contactId}
      role={tutor.contactId}
      onClick={e => onItemClick(e, tutor)}
    >
      {/* <AttendanceIcon type={k} /> */}
      {/* &nbsp; */}
      {tutor.tutorName}
      {/* {getStudentAttendanceLabel(k)} */}
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
          <MenuItem disabled>Select Assessor...</MenuItem>
          {menuItems}
        </Menu>
      </div>
    </div>
  );
};

export default AssesmentsSubmissionActionsMenu;