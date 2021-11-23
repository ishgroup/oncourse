/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import createStyles from "@mui/styles/createStyles";
import { alpha } from '@mui/material/styles';

const styles = theme =>
  createStyles({
    notesContainer: {
      padding: theme.spacing(0.5, 3, 2.5, 3)
    },
    showMore: {
      borderTop: `1px solid ${theme.palette.divider}`
    },
    dateInfo: {
      color: alpha(theme.palette.text.primary, 0.5),
      fontSize: "12px"
    },
    leftOffset: {
      paddingLeft: "2px"
    }
  });

export default styles;
