import React from "react";
import BugsnagPluginReact from "@bugsnag/plugin-react";
import Bugsnag from '@bugsnag/js';

// workaround for jest failing tests
let bugsnagClientBase;
if (typeof jest === 'undefined' && window.location.protocol !== "file:" && process.env.NODE_ENV === "production") {
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
      );
    },
  });
} else {
  bugsnagClientBase = {
    getPlugin: () => ({
      createErrorBoundary: () => null,
    }),
    notify:() => null,
  };
}

export const ErrorBoundary = bugsnagClientBase.getPlugin('react').createErrorBoundary(React);
