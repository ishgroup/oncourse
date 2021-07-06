import React from "react";
import BugsnagPluginReact from "@bugsnag/plugin-react";
import Bugsnag from '@bugsnag/js';
import { IS_JEST } from './Config';

// workaround for jest failing tests
let bugsnagClientBase;
if (!IS_JEST && window.location.protocol !== "file:" && process.env.NODE_ENV === "production") {
  bugsnagClientBase = Bugsnag;

  bugsnagClientBase.start({
    apiKey: "08b42469660b2c6a50e998866c7a2bee",
    plugins: [new BugsnagPluginReact()],
    appVersion:  String(process.env.BUILD_NUMBER),
    releaseStage: process.env.NODE_ENV,
    onError (event) {
      return !(!event.errors.length
        || !event.errors[0].stacktrace.some(s => s.file.includes("dynamic.js"))
        || event.errors[0].stacktrace.some(s => s.file.includes("gtm.js"))
        || event.errors[0].stacktrace.some(s => s.file.includes("all.js"))
      );
    },
  });
} else {
  bugsnagClientBase = {
    getPlugin: () => ({
      createErrorBoundary: () => "div",
    }),
    notify:() => null,
  };
}
export const bugsnagClient  = bugsnagClientBase;

export const ErrorBoundary = IS_JEST
  ? props => <>{props.children}</>
  : bugsnagClientBase.getPlugin('react').createErrorBoundary(React);
