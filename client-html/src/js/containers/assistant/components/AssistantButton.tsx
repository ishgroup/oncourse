/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import Fab from '@mui/material/Fab';
import { makeAppStyles } from 'ish-ui';
import React from 'react';
import { APP_BAR_HEIGHT } from '../../../constants/Config';
import { ASSISTANT_BUTTON_ID } from '../constants';

const useStyles = makeAppStyles()(theme => ({
  button: {
    position: "fixed",
    right: theme.spacing(3),
    // above the bottom app bar of lists, so it doesn't cover its buttons
    bottom: APP_BAR_HEIGHT + 16,
    // below app bars, drawers and dialogs, which cover it; full screen edit views host it (see AssistantHost)
    zIndex: theme.zIndex.speedDial,
    textTransform: "none",
    backgroundColor: theme.palette.primary.main,
    // white text on the orange accent is not readable enough
    color: theme.palette.getContrastText(theme.palette.primary.main),
    "&:hover": {
      backgroundColor: theme.palette.primary.dark
    }
  },
  sparkle: {
    marginRight: theme.spacing(1)
  }
}));

interface Props {
  onClick: () => void;
}

const AssistantButton = React.forwardRef<HTMLButtonElement, Props>(({ onClick }, ref) => {
  const { classes } = useStyles();

  return (
    <Fab
      ref={ref}
      id={ASSISTANT_BUTTON_ID}
      variant="extended"
      size="medium"
      className={classes.button}
      onClick={onClick}
      aria-haspopup="dialog"
    >
      <span aria-hidden="true" className={classes.sparkle}>✦</span>
      {$t("ask_oncourse_ai")}
    </Fab>
  );
});

export default AssistantButton;
