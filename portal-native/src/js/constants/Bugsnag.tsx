/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import Bugsnag from '@bugsnag/expo';
import { IS_JEST } from './Environment';

export const bugsnagClient = IS_JEST
  ? {
    setUser: () => null,
  }
  : Bugsnag.start();

export const ErrorBoundary = IS_JEST
  ? (props) => <>{props.children}</>
  : Bugsnag.getPlugin('react').createErrorBoundary(React);
