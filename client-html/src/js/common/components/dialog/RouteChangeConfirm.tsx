/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import ErrorOutline from '@mui/icons-material/ErrorOutline';
import Button from '@mui/material/Button';
import $t from '@t';
import { ShowConfirmCaller } from 'ish-ui';
import * as React from 'react';
import { useEffect, useRef } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router-dom';
import { Dispatch } from 'redux';
import { isInvalid, submit } from 'redux-form';
import { State } from '../../../reducers/state';
import { closeConfirm, setNextLocation, showConfirm } from '../../actions';

interface Props {
  when: boolean;
  form: string;
  submitForm?: (form: string) => void;
  message?: string;
  showConfirm?: ShowConfirmCaller;
  closeConfirm?: () => void;
  setNextLocation?: (nextLocation: string) => void;
  isInvalid?: boolean;
  isOpened?: boolean;
  nextLocation?: string;
}

const RouteChangeConfirm = (
  {
    nextLocation,
    when,
    isOpened,
    closeConfirm,
    location,
    history,
    submitForm,
    form,
    isInvalid,
    showConfirm,
    setNextLocation,
    message = "You have unsaved changes. Do you want to leave this page and discard them?"
  }: Props & RouteComponentProps
) => {

  const unblock = useRef<any>(undefined);

  function onCancel() {
    setNextLocation("");
  }

  const blockCallback = nextLocationCurrent => {
    const isCurrent = nextLocationCurrent.pathname === location.pathname;

    if (when && !isCurrent) {
      const confirmButton = (
        <Button
          classes={{
            disabled: "saveButtonEditViewDisabled"
          }}
          startIcon={isInvalid && <ErrorOutline color="error"/>}
          variant="contained"
          color="primary"
          disabled={isInvalid}
          onClick={() => {
            submitForm(form);
            closeConfirm();
          }}
        >
          {$t('save')}
        </Button>
      );

      setNextLocation(nextLocationCurrent.pathname);

      showConfirm(
        {
          onCancel,
          onCancelCustom: () => navigateToNextLocation(nextLocationCurrent.pathname),
          confirmMessage: message,
          cancelButtonText: "DISCARD CHANGES",
          confirmCustomComponent: confirmButton
        }
      );
    }

    return !when as any;
  };

  unblock.current = history.block(blockCallback);

  function navigateToNextLocation(navigateTo) {
    unblock.current();
    history.push(navigateTo);
    if (isOpened) {
      closeConfirm();
    }
    onCancel();
  }

  useEffect(() => {
    if (nextLocation && !when) {
      navigateToNextLocation(nextLocation);
    }
    return unblock.current;
  }, [
    when,
    isInvalid,
    submitForm,
    form,
    closeConfirm,
    nextLocation,
    setNextLocation
  ]);

  return null;
};

const mapStateToProps = (state: State, ownProps) => ({
  isInvalid: isInvalid(ownProps.form)(state),
  isOpened: state.confirm.open,
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
