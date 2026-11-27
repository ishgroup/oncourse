/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import CloseIcon from '@mui/icons-material/Close';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import { alpha, useTheme } from '@mui/material/styles';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../../../common/utils/hooks/redux';
import { APP_BAR_HEIGHT } from '../../../constants/Config';
import { sendAssistantMessage } from '../actions';
import {
  ASSISTANT_MAX_TEXT,
  ASSISTANT_PANEL_ID,
  ASSISTANT_PANEL_TITLE_ID,
  ASSISTANT_PANEL_WIDTH,
  ASSISTANT_SUGGESTIONS
} from '../constants';
import { AssistantRole } from '../model';
import {
  ASSISTANT_COLLAPSE_MS,
  ASSISTANT_EASING_IN,
  ASSISTANT_EASING_OUT,
  ASSISTANT_EXPAND_MS,
  assistantAppearStyle,
  assistantWorkingStyle,
  canAnimate,
  MorphSource,
  morphKeyframes
} from '../utils/motion';
import { useDraggable } from '../utils/useDraggable';

// the greeting and the suggestions come in one after another as the panel opens
const GREETING_DELAY_MS = ASSISTANT_EXPAND_MS / 2;
const SUGGESTION_DELAY_MS = GREETING_DELAY_MS + 80;
const SUGGESTION_STAGGER_MS = 50;

// the look follows the SST-260 mocks: a white card, grey assistant bubbles, orange user bubbles, outlined chips
const useStyles = makeAppStyles()(theme => ({
  panel: {
    position: "fixed",
    right: theme.spacing(3),
    bottom: APP_BAR_HEIGHT + 16,
    width: ASSISTANT_PANEL_WIDTH,
    maxWidth: "calc(100vw - 32px)",
    height: `min(640px, calc(100vh - ${APP_BAR_HEIGHT + 48}px))`,
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
    // above page content and app bars, below drawers and dialogs; full screen edit views host it (see AssistantHost)
    zIndex: theme.zIndex.appBar + 1,
    borderRadius: 12,
    border: `1px solid ${alpha(theme.palette.text.primary, 0.06)}`,
    boxShadow: `0 12px 32px ${alpha(theme.palette.common.black, 0.12)}, 0 2px 6px ${alpha(theme.palette.common.black, 0.06)}`,
    [theme.breakpoints.down("sm")]: {
      left: theme.spacing(1),
      right: theme.spacing(1),
      bottom: theme.spacing(1),
      width: "auto",
      maxWidth: "none",
      height: `calc(100vh - ${APP_BAR_HEIGHT + 16}px)`
    }
  },
  header: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    minHeight: 56,
    padding: theme.spacing(1, 1.5, 1, 2.5),
    borderBottom: `1px solid ${theme.palette.divider}`
  },
  headerButton: {
    color: theme.palette.text.secondary
  },
  // the panel is moved by its header, also with a finger
  draggableHeader: {
    cursor: "grab",
    touchAction: "none",
    userSelect: "none"
  },
  dragging: {
    cursor: "grabbing"
  },
  collapsing: {
    pointerEvents: "none"
  },
  appear: assistantAppearStyle(),
  title: {
    fontSize: "1.0625rem",
    fontWeight: 600,
    color: theme.palette.text.primary
  },
  sparkle: {
    color: theme.palette.primary.main,
    marginRight: theme.spacing(1)
  },
  body: {
    flex: 1,
    overflowY: "auto",
    padding: theme.spacing(2.5),
    display: "flex",
    flexDirection: "column",
    gap: theme.spacing(1.5)
  },
  log: {
    display: "flex",
    flexDirection: "column",
    gap: theme.spacing(1.5)
  },
  bubble: {
    maxWidth: "88%",
    padding: theme.spacing(1.25, 2),
    borderRadius: 12,
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere",
    fontSize: "0.9rem",
    lineHeight: 1.55
  },
  assistantBubble: {
    alignSelf: "flex-start",
    color: theme.palette.text.primary,
    backgroundColor: alpha(theme.palette.text.primary, 0.03),
    border: `1px solid ${alpha(theme.palette.text.primary, 0.07)}`
  },
  userBubble: {
    alignSelf: "flex-end",
    color: theme.palette.primary.contrastText,
    backgroundColor: theme.palette.primary.main,
    fontWeight: 500
  },
  // a new message comes from its side of the conversation
  assistantAppear: assistantAppearStyle("bottom left"),
  userAppear: assistantAppearStyle("bottom right"),
  suggestions: {
    display: "flex",
    flexWrap: "wrap",
    gap: theme.spacing(1)
  },
  suggestion: {
    height: 32,
    borderRadius: 16,
    fontSize: "0.8125rem",
    color: theme.palette.text.primary,
    backgroundColor: theme.palette.background.paper,
    borderColor: alpha(theme.palette.text.primary, 0.18)
  },
  status: {
    display: "block",
    fontStyle: "italic",
    color: theme.palette.text.secondary,
    fontSize: "0.875rem"
  },
  working: assistantWorkingStyle,
  footer: {
    display: "flex",
    alignItems: "flex-end",
    gap: theme.spacing(1.5),
    padding: theme.spacing(2, 2.5),
    borderTop: `1px solid ${theme.palette.divider}`
  },
  input: {
    "& .MuiOutlinedInput-root": {
      borderRadius: 8,
      fontSize: "0.9rem",
      backgroundColor: theme.palette.background.paper
    }
  },
  send: {
    height: 40,
    minWidth: 64,
    borderRadius: 8,
    textTransform: "none",
    fontWeight: 600,
    flexShrink: 0
  }
}));

interface Props {
  onClose: () => void;
  // the panel shrinks back into the button, then onCollapsed is called
  collapsing?: boolean;
  // the button it grows out of when it opens, null to show it at once
  expandFrom?: MorphSource;
  // the button it shrinks into when it closes, null to hide it at once
  collapseInto?: MorphSource;
  onCollapsed?: () => void;
}

const AssistantPanel = ({
  onClose, collapsing = false, expandFrom = null, collapseInto = null, onCollapsed = () => undefined
}: Props) => {
  const { classes } = useStyles();
  const dispatch = useAppDispatch();
  const theme = useTheme();

  const messages = useAppSelector(state => state.assistant.messages);
  const pending = useAppSelector(state => state.assistant.pending);
  const error = useAppSelector(state => state.assistant.error);
  const firstName = useAppSelector(state => state.systemUser?.firstName);

  const [text, setText] = useState("");
  const panelRef = useRef<HTMLElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const bodyRef = useRef<HTMLDivElement>(null);
  // messages already there when the panel opened don't come in again
  const shownMessages = useRef(new Set(messages.map(message => message.id)));

  // on narrow screens the panel takes the whole width and stays at the bottom
  const fullWidth = useMediaQuery(theme.breakpoints.down("sm"));
  const draggable = !fullWidth && !collapsing;
  const { position, dragging, handlers } = useDraggable("panel", panelRef, !draggable);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  useLayoutEffect(() => {
    const panel = panelRef.current;
    if (!expandFrom || !canAnimate(panel)) {
      return;
    }
    panel.animate(morphKeyframes(expandFrom, panel), { duration: ASSISTANT_EXPAND_MS, easing: ASSISTANT_EASING_IN });
    // the content fades in once the panel is big enough to show it
    Array.from(panel.children).forEach(child => child.animate([{ opacity: 0 }, { opacity: 1 }], {
      duration: ASSISTANT_EXPAND_MS / 2,
      delay: ASSISTANT_EXPAND_MS / 2,
      fill: "backwards"
    }));
  }, []);

  useLayoutEffect(() => {
    const panel = panelRef.current;
    if (!collapsing) {
      return undefined;
    }
    if (!collapseInto || !canAnimate(panel)) {
      onCollapsed();
      return undefined;
    }
    const shrink = panel.animate(morphKeyframes(collapseInto, panel).reverse(), {
      duration: ASSISTANT_COLLAPSE_MS,
      easing: ASSISTANT_EASING_OUT,
      fill: "forwards"
    });
    const fadeOut = Array.from(panel.children).map(child => child.animate([{ opacity: 1 }, { opacity: 0 }], {
      duration: ASSISTANT_COLLAPSE_MS / 2,
      fill: "forwards"
    }));
    shrink.onfinish = () => onCollapsed();
    // opened again before it has collapsed
    return () => [shrink, ...fadeOut].forEach(animation => animation.cancel());
  }, [collapsing]);

  useEffect(() => {
    if (bodyRef.current) {
      bodyRef.current.scrollTop = bodyRef.current.scrollHeight;
    }
  }, [messages.length, pending, error]);

  const send = (value: string) => {
    const message = value.trim();
    if (!message || pending) {
      return;
    }
    dispatch(sendAssistantMessage(message));
    setText("");
    inputRef.current?.focus();
  };

  const onInputKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey && !e.nativeEvent.isComposing) {
      e.preventDefault();
      send(text);
    }
  };

  const onPanelKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Escape") {
      e.stopPropagation();
      onClose();
    }
  };

  const bubbleClass = (role: AssistantRole) => clsx(classes.bubble, role === "user" ? classes.userBubble : classes.assistantBubble);

  const appearClass = (role: AssistantRole) => (role === "user" ? classes.userAppear : classes.assistantAppear);

  return (
    <Paper
      component="section"
      ref={panelRef}
      id={ASSISTANT_PANEL_ID}
      role="dialog"
      aria-modal="false"
      aria-labelledby={ASSISTANT_PANEL_TITLE_ID}
      elevation={8}
      className={clsx(classes.panel, collapsing && classes.collapsing)}
      style={fullWidth ? undefined : { right: position.right, bottom: position.bottom }}
      onKeyDown={onPanelKeyDown}
    >
      <div className={clsx(classes.header, draggable && classes.draggableHeader, dragging && classes.dragging)} {...handlers}>
        <Typography id={ASSISTANT_PANEL_TITLE_ID} component="h2" className={classes.title}>
          <span aria-hidden="true" className={classes.sparkle}>✦</span>
          {$t("oncourse_assistant")}
        </Typography>
        <IconButton size="small" onClick={onClose} aria-label={$t("close")} className={classes.headerButton}>
          <CloseIcon fontSize="small" />
        </IconButton>
      </div>

      <div className={classes.body} ref={bodyRef}>
        <div className={clsx(bubbleClass("assistant"), classes.assistantAppear)} style={{ animationDelay: `${GREETING_DELAY_MS}ms` }}>
          {firstName ? $t("assistant_greeting", [firstName]) : $t("assistant_greeting_no_name")}
        </div>

        {!messages.length && (
          <div className={classes.suggestions}>
            {ASSISTANT_SUGGESTIONS.map((key, index) => (
              <Chip
                key={key}
                label={$t(key)}
                variant="outlined"
                clickable
                disabled={pending}
                onClick={() => send($t(key))}
                className={clsx(classes.suggestion, classes.appear)}
                style={{ animationDelay: `${SUGGESTION_DELAY_MS + index * SUGGESTION_STAGGER_MS}ms` }}
              />
            ))}
          </div>
        )}

        <div className={classes.log} role="log" aria-live="polite" aria-relevant="additions" aria-label={$t("assistant_conversation")}>
          {messages.map(message => (
            <div
              key={message.id}
              className={clsx(bubbleClass(message.role), !shownMessages.current.has(message.id) && appearClass(message.role))}
            >
              {message.text}
            </div>
          ))}
        </div>

        <div role="status">
          {pending && (
            <span className={clsx(classes.status, classes.appear)}>
              <span className={classes.working}>{$t("assistant_thinking")}</span>
            </span>
          )}
        </div>

        {error && (
          <Typography key={error} role="alert" color="error" variant="body2" className={classes.appear}>
            {error}
          </Typography>
        )}
      </div>

      <div className={classes.footer}>
        <TextField
          value={text}
          onChange={e => setText(e.target.value)}
          onKeyDown={onInputKeyDown}
          inputRef={inputRef}
          placeholder={$t("describe_a_course_class_or_schedule")}
          slotProps={{ htmlInput: { "aria-label": $t("message_to_assistant"), maxLength: ASSISTANT_MAX_TEXT } }}
          variant="outlined"
          size="small"
          className={classes.input}
          multiline
          maxRows={4}
          fullWidth
        />
        <Button
          variant="contained"
          color="primary"
          disableElevation
          className={classes.send}
          onClick={() => send(text)}
          disabled={pending || !text.trim()}
        >
          {$t("send")}
        </Button>
      </div>
    </Paper>
  );
};

export default AssistantPanel;
