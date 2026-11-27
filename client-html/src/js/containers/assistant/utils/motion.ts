/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { keyframes } from 'tss-react';

export const ASSISTANT_EXPAND_MS = 300;

export const ASSISTANT_COLLAPSE_MS = 220;

export const ASSISTANT_APPEAR_MS = 240;

// a quick start that settles slowly, for what comes in
export const ASSISTANT_EASING_IN = "cubic-bezier(0.2, 0, 0, 1)";

// a slow start that speeds up, for what goes away
export const ASSISTANT_EASING_OUT = "cubic-bezier(0.3, 0, 0.8, 0.15)";

const appear = keyframes`
  from {
    opacity: 0;
    transform: translateY(8px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: none;
  }
`;

/**
 * Styles of an item that appears in the panel: a message, a suggestion or a record. It fades in, rising and growing
 * from the origin. Delay it with animationDelay to show items one after another.
 */
export const assistantAppearStyle = (transformOrigin = "bottom center") => ({
  animation: `${appear} ${ASSISTANT_APPEAR_MS}ms ${ASSISTANT_EASING_IN} both`,
  transformOrigin,
  "@media (prefers-reduced-motion: reduce)": {
    animation: "none"
  }
});

const pulse = keyframes`
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
`;

/**
 * Styles of a note that the assistant is still working: it breathes until the reply comes.
 */
export const assistantWorkingStyle = {
  animation: `${pulse} 1.6s ease-in-out infinite`,
  "@media (prefers-reduced-motion: reduce)": {
    animation: "none"
  }
};

const prefersReducedMotion = () =>
  typeof window.matchMedia === "function" && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

/**
 * @return false where the change is shown at once: browsers without the Web Animations API, and users who asked
 * their system for less motion
 */
export const canAnimate = (element: Element): boolean =>
  Boolean(element) && typeof element.animate === "function" && !prefersReducedMotion();

export interface Box {
  left: number;
  top: number;
  width: number;
  height: number;
}

/**
 * The box in the window and the colour of the button the panel opens from and collapses into.
 */
export interface MorphSource {
  rect: Box;
  borderRadius: number;
  backgroundColor: string;
}

export const morphSourceOf = (element: HTMLElement): MorphSource => {
  if (!element) {
    return null;
  }
  const {
    left, top, width, height
  } = element.getBoundingClientRect();
  const style = getComputedStyle(element);
  return {
    rect: { left, top, width, height },
    borderRadius: parseFloat(style.borderTopLeftRadius) || 0,
    backgroundColor: style.backgroundColor
  };
};

/**
 * Keyframes that turn the source's box, rounded corners and colour into the element's own: the element grows out of
 * the source. Reversed, it shrinks back into it.
 */
export const morphKeyframes = (source: MorphSource, element: HTMLElement): Keyframe[] => {
  // what is still running would distort the element's box
  element.getAnimations?.().forEach(animation => animation.cancel());

  const target = element.getBoundingClientRect();
  const style = getComputedStyle(element);
  const scaleX = source.rect.width / target.width;
  const scaleY = source.rect.height / target.height;
  // the scale shrinks the corners as well, larger radii give the source's own rounded corners
  const radius = source.borderRadius;

  return [
    {
      transformOrigin: "top left",
      transform: `translate(${source.rect.left - target.left}px, ${source.rect.top - target.top}px) scale(${scaleX}, ${scaleY})`,
      borderRadius: `${radius / scaleX}px / ${radius / scaleY}px`,
      backgroundColor: source.backgroundColor
    },
    {
      transformOrigin: "top left",
      transform: "none",
      borderRadius: style.borderRadius,
      backgroundColor: style.backgroundColor
    }
  ];
};
