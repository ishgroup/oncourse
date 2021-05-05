/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Dispatch } from "redux";
import { isInvalid, submit } from "redux-form";
import { connect } from "react-redux";
import Button from "@material-ui/core/Button";
import ErrorOutline from "@material-ui/icons/ErrorOutline";
import { closeConfirm, showConfirm, setNextLocation } from "../../../actions";

interface Props {
  when: boolean;
  form: string;
  submitForm?: (form: string) => void;
  message?: string;
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string, onCancel?: any, title?: string,
                 cancelButtonText?: string, onCancelCustom?: () => void, confirmCustomComponent?: React.ReactNode) => void;
  closeConfirm?: () => void;
  setNextLocation?: (nextLocation: string) => void;
  isInvalid?: (form: string) => boolean;
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

  shouldComponentUpdate(nextProps: Readonly<Props & RouteComponentProps>): boolean {
    const {
      isInvalid,
      form,
    } = this.props;

    if (nextProps.form !== form || nextProps.isInvalid(nextProps.form) !== isInvalid(form)) {
      return true;
    }

    return false;
  }

  componentDidMount() {
    this.setUnblockFunction();
  }

  componentDidUpdate() {
    this.setUnblockFunction();
  }

  setUnblockFunction() {
    const {
      message = "You have unsaved changes. Do you want to leave this page and discard them?",
      history,
      location,
      openConfirm,
      isInvalid,
      form,
      submitForm,
      closeConfirm,
      setNextLocation,
    } = this.props;

    this.unblock = history.block(nextLocation => {
      const isCurrent = nextLocation.pathname === location.pathname;

      if (this.props.when && !isCurrent) {
        nextLocation.pathname && setNextLocation(nextLocation.pathname);

        const isInvalidForm = isInvalid(form);

        const confirmButton = (
          <Button
            classes={{
              disabled: "saveButtonEditViewDisabled"
            }}
            startIcon={isInvalidForm && <ErrorOutline color="error" />}
            variant="contained"
            color="primary"
            disabled={isInvalidForm}
            onClick={() => {
              submitForm(form);
              closeConfirm();
            }}
          >
            SAVE
          </Button>
        );

        openConfirm(
          undefined,
          message,
          "SAVE",
          this.onCancel,
          undefined,
          "DISCARD CHANGES",
          this.onConfirm,
          confirmButton,
        );

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
    this.props.setNextLocation('');
  };

  onConfirm = () => {
    this.navigateToNextLocation();
    this.props.setNextLocation('');
  };

  render() {
    return null;
  }
}

const mapStateToProps = (state: State) => ({
  isInvalid: (form: string) => isInvalid(form)(state),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string, onCancel?: any, title?: string,
                  cancelButtonText?: string, onCancelCustom?: () => void, confirmCustomComponent?: React.ReactNode) =>
      dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText, onCancel, title, cancelButtonText,
        onCancelCustom, confirmCustomComponent)),
    submitForm: (form: string) => dispatch(submit(form)),
    closeConfirm: () => dispatch(closeConfirm()),
    setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(RouteChangeConfirm)) as React.ComponentType<
  Props
>;
