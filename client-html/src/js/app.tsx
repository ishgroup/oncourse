/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "react-redux";
import AppEntry from "./AppEntry";
import { ErrorBoundary } from "./constants/Bugsnag";
import { DEFAULT_CONFIG } from "./constants/Config";
import store from "./constants/Store";
import { EnvironmentConstants } from "./constants/EnvironmentConstants";

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

const start = async () => {
  if ((window as any)?.location.pathname === "/provisioning") {
    (window as any)?.setBaseURL("https://provisioning.ish.com.au/b/v1/");

    let index = await fetch("https://provisioning.ish.com.au/index.html").then(r => r.text());
    index = index.replace("/billing.js", "https://provisioning.ish.com.au/billing.js");
    index = index.replace("/billing.css", "https://provisioning.ish.com.au/billing.css");

    const newHTML = document.open("text/html", "replace");
    newHTML.write(index);
    newHTML.close();

    return;
  }

  ReactDOM.render(
    <ErrorBoundary>
      <Provider store={store as any}>
        <AppEntry />
      </Provider>
    </ErrorBoundary>,
    document.getElementById(DEFAULT_CONFIG.CONTAINER_ID)
  );
};

start();
