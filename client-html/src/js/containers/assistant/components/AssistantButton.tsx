/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import Fab from '@mui/material/Fab';
import clsx from 'clsx';
import { alpha } from '@mui/material/styles';
import { makeAppStyles } from 'ish-ui';
import React, { useImperativeHandle, useRef } from 'react';
import { ASSISTANT_BUTTON_ID, ASSISTANT_BUTTON_SIZE } from '../constants';
import { ASSISTANT_EASING_IN } from '../utils/motion';
import { useDraggable } from '../utils/useDraggable';

const LABEL_CLASS = "assistant-button-label";

const EXPAND = `200ms ${ASSISTANT_EASING_IN}`;

const useStyles = makeAppStyles()(theme => ({
  button: {
    position: "fixed",
    // below app bars, drawers and dialogs, which cover it; full screen edit views host it (see AssistantHost)
    zIndex: theme.zIndex.speedDial,
    height: ASSISTANT_BUTTON_SIZE,
    minWidth: ASSISTANT_BUTTON_SIZE,
    padding: 0,
    // a rounded rectangle, as in the SST-260 mocks
    borderRadius: 10,
    textTransform: "none",
    fontWeight: 600,
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.primary.contrastText,
    boxShadow: `0 4px 12px ${alpha(theme.palette.primary.main, 0.35)}`,
    // it can be dragged, also with a finger
    cursor: "grab",
    touchAction: "none",
    userSelect: "none",
    transition: `padding ${EXPAND}, background-color 150ms`,
    // only the icon until the user points at the button or moves to it with the keyboard
    "&:hover, &.Mui-focusVisible": {
      padding: theme.spacing(0, 2),
      [`& .${LABEL_CLASS}`]: {
        maxWidth: 240,
        marginLeft: theme.spacing(1),
        opacity: 1
      }
    },
    "&:hover": {
      backgroundColor: theme.palette.primary.dark
    },
    "@media (prefers-reduced-motion: reduce)": {
      transition: "none",
      [`& .${LABEL_CLASS}`]: {
        transition: "none"
      }
    }
  },
  dragging: {
    cursor: "grabbing"
  },
  sparkle: {
    fontSize: "1.25rem",
    lineHeight: 1
  },
  label: {
    display: "inline-block",
    maxWidth: 0,
    marginLeft: 0,
    opacity: 0,
    overflow: "hidden",
    whiteSpace: "nowrap",
    transition: `max-width ${EXPAND}, margin-left ${EXPAND}, opacity ${EXPAND}`
  }
}));

interface Props {
  onClick: () => void;
}

const AssistantButton = React.forwardRef<HTMLButtonElement, Props>(({ onClick }, ref) => {
  const { classes } = useStyles();

  const buttonRef = useRef<HTMLButtonElement>(null);
  useImperativeHandle(ref, () => buttonRef.current);

  const {
    position, dragging, handlers, wasDragged
  } = useDraggable("button", buttonRef);

  // the button grows towards the middle of the window when it shows its label, so the label stays in sight
  const left = window.innerWidth - position.right - ASSISTANT_BUTTON_SIZE;
  const growsRight = left + ASSISTANT_BUTTON_SIZE / 2 < window.innerWidth / 2;

  return (
    <Fab
      ref={buttonRef}
      id={ASSISTANT_BUTTON_ID}
      variant="extended"
      className={clsx(classes.button, dragging && classes.dragging)}
      style={growsRight ? { left, bottom: position.bottom } : { right: position.right, bottom: position.bottom }}
      data-grows={growsRight ? "right" : "left"}
      onClick={() => {
        // the click that ends a drag doesn't open the panel
        if (!wasDragged()) {
          onClick();
        }
      }}
      aria-haspopup="dialog"
      {...handlers}
    >
      <span aria-hidden="true" className={classes.sparkle}>✦</span>
      <span className={clsx(classes.label, LABEL_CLASS)}>{$t("ask_oncourse_ai")}</span>
    </Fab>
  );
});

export default AssistantButton;
