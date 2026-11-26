/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import CloseIcon from '@mui/icons-material/Close';
import SendIcon from '@mui/icons-material/Send';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import { alpha } from '@mui/material/styles';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useEffect, useRef, useState } from 'react';
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
    borderTop: `3px solid ${theme.palette.primary.main}`,
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
    padding: theme.spacing(1, 1, 1, 2),
    borderBottom: `1px solid ${theme.palette.divider}`
  },
  title: {
    fontSize: "1rem",
    fontWeight: 600
  },
  sparkle: {
    color: theme.palette.primary.main,
    marginRight: theme.spacing(1)
  },
  body: {
    flex: 1,
    overflowY: "auto",
    padding: theme.spacing(2),
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
    maxWidth: "85%",
    padding: theme.spacing(1, 1.5),
    borderRadius: 12,
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere",
    fontSize: "0.875rem",
    lineHeight: 1.5
  },
  assistantBubble: {
    alignSelf: "flex-start",
    backgroundColor: theme.palette.action.hover
  },
  userBubble: {
    alignSelf: "flex-end",
    backgroundColor: alpha(theme.palette.primary.main, 0.12)
  },
  suggestions: {
    display: "flex",
    flexWrap: "wrap",
    gap: theme.spacing(1)
  },
  status: {
    display: "flex",
    alignItems: "center",
    gap: theme.spacing(1),
    color: theme.palette.text.secondary,
    fontSize: "0.875rem"
  },
  footer: {
    display: "flex",
    alignItems: "flex-end",
    gap: theme.spacing(1),
    padding: theme.spacing(1, 1, 1, 2),
    borderTop: `1px solid ${theme.palette.divider}`
  }
}));

interface Props {
  onClose: () => void;
}

const AssistantPanel = ({ onClose }: Props) => {
  const { classes } = useStyles();
  const dispatch = useAppDispatch();

  const messages = useAppSelector(state => state.assistant.messages);
  const pending = useAppSelector(state => state.assistant.pending);
  const error = useAppSelector(state => state.assistant.error);
  const firstName = useAppSelector(state => state.systemUser?.firstName);

  const [text, setText] = useState("");
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const bodyRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

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

  return (
    <Paper
      component="section"
      id={ASSISTANT_PANEL_ID}
      role="dialog"
      aria-modal="false"
      aria-labelledby={ASSISTANT_PANEL_TITLE_ID}
      elevation={8}
      className={classes.panel}
      onKeyDown={onPanelKeyDown}
    >
      <div className={classes.header}>
        <Typography id={ASSISTANT_PANEL_TITLE_ID} component="h2" className={classes.title}>
          <span aria-hidden="true" className={classes.sparkle}>✦</span>
          {$t("oncourse_assistant")}
        </Typography>
        <IconButton size="small" onClick={onClose} aria-label={$t("close")}>
          <CloseIcon fontSize="small" />
        </IconButton>
      </div>

      <div className={classes.body} ref={bodyRef}>
        <div className={bubbleClass("assistant")}>
          {firstName ? $t("assistant_greeting", [firstName]) : $t("assistant_greeting_no_name")}
        </div>

        {!messages.length && (
          <div className={classes.suggestions}>
            {ASSISTANT_SUGGESTIONS.map(key => (
              <Chip
                key={key}
                label={$t(key)}
                color="primary"
                variant="outlined"
                clickable
                disabled={pending}
                onClick={() => send($t(key))}
              />
            ))}
          </div>
        )}

        <div className={classes.log} role="log" aria-live="polite" aria-relevant="additions" aria-label={$t("assistant_conversation")}>
          {messages.map(message => (
            <div key={message.id} className={bubbleClass(message.role)}>
              {message.text}
            </div>
          ))}
        </div>

        <div role="status" className={classes.status}>
          {pending && (
            <>
              <CircularProgress size={16} />
              {$t("assistant_thinking")}
            </>
          )}
        </div>

        {error && (
          <Typography role="alert" color="error" variant="body2">
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
          variant="standard"
          multiline
          maxRows={4}
          fullWidth
        />
        <IconButton
          color="primary"
          onClick={() => send(text)}
          disabled={pending || !text.trim()}
          aria-label={$t("send")}
        >
          <SendIcon />
        </IconButton>
      </div>
    </Paper>
  );
};

export default AssistantPanel;
