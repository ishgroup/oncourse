/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import bugsnagReact from "@bugsnag/plugin-react";
import React from "react";
import { EnvironmentConstants } from "./EnvironmentConstants";

const bugsnag = require('@bugsnag/browser');

export const bugsnagClient = bugsnag( {
  apiKey: "8fc0c45fd7cbb17b6e8d6cad20738799",
  appVersion: String(process.env.RELEASE_VERSION),
  releaseStage: process.env.NODE_ENV === EnvironmentConstants.production
  && process.env.RELEASE_VERSION !== "99-SNAPSHOT" ? "production" : "development"
});
bugsnagClient.use(bugsnagReact, React);
export const ErrorBoundary = bugsnagClient.getPlugin("react");
