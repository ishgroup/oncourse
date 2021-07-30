/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import Bugsnag from '@bugsnag/expo';
import { IS_JEST } from './Environment';

const isDisabled = IS_JEST || __DEV__;

export const bugsnagClient = isDisabled
  ? {
    setUser: () => null,
  }
  : Bugsnag.start();

export const ErrorBoundary = isDisabled
  ? (props) => <>{props.children}</>
  : Bugsnag.getPlugin('react').createErrorBoundary(React);
