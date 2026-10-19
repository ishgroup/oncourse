/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Breakpoint, useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import React from 'react';

/**
 * Drop-in replacement for `@mui/material/Hidden`, removed in MUI v9.
 *
 * Reproduces the default ("js") implementation, which unmounts its children
 * rather than hiding them with CSS, and keeps MUI v5+ breakpoint semantics:
 * `${bp}Down` hides strictly below `bp`, `${bp}Up` hides at `bp` and above.
 */

const keys: Breakpoint[] = ['xs', 'sm', 'md', 'lg', 'xl'];

type BreakpointProps = Partial<Record<`${Breakpoint}Up` | `${Breakpoint}Down`, boolean>>;

interface Props extends BreakpointProps {
  children?: React.ReactNode;
  only?: Breakpoint | Breakpoint[];
}

const useWidth = (): Breakpoint => {
  const theme = useTheme();
  // fixed number of hooks, one per breakpoint; the widest match wins
  const matches = [
    useMediaQuery(theme.breakpoints.up('xs')),
    useMediaQuery(theme.breakpoints.up('sm')),
    useMediaQuery(theme.breakpoints.up('md')),
    useMediaQuery(theme.breakpoints.up('lg')),
    useMediaQuery(theme.breakpoints.up('xl'))
  ];

  for (let i = matches.length - 1; i >= 0; i--) {
    if (matches[i]) return keys[i];
  }
  return 'xs';
};

const Hidden = ({ children, only, ...breakpoints }: Props) => {
  const width = useWidth();
  const current = keys.indexOf(width);

  let visible = true;

  if (only) {
    visible = Array.isArray(only) ? !only.includes(width) : only !== width;
  }

  if (visible) {
    for (let i = 0; i < keys.length; i++) {
      const key = keys[i];
      // `${bp}Up` is inclusive of `bp`, `${bp}Down` is not — as in MUI v5+
      if ((breakpoints[`${key}Up`] && i <= current) || (breakpoints[`${key}Down`] && current < i)) {
        visible = false;
        break;
      }
    }
  }

  return visible ? <>{children}</> : null;
};

export default Hidden;
