/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { useAppDispatch, useAppSelector } from '../../../common/utils/hooks/redux';
import { ASSISTANT_AVAILABLE_KEY } from '../../../constants/Config';
import { setAssistantOpen } from '../actions';
import { ASSISTANT_BUTTON_SIZE } from '../constants';
import { MorphSource, morphSourceOf } from '../utils/motion';
import AssistantButton from './AssistantButton';
import { getAssistantHost } from './AssistantHost';
import AssistantPanel from './AssistantPanel';

/**
 * The button as it is when nobody points at it: the square with the icon, at the end it grows from.
 */
const restingButton = (button: HTMLButtonElement, source: MorphSource): MorphSource => {
  const { rect } = source;
  return {
    ...source,
    rect: {
      left: button.dataset.grows === "right" ? rect.left : rect.left + rect.width - ASSISTANT_BUTTON_SIZE,
      top: rect.top + rect.height - ASSISTANT_BUTTON_SIZE,
      width: ASSISTANT_BUTTON_SIZE,
      height: ASSISTANT_BUTTON_SIZE
    }
  };
};

const AssistantLauncher = () => {
  const dispatch = useAppDispatch();

  const available = useAppSelector(state => state.userPreferences[ASSISTANT_AVAILABLE_KEY] === "true");
  const open = useAppSelector(state => state.assistant.open);

  const buttonRef = useRef<HTMLButtonElement>(null);
  // the panel grows out of the button and collapses back into it
  const expandFrom = useRef<MorphSource>(null);
  const collapseInto = useRef<MorphSource>(null);

  // the panel stays until it has collapsed into the button
  const [panelShown, setPanelShown] = useState(open);
  const showPanel = available && (open || panelShown);
  const wasPanelShown = useRef(showPanel);

  useEffect(() => {
    if (!available && open) {
      dispatch(setAssistantOpen(false));
    }
    if (!available) {
      setPanelShown(false);
    }
  }, [available, open]);

  useEffect(() => {
    if (open) {
      setPanelShown(true);
    }
  }, [open]);

  useEffect(() => {
    // focus returns to the button once the panel has collapsed into it
    if (wasPanelShown.current && !showPanel) {
      buttonRef.current?.focus();
    }
    wasPanelShown.current = showPanel;
  }, [showPanel]);

  if (!available) {
    return null;
  }

  const openPanel = () => {
    const button = buttonRef.current;
    expandFrom.current = morphSourceOf(button);
    collapseInto.current = button ? restingButton(button, expandFrom.current) : null;
    dispatch(setAssistantOpen(true));
  };

  return createPortal(
    showPanel
      ? (
        <AssistantPanel
          onClose={() => dispatch(setAssistantOpen(false))}
          collapsing={!open}
          expandFrom={expandFrom.current}
          collapseInto={collapseInto.current}
          onCollapsed={() => setPanelShown(false)}
        />
      )
      : <AssistantButton ref={buttonRef} onClick={openPanel} />,
    getAssistantHost()
  );
};

export default AssistantLauncher;
