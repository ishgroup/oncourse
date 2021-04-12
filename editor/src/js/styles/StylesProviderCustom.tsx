import * as React from "react";
import { StylesProvider, createGenerateClassName } from "@material-ui/styles";

const generateClassName = createGenerateClassName({
  disableGlobal: true
});

export default props => <StylesProvider generateClassName={generateClassName}>{props.children}</StylesProvider>;
