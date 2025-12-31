/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import React, { memo, useCallback } from 'react';
import { useSelector } from 'react-redux';
import { State } from '../../../../reducers/state';

export default memo<{ menuItemClass?, closeMenu?, toggleBulkEditDrawer?, selection? }>(({
 menuItemClass, closeMenu, toggleBulkEditDrawer, selection
}) => {
  const hasAql = useSelector<State, any>(state => state.list.searchQuery
    && (state.list.searchQuery.search || state.list.searchQuery.filter || state.list.searchQuery.tagGroups.length));

  const onBulkEditClick = useCallback(() => {
    toggleBulkEditDrawer();
    closeMenu();
  }, []);

  return (
    <>
      <MenuItem className={menuItemClass} onClick={onBulkEditClick} disabled={!selection.length && !hasAql}>
        {$t('bulk_edit2')}
      </MenuItem>
    </>
  );
});
