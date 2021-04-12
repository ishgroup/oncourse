import * as React from 'react';
import {Browser} from "../../utils";
import {withStyles} from "@material-ui/core/styles";

// tslint:disable-next-line:max-line-length
const BROWSER_WARNING_TEXT = "Your current browser is not supported. Please ensure you are using an up to date version of Chrome, Firefox or Edge.";

const styles: any = theme => ({
  browserWarning: {
    padding: "10px",
    background: "$danger-hover",
    color: "white",
    lineHeight: "17px",
    fontWeight: "300",
    textAlign: "center",
  }
})

const BrowserWarning = (props: any) => {
  return (
    Browser.unsupported() && <div className={props.classes.browserWarning}>
      {BROWSER_WARNING_TEXT}
    </div>
  );
};

export default (withStyles(styles)(BrowserWarning));