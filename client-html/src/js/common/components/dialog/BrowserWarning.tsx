import { Alert } from "@mui/lab";
import * as React from 'react';

const BROWSER_WARNING_TEXT = "Your current browser is not supported. Please ensure you are using an up to date version of Chrome, Firefox or Edge.";

export const isOldIE = () => {
  const ua = window.navigator.userAgent;
  const msie = ua.indexOf('MSIE ');
  return msie !== -1;
};

export const BrowserWarning = () => isOldIE() && (
  <div style={{
    position: "fixed",
    bottom: "50vh",
    left: "35vw",
    right: "35vw",
    zIndex: 10000
  }}>
    <Alert variant="filled" severity="error">
      {BROWSER_WARNING_TEXT}
    </Alert>
  </div>
);
