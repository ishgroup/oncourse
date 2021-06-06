/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { Dispatch } from "redux";
import { isInvalid, submit } from "redux-form";
import { connect } from "react-redux";
import Button from "@material-ui/core/Button";
import ErrorOutline from "@material-ui/icons/ErrorOutline";
import { closeConfirm, showConfirm, setNextLocation } from "../../../actions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

interface Props {
  when: boolean;
  form: string;
  submitForm?: (form: string) => void;
  message?: string;
  showConfirm?: ShowConfirmCaller;
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
      showConfirm,
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

        showConfirm(
          {
            onCancelCustom: this.onConfirm,
            onCancel: this.onCancel,
            confirmMessage: message,
            cancelButtonText: "DISCARD CHANGES",
            confirmCustomComponent: confirmButton
          }
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
  showConfirm: state => dispatch(showConfirm(state)),
  submitForm: (form: string) => dispatch(submit(form)),
  closeConfirm: () => dispatch(closeConfirm()),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(RouteChangeConfirm)) as React.ComponentType<
  Props
>;
