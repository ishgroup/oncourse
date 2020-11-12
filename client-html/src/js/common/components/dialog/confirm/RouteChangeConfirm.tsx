/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Dispatch } from "redux";
import { showConfirm } from "../../../actions";
import { connect } from "react-redux";

interface Props {
  when: boolean;
  message?: string;
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string, onCancel?: any) => void;
}

interface State {
  nextLocation?: any;
}

class RouteChangeConfirm extends React.Component<Props & RouteComponentProps, State> {
  private unblock: () => void;

  constructor(props) {
    super(props);
    this.state = { nextLocation: null };
  }

  componentDidMount() {
    const {
      message = "You have unsaved changes. Do you want to leave this page and discard them?",
      history,
      location,
      openConfirm
    } = this.props;

    this.unblock = history.block(nextLocation => {
      const isCurrent = nextLocation.pathname === location.pathname;

      if (this.props.when && !isCurrent) {
        openConfirm(this.onConfirm, message, "DISCARD CHANGES", this.onCancel);

        this.setState({
          nextLocation
        });
      }
      return !this.props.when as any;
    });
  }

  componentWillUnmount() {
    this.unblock();
  }

  navigateToNextLocation() {
    this.unblock();
    this.props.history.push(this.state.nextLocation.pathname);
  }

  onCancel = () => {
    this.setState({ nextLocation: null });
  };

  onConfirm = () => {
    this.navigateToNextLocation();
  };

  render() {
    return null;
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string, onCancel?: any) =>
      dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText, onCancel))
  };
};

export default connect<any, any, any>(null, mapDispatchToProps)(withRouter(RouteChangeConfirm)) as React.ComponentType<
  Props
>;
