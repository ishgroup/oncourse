import * as React from 'react';
import {Browser} from "../../utils";

// tslint:disable-next-line:max-line-length
const BROWSER_WARNING_TEXT = "You are using an unsupported browser. Please download and use Firefox, Chrome or Edge to continue using the onCourse editor.";

export const BrowserWarning = () => {
  return (
    Browser.unsupported() && <div className="browser-warning">
      {BROWSER_WARNING_TEXT}
    </div>
  );
};
