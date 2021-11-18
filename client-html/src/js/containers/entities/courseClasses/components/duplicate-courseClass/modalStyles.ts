/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import createStyles from "@mui/styles/createStyles";
import { AppTheme } from "../../../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
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
  daysInput: {
    maxWidth: theme.spacing(6)
  },
  dateTime: {
    minWidth: theme.spacing(32.5)
  }
});

export default styles;
