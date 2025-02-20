/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { getFormValues } from 'redux-form';
import { IAction } from '../../../../common/actions/IshAction';
import { getPluralSuffix } from '../../../../common/utils/strings';
import { State } from '../../../../reducers/state';
import BulkEditCogwheelOption from '../../common/components/BulkEditCogwheelOption';
import PayslipGenerateCogwheelAction from '../../payslips/components/PayslipGenerateCogwheelAction';
import { closeMergeContactsSuccess, getMergeContacts } from '../actions';
import MergeContactsModal from './MergeContactsModal';

const ContactCogWheel = props => {
  const {
    selection,
    menuItemClass,
    closeMenu,
    getMergeContacts,
    mergeContactValues,
    closeMergeContactsSuccess,
    showConfirm
  } = props;

  // const selectedAndNotNew = useMemo(() => selection.length >= 1 && selection[0] !== "NEW", [selection]);

  const isMergeEnable = useMemo(() => selection.length === 2 && selection[0] !== "NEW", [selection]);

  const onClickMerge = useCallback(() => {
    closeMergeContactsSuccess();
    if (selection.length === 2) {
      getMergeContacts(selection[0], selection[1]);
    }
  }, []);

  const onMergeClose = useCallback(() => {
    closeMenu();
    closeMergeContactsSuccess();
  }, []);

  const contactsCountLabel = useMemo(() => `${selection.length} selected contact${getPluralSuffix(selection.length)}`, [
    selection.length
  ]);

  return (
    <>
      <MergeContactsModal
        onClose={onMergeClose}
        opened={Boolean(mergeContactValues && mergeContactValues.mergeData.mergeLines.length)}
      />

      <PayslipGenerateCogwheelAction
        entity="Contact"
        generateLabel={`Generate tutor pay${selection.length ? ` for ${contactsCountLabel}` : ""}`}
        closeMenu={closeMenu}
        showConfirm={showConfirm}
        menuItemClass={menuItemClass}
        selection={selection}
      />

      <MenuItem disabled={!isMergeEnable} className={menuItemClass} onClick={onClickMerge}>
        {$t('merge')}
      </MenuItem>
      <BulkEditCogwheelOption {...props} />
    </>
  );
};

const mapStateToProps = (state: State) => ({
  mergeContactValues: getFormValues("MergeContactsForm")(state),
  search: state.list.searchQuery
});

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  getMergeContacts: (contactA: string, contactB: string) => dispatch(getMergeContacts(contactA, contactB)),
  closeMergeContactsSuccess: () => dispatch(closeMergeContactsSuccess())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ContactCogWheel);
