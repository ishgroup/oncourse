import bugsnagReact from "@bugsnag/plugin-react";
import Bugsnag from '@bugsnag/js';
import React from "react";

// workaround for jest failing tests
let bugsnagClientBase;
if (typeof jest === 'undefined') {
  bugsnagClientBase = Bugsnag( {
    apiKey: "08b42469660b2c6a50e998866c7a2bee",
    appVersion:  String(process.env.BUILD_NUMBER),
    releaseStage: process.env.NODE_ENV,
  });
  bugsnagClientBase.use(bugsnagReact, React);
} else {
  bugsnagClientBase = {
    getPlugin() {
      return () => {};
    },
    notify() {},
  };
}

export const bugsnagClient = bugsnagClientBase;
export const ErrorBoundary = bugsnagClientBase.getPlugin("react");
