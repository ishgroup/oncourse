/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import clsx from 'clsx';
import { AnyArgFunction, ShowConfirmCaller } from 'ish-ui';
import React, { memo } from 'react';
import { useAppDispatch, useAppSelector } from '../../../../common/utils/hooks';
import { ListState } from '../../../../model/common/ListView';
import { ListActionEntity } from '../../../../model/entities/common';
import { checkContactsDelete } from '../../contacts/actions';
import { bulkDeleteEntityRecordsRequest } from '../actions';

interface BulkDeleteCogwheelOptionProps {
  menuItemClass: string;
  closeMenu: AnyArgFunction;
  selection: ListState['selection'];
  showConfirm: ShowConfirmCaller;
  entity: ListActionEntity;
}

const BulkDeleteCogwheelOption = memo<BulkDeleteCogwheelOptionProps>(({
  entity,
  menuItemClass, 
  closeMenu, 
  selection, 
  showConfirm
}) => {
  const hasAql = useAppSelector(state => state.list.searchQuery
    && (state.list.searchQuery.search || state.list.searchQuery.filter || state.list.searchQuery.tagGroups.length));
  const { search, filter, tagGroups } = useAppSelector(state => state.list.searchQuery);
  const dispatch = useAppDispatch();

  const onBulkEditClick = () => {
    if (entity === 'Contact') {
      dispatch(checkContactsDelete(selection));
      return;
    }
    showConfirm({
      onConfirm: () => {
        dispatch(bulkDeleteEntityRecordsRequest(
          entity,
          {
            ids: selection as any,
            search,
            filter,
            tagGroups
          }));
        closeMenu();
      },
      confirmMessage: "Records will be deleted permanently. This action can not be undone",
      confirmButtonText: "Delete anyway"
    });
  };

  return hasAql || selection.length > 1 ? (
    <MenuItem className={clsx(menuItemClass, "errorColor")} onClick={onBulkEditClick} disabled={!selection.length && !hasAql}>
      {$t('bulk_delete')}
    </MenuItem>
  ) : null;
});

export default BulkDeleteCogwheelOption;
