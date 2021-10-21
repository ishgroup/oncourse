/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MenuItem } from '@mui/material';
import React from 'react';
import { SelectRenderArgs } from '../models/Common';

export const renderSelectItems = (
  {
    items = [],
    valueKey,
    labelKey,
    labelCondition
  }: SelectRenderArgs
) => (items || []).map((i) => (
  <MenuItem key={i[valueKey] || i} value={i[valueKey] || i}>
    {labelCondition ? labelCondition(i) : (i[labelKey] || i)}
  </MenuItem>
));

export const renderSelectItemsWithEmpty = (
  args: SelectRenderArgs
) => [
  <MenuItem key="empty" value="" className="text-placeholder">
    No value
  </MenuItem>
].concat(renderSelectItems(args));
