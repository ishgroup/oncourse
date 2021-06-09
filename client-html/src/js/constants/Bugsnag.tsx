/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Bugsnag from "@bugsnag/browser";
import { Client } from "@bugsnag/core";
import BugsnagPluginReact from "@bugsnag/plugin-react";
import { EnvironmentConstants } from "./EnvironmentConstants";
import { stubFunction } from "../common/utils/common";

export const bugsnagClient = jest
  ? {
    setUser: stubFunction,
  } as Client
  : Bugsnag.start( {
    apiKey: "8fc0c45fd7cbb17b6e8d6cad20738799",
    appVersion: String(process.env.RELEASE_VERSION),
    releaseStage: process.env.NODE_ENV === EnvironmentConstants.production
    && process.env.RELEASE_VERSION !== "99-SNAPSHOT" ? "production" : "development",
    plugins: [new BugsnagPluginReact(React)]
  });

export const ErrorBoundary = jest
  ? props => <>{props.children}</>
  : bugsnagClient.getPlugin("react").createErrorBoundary(React);
