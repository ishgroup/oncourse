/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MenuItem } from '@mui/material';
import React from 'react';

export const renderSelectItemsWithEmpty = (
  items: any[],
  valueKey?: string,
  labelKey?: string
) => [
  <MenuItem key="empty" value="" className="text-placeholder">
    No value
  </MenuItem>
].concat((items || []).map((i) => (
  <MenuItem key={i[valueKey] || i} value={i[valueKey] || i}>
    {i[labelKey] || i}
  </MenuItem>
)));

export const renderSelectItems = (
  items: any[],
  valueKey?: string,
  labelKey?: string
) => (items || []).map((i) => (
  <MenuItem key={i[valueKey] || i} value={i[valueKey] || i}>
    {i[labelKey] || i}
  </MenuItem>
));
