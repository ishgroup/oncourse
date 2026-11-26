/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { useAppDispatch, useAppSelector } from '../../../common/utils/hooks/redux';
import { ASSISTANT_AVAILABLE_KEY } from '../../../constants/Config';
import { setAssistantOpen } from '../actions';
import AssistantButton from './AssistantButton';
import { getAssistantHost } from './AssistantHost';
import AssistantPanel from './AssistantPanel';

/**
 * The Ask onCourse AI button and the assistant panel it opens, on every page for users the assistant is available to,
 * including full screen record edit views (see AssistantHost).
 */
const AssistantLauncher = () => {
  const dispatch = useAppDispatch();

  const available = useAppSelector(state => state.userPreferences[ASSISTANT_AVAILABLE_KEY] === "true");
  const open = useAppSelector(state => state.assistant.open);

  const buttonRef = useRef<HTMLButtonElement>(null);
  const wasOpen = useRef(open);

  useEffect(() => {
    if (!available && open) {
      dispatch(setAssistantOpen(false));
    }
  }, [available, open]);

  useEffect(() => {
    // focus returns to the button when the panel closes
    if (wasOpen.current && !open) {
      buttonRef.current?.focus();
    }
    wasOpen.current = open;
  }, [open]);

  if (!available) {
    return null;
  }

  return createPortal(
    open
      ? <AssistantPanel onClose={() => dispatch(setAssistantOpen(false))} />
      : <AssistantButton ref={buttonRef} onClick={() => dispatch(setAssistantOpen(true))} />,
    getAssistantHost()
  );
};

export default AssistantLauncher;
