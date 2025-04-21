/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { VerticalSplit, ViewHeadline } from '@mui/icons-material';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import $t from '@t';
import clsx from 'clsx';
import React from 'react';

export default ({
 threeColumn, switchLayout, classes, disabled
}) => (
  <Tooltip title={$t('change_columns_mode')}>
    <span>
      <IconButton
        color="inherit"
        classes={{
              root: clsx(classes.actionsBarButton, classes.customIconButton),
              disabled: classes.buttonDisabledOpacity
            }}
        onClick={switchLayout}
        className={classes.customIconButton}
        disabled={disabled}
      >
        <VerticalSplit
          className={threeColumn ? classes.switcherActive : classes.switcherDisabled}
          style={{ color: threeColumn ? classes.switcherActive : classes.switcherDisabled }}
        />
        <ViewHeadline
          className={threeColumn ? classes.switcherDisabled : classes.switcherActive}
          style={{ marginLeft: "-0.2em", color: threeColumn ? classes.switcherDisabled : classes.switcherActive }}
        />
      </IconButton>
    </span>
  </Tooltip>
    );
