/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useRef } from "react";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";
import { AnyArgFunction } from  "ish-ui";
import { CourseClassTutor } from "@api/model";

interface Props {
  anchorEl: Element;
  tutorsMenuOpened: boolean;
  closeAddBudgetMenu: AnyArgFunction;
  openAddModal: AnyArgFunction;
  openAddTutorModal: AnyArgFunction;
  tutors: CourseClassTutor[];
}

const AddBudgetMenu: React.FC<Props> = ({
  anchorEl,
  tutorsMenuOpened,
  closeAddBudgetMenu,
  openAddModal,
  openAddTutorModal,
  tutors
}) => {
  const tutorsRef = useRef();

  return (
    <>
      <Menu anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={closeAddBudgetMenu}>
        <MenuItem role="expense" onClick={openAddModal}>
          Expense
        </MenuItem>
        <MenuItem role="income" onClick={openAddModal}>
          Income
        </MenuItem>
        <MenuItem ref={tutorsRef} role="tutorPay" onClick={openAddModal}>
          Tutor Pay
        </MenuItem>
      </Menu>

      <Menu
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        anchorEl={tutorsRef.current}
        open={tutorsMenuOpened}
        onClose={closeAddBudgetMenu}
      >
        {tutors &&
          tutors.map(t => t.contactId !== null && t.roleId !== null &&(
            <MenuItem key={t.id} role="expense" onClick={() => openAddTutorModal(t)}>
              {`${t.tutorName} ${t.roleName}`}
            </MenuItem>
          ))}
      </Menu>
    </>
  );
};

export default AddBudgetMenu;
