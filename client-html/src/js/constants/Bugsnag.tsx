/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Bugsnag from "@bugsnag/browser";
import { Client } from "@bugsnag/core";
import BugsnagPluginReact from "@bugsnag/plugin-react";
import { stubFunction } from "ish-ui";
import React from "react";
import { EnvironmentConstants, IS_JEST } from "./EnvironmentConstants";

export const bugsnagClient = IS_JEST
  ? {
    setUser: stubFunction,
  } as Client
  : Bugsnag.start( {
    apiKey: "8fc0c45fd7cbb17b6e8d6cad20738799",
    appVersion: String(process.env.RELEASE_VERSION),
    autoTrackSessions: false,
    releaseStage: process.env.NODE_ENV === EnvironmentConstants.production
    && process.env.RELEASE_VERSION !== "99-SNAPSHOT" ? "production" : "development",
    plugins: [new BugsnagPluginReact(React)]
  });

export const ErrorBoundary = IS_JEST
  ? (props: { children?: React.ReactNode }) => <>{props.children}</>
  : bugsnagClient.getPlugin("react").createErrorBoundary(React);
