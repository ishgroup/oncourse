/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import AppEntry from './AppEntry';
import { ErrorBoundary } from './constants/Bugsnag';
import { DEFAULT_CONFIG } from './constants/Config';
import { EnvironmentConstants } from './constants/EnvironmentConstants';
import store from './constants/Store';

if (process.env.NODE_ENV === EnvironmentConstants.production) {
  if ("serviceWorker" in navigator) {
    window.addEventListener("load", () => {
      navigator.serviceWorker
        .register("/service-worker.js")
        .then(registration => {
          console.log("SW registered: ", registration);
        })
        .catch(registrationError => {
          console.log("SW registration failed: ", registrationError);
        });
    });
  }
}

const root = createRoot(document.getElementById(DEFAULT_CONFIG.CONTAINER_ID));

root.render(
  <ErrorBoundary>
    <Provider store={store as any}>
      <AppEntry />
    </Provider>
  </ErrorBoundary>,
);