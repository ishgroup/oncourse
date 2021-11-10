/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { createMakeStyles } from 'tss-react';
import { AppTheme } from '../../model/common/Theme';
import { useTheme } from '../themes/ishTheme';

const { makeStyles } = createMakeStyles<AppTheme>({ useTheme });

export const makeAppStyles = makeStyles;
