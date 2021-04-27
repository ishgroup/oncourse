/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { withStyles } from "@material-ui/core/styles";
import { LinearProgress } from "@material-ui/core";

const styles: any = () => ({
  root: {
    flexGrow: 1,
    position: "fixed",
    top: 0,
    width: "100%"
  }
});

interface Props {
  classes: any;
  error?: any;
}

class Loading extends React.Component<Props, any> {
  state = {
    completed: 0
  };

  componentDidMount() {
    this.timer = setInterval(this.progress, 300);
  }

  componentDidUpdate(prevProps) {
    if (!prevProps.error && this.props.error) {
      console.error(this.props.error);
    }
  }

  componentWillUnmount() {
    clearInterval(this.timer);
  }

  timer = null;

  progress = () => {
    const { completed } = this.state;
    if (completed === 100) {
      clearInterval(this.timer);
    } else {
      const diff = Math.random() * 10;
      this.setState({ completed: Math.min(completed + diff, 100) });
    }
  };

  render() {
    const { classes } = this.props;
    return (
      <div className={classes.root}>
        <LinearProgress variant="determinate" value={this.state.completed} />
      </div>
    );
  }
}

export default withStyles(styles)(Loading) as any;
