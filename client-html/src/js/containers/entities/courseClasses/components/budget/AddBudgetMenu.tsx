/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor } from '@api/model';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import { AnyArgFunction } from 'ish-ui';
import React, { useRef } from 'react';

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
  const tutorsRef = useRef(undefined);

  return (
    <>
      <Menu anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={closeAddBudgetMenu}>
        <MenuItem role="expense" onClick={openAddModal}>
          {$t('expense')}
        </MenuItem>
        <MenuItem role="income" onClick={openAddModal}>
          {$t('income2')}
        </MenuItem>
        <MenuItem ref={tutorsRef} role="tutorPay" onClick={openAddModal}>
          {$t('tutor_pay')}
        </MenuItem>
      </Menu>

      <Menu
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        anchorEl={tutorsRef.current}
        open={tutorsMenuOpened}
        onClose={closeAddBudgetMenu}
      >
        {tutors &&
          tutors.map(t => t.contactId !== null && t.roleId !== null && (
            <MenuItem key={t.id} role="expense" onClick={() => openAddTutorModal(t)}>
              {`${t.tutorName} ${t.roleName}`}
            </MenuItem>
          ))}
      </Menu>
    </>
  );
};

export default AddBudgetMenu;
