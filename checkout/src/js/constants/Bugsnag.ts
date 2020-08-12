import bugsnagReact from "@bugsnag/plugin-react";
import Bugsnag from '@bugsnag/js';
import React from "react";

export const bugsnagClient = Bugsnag( {
  apiKey: "08b42469660b2c6a50e998866c7a2bee",
  appVersion:  process.env.BUILD_NUMBER,
  releaseStage: process.env.NODE_ENV,
});

bugsnagClient.use(bugsnagReact, React);
export const ErrorBoundary = bugsnagClient.getPlugin("react");
