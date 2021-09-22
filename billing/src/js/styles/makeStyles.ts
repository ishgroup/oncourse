/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createMakeStyles } from 'tss-react';
import { AppTheme } from '../models/Theme';
import { useTheme } from '../themes/ishTheme';

const { makeStyles } = createMakeStyles<AppTheme>({ useTheme });

export const makeAppStyles = makeStyles;
