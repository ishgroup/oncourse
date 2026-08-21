/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Replacement for react-window v1's `areEqual`, dropped in v2.
 *
 * Same idea as the original — a shallow compare that looks one level into the
 * `style` prop, which react-window rebuilds on every scroll — extended to also
 * look into v2's `ariaAttributes`, which is likewise a fresh object each render
 * and would otherwise defeat the memo entirely.
 */

const shallowDiffers = (prev: any, next: any): boolean => {
  if (prev === next) return false;
  if (!prev || !next) return true;
  for (const key in prev) {
    if (!(key in next)) return true;
  }
  for (const key in next) {
    if (prev[key] !== next[key]) return true;
  }
  return false;
};

const areEqual = (prevProps: any, nextProps: any): boolean => {
  const { style: prevStyle, ariaAttributes: prevAria, ...prevRest } = prevProps;
  const { style: nextStyle, ariaAttributes: nextAria, ...nextRest } = nextProps;

  return !shallowDiffers(prevStyle, nextStyle)
    && !shallowDiffers(prevAria, nextAria)
    && !shallowDiffers(prevRest, nextRest);
};

export default areEqual;
