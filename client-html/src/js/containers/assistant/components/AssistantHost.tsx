/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useLayoutEffect, useRef } from 'react';
import { ASSISTANT_HOST_ID } from '../constants';

/*
 * The assistant button and panel are rendered into a host element at the end of the body. While a full screen
 * edit view is open, the host moves into it: a record's edit view is a page too, but it is a modal dialog,
 * which covers everything outside it and hides it from assistive technologies with aria-hidden.
 * Other dialogs and drawers cover the assistant while they are open, as they cover the page.
 */

let host: HTMLDivElement = null;

// the open full screen edit views, the last one is on top
const slots: HTMLElement[] = [];

const placeHost = () => {
  // a slot already taken off the page with its edit view, before its cleanup has run, can't show the host
  const target = slots.filter(slot => slot.isConnected).pop() || document.body;

  if (!host.isConnected || host.parentElement !== target) {
    // moving an element blurs what is focused in it, typing in the panel continues
    const focused = host.contains(document.activeElement) ? document.activeElement as HTMLElement : null;
    target.appendChild(host);
    focused?.focus();
  }

  // a modal opened while the host was in the body has hidden it with the rest of the page
  host.removeAttribute("aria-hidden");
};

export const getAssistantHost = (): HTMLDivElement => {
  if (!host) {
    host = document.createElement("div");
    host.id = ASSISTANT_HOST_ID;
  }
  if (!host.isConnected) {
    placeHost();
  }
  return host;
};

/**
 * Shows the assistant in the full screen dialog this is rendered in.
 */
export const AssistantHostSlot = () => {
  const ref = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    const slot = ref.current;
    getAssistantHost();
    slots.push(slot);
    placeHost();

    return () => {
      slots.splice(slots.indexOf(slot), 1);
      placeHost();
    };
  }, []);

  return <div ref={ref} />;
};
