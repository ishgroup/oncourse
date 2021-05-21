import React from "react";
import AppBar from "@material-ui/core/AppBar";
import { withStyles } from "@material-ui/core/styles";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";

const styles: any = theme => ({
  appBar: {
    width: "auto",
    marginLeft: -10,
    marginRight: -10,
    minWidth: "100%",
    left: 0,
    right: 0,
    padding: 0,
  }
});

class Bar extends React.Component<any, any> {
  render() {
    const { classes, title, children } = this.props;

    return (
      <AppBar
        classes={{ root: classes.appBar }}
        position="absolute"
      >
        <Toolbar>
          <Typography className="appHeaderFontSize flex-fill" color="inherit" noWrap>
            {title}
          </Typography>
          {children}
        </Toolbar>
      </AppBar>
    );
  }
}

export default withStyles(styles)(Bar);
