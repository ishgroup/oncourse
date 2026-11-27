/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useLayoutEffect, useRef, useState } from 'react';
import {
  AssistantDraggable,
  AssistantPosition,
  clampPosition,
  DEFAULT_ASSISTANT_POSITION,
  getStoredPosition,
  storePosition
} from './position';

// a press that moves less is a click
const DRAG_THRESHOLD = 4;

// controls in the part to drag by keep working as usual
const CONTROLS = "button, a, input, textarea";

interface Drag {
  pointerId: number;
  x: number;
  y: number;
  from: AssistantPosition;
  last: AssistantPosition;
  dragging: boolean;
}

/**
 * Lets the user move a fixed element of the onCourse Assistant over the window with the mouse or a finger.
 * The position is kept inside the window, also when it is resized, and stored in local storage when a drag ends.
 *
 * @param item which element it is, each has its own position
 * @param elementRef the element, measured to keep it inside the window
 * @param disabled true to keep the element where it is
 * @return the position, the pointer handlers for the part to drag by, whether it is being dragged, and wasDragged,
 * which tells once whether the last press moved the element, so its click isn't a click
 */
export const useDraggable = (item: AssistantDraggable, elementRef: React.RefObject<HTMLElement>, disabled = false) => {
  const [position, setPosition] = useState<AssistantPosition>(() => getStoredPosition(item) || DEFAULT_ASSISTANT_POSITION);
  const [size, setSize] = useState<{ width: number; height: number }>(null);
  const [dragging, setDragging] = useState(false);
  const drag = useRef<Drag>(null);
  const dragged = useRef(false);

  useLayoutEffect(() => {
    // measured again on every resize, which also keeps the element inside the smaller window
    const measure = () => {
      const element = elementRef.current;
      if (element) {
        setSize({ width: element.offsetWidth, height: element.offsetHeight });
      }
    };
    measure();
    window.addEventListener("resize", measure);
    return () => window.removeEventListener("resize", measure);
  }, []);

  const inWindow = (value: AssistantPosition) => (size ? clampPosition(value, size.width, size.height) : value);

  const current = inWindow(position);

  const onPointerDown = (e: React.PointerEvent<HTMLElement>) => {
    const control = (e.target as Element).closest(CONTROLS);
    if (disabled || e.button !== 0 || !e.isPrimary || (control && control !== e.currentTarget)) {
      return;
    }
    drag.current = { pointerId: e.pointerId, x: e.clientX, y: e.clientY, from: current, last: current, dragging: false };
    dragged.current = false;
  };

  const onPointerMove = (e: React.PointerEvent<HTMLElement>) => {
    const started = drag.current;
    if (!started || started.pointerId !== e.pointerId) {
      return;
    }
    const dx = e.clientX - started.x;
    const dy = e.clientY - started.y;
    if (!started.dragging) {
      if (Math.abs(dx) < DRAG_THRESHOLD && Math.abs(dy) < DRAG_THRESHOLD) {
        return;
      }
      started.dragging = true;
      dragged.current = true;
      setDragging(true);
      // the element follows the pointer even when it is moved faster than the element
      try {
        e.currentTarget.setPointerCapture?.(e.pointerId);
      } catch {
        // the pointer is gone already, the drag ends with its next event
      }
    }
    started.last = inWindow({ right: started.from.right - dx, bottom: started.from.bottom - dy });
    setPosition(started.last);
  };

  const onPointerEnd = (e: React.PointerEvent<HTMLElement>) => {
    const started = drag.current;
    if (!started || started.pointerId !== e.pointerId) {
      return;
    }
    drag.current = null;
    if (started.dragging) {
      setDragging(false);
      storePosition(item, started.last);
    }
  };

  const wasDragged = () => {
    const was = dragged.current;
    dragged.current = false;
    return was;
  };

  return {
    position: current,
    dragging,
    wasDragged,
    handlers: disabled ? {} : {
      onPointerDown,
      onPointerMove,
      onPointerUp: onPointerEnd,
      onPointerCancel: onPointerEnd
    }
  };
};
