/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStyles } from "@mui/styles";
import { alpha } from "@mui/material/styles";
import { AppTheme } from  "ish-ui";

export const paymentPlanStyles = (theme: AppTheme) => createStyles({
  root: {
    backgroundColor: "inherit",
    padding: 0
  },
  deleteIcon: {
    color: alpha(theme.palette.text.primary, 0.2),
    fontSize: "18px",
    width: theme.spacing(4),
    height: theme.spacing(4),
    padding: 0
  },
  step: {
    "&:first-child $stepButton": {
      marginTop: theme.spacing(-1),
      paddingTop: theme.spacing(1)
    }
  },
  stepButton: {}
});
