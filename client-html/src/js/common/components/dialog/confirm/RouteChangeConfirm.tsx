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
import Button from "@mui/material/Button";
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import { closeConfirm, showConfirm, setNextLocation } from "../../../actions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { State } from "../../../../reducers/state";

interface Props {
  when: boolean;
  form: string;
  submitForm?: (form: string) => void;
  message?: string;
  showConfirm?: ShowConfirmCaller;
  closeConfirm?: () => void;
  setNextLocation?: (nextLocation: string) => void;
  isInvalid?: boolean;
  nextLocation?: string;
}

class RouteChangeConfirm extends React.Component<Props & RouteComponentProps> {
  private unblock: () => void;

  constructor(props) {
    super(props);
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
      when
    } = this.props;

    this.unblock = history.block(nextLocation => {
      const isCurrent = nextLocation.pathname === location.pathname;

      if (when && !isCurrent) {
        nextLocation.pathname && setNextLocation(nextLocation.pathname);
        
        const confirmButton = (
          <Button
            classes={{
              disabled: "saveButtonEditViewDisabled"
            }}
            startIcon={isInvalid && <ErrorOutline color="error" />}
            variant="contained"
            color="primary"
            disabled={isInvalid}
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
            onCancelCustom: this.onCancelCustom,
            confirmMessage: message,
            cancelButtonText: "DISCARD CHANGES",
            confirmCustomComponent: confirmButton
          }
        );

        setNextLocation(nextLocation.pathname);
      }
      return !when as any;
    });
  }

  componentWillUnmount() {
    this.unblock();
  }

  onCancelCustom = () => {
    this.navigateToNextLocation();
  }

  navigateToNextLocation() {
    const { nextLocation } = this.props;
    this.unblock();
    this.props.history.push(nextLocation);
  }

  render() {
    return null;
  }
}

const mapStateToProps = (state: State, ownProps) => ({
  isInvalid: isInvalid(ownProps.form)(state),
  nextLocation: state.nextLocation
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
