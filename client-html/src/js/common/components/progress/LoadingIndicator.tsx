/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import CircularProgress from "@mui/material/CircularProgress";
import clsx from "clsx";
import { withStyles } from "@mui/styles";
import { connect } from "react-redux";
import { State } from "../../../reducers/state";

const styles: any = theme => ({
  transparentBackdrop: {
    "&$backdrop": {
      background: "unset"
    }
  },
  backdrop: {
    opacity: 0,
    width: "100%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: -1
  },
  active: {
    willChange: "opacity",
    opacity: 1,
    zIndex: theme.zIndex.drawer + 2,
    transition: `opacity ${theme.transitions.duration.enteringScreen}ms ${theme.transitions.easing.easeInOut}`
  },
  withOffset: {
    height: "calc(100vh - 64px)",
    bottom: "0"
  },
  noOffset: {
    height: "100%"
  },
  interactive: {
    pointerEvents: "none"
  }
});

// approximate time in ms after which loading indicator is shown if request is still pending
const PENDING_TIME = 100;

class LoadingIndicator extends React.PureComponent<any, any> {
  private timeout;

  private _isMounted: boolean;

  constructor(props) {
    super(props);

    this.state = {
      showLoading: false
    };
  }

  componentDidMount() {
    this._isMounted = true;
    this.checkLoading();
  }

  componentDidUpdate() {
    this.checkLoading();
  }

  componentWillUnmount() {
    this._isMounted = false;
    clearTimeout(this.timeout);
  }

  checkLoading() {
    const loading = this.props.fetch.pending;
    loading ? this.checkLoadingTime() : this.hideLoading();
  }

  hideLoading() {
    if (this._isMounted) {
      this.setState({
        showLoading: false
      });
    }
    clearTimeout(this.timeout);
  }

  checkLoadingTime() {
    const {hideIndicator, pending} = this.props.fetch;

    this.timeout = setTimeout(() => {
      if (this._isMounted) {
        this.setState({
          showLoading: hideIndicator ? false : pending
        });
      }
    }, PENDING_TIME);
  }

  render() {
    const {
      classes, appBarOffset, transparentBackdrop, position = "absolute", allowInteractions, customLoading
    } = this.props;

    const {showLoading} = this.state;

    return (
      <div
        className={clsx(classes.backdrop, {
          [classes.transparentBackdrop]: Boolean(transparentBackdrop),
          [classes.active]: customLoading || showLoading,
          [classes.withOffset]: Boolean(appBarOffset),
          [classes.noOffset]: !appBarOffset,
          [classes.interactive]: allowInteractions,
          "absolute": position === "absolute",
          "fixed": position === "fixed"
        })}
      >
        <CircularProgress classes={{root: customLoading || showLoading ? undefined : "d-none"}} size={40}
                          thickness={5}/>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  fetch: state.fetch
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(withStyles(styles)(LoadingIndicator));
