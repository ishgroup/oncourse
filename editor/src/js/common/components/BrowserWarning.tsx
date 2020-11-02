import * as React from 'react';
import {Browser} from "../../utils";

// tslint:disable-next-line:max-line-length
const BROWSER_WARNING_TEXT = "Your current browser is not supported. Please ensure you are using an up to date version of Chrome, Firefox or Edge.";

export const BrowserWarning = () => {
  return (
    Browser.unsupported() && <div className="browser-warning">
      {BROWSER_WARNING_TEXT}
    </div>
  );
};
