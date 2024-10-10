/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AppTheme } from 'ish-ui';

const styles = (theme: AppTheme) => ({
  root: {
    maxWidth: "unset",
    minWidth: "1200px"
  },
  dialogContent: {
    "&:first-child": {
      paddingTop: theme.spacing(1)
    }
  },
  timetableContainer: {
    background: theme.palette.background.default,
    borderRadius: theme.shape.borderRadius
  },
});

export default styles;
