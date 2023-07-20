/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import ConfirmBase from "../../../../ish-ui/dialog/confirm/ConfirmBase";
import { State } from "../../../reducers/state";
import { closeConfirm } from "../../actions";
import { AnyArgFunction } from "../../../model/common/CommonFunctions";
import { ConfirmState } from "../../../../ish-ui/model/Confirm";

interface Props {
  stateProps: ConfirmState;
  closeConfirm?: AnyArgFunction;
}

const ConfirmProvider = React.memo<Props>(({ stateProps, closeConfirm }) => {
  const {
 onCancel, onConfirm, onCancelCustom, confirmCustomComponent, ...rest
} = stateProps;

  const onCancelHandler = useCallback(() => {
    if (onCancel) {
      onCancel();
    }

    closeConfirm();
  }, [onCancel]);

  const onCancelCustomHandler = useCallback(() => {
    if (onCancelCustom) {
      onCancelCustom();
    }

    closeConfirm();
  }, [onCancelCustom]);

  const onConfirmHandler = useCallback(
    typeof onConfirm === "function"
      ? () => {
          onConfirm();
          closeConfirm();
        }
      : null,
    [onConfirm]
  );

  return (
    <ConfirmBase
      {...rest}
      onCancel={onCancelHandler}
      onConfirm={onConfirmHandler}
      onCancelCustom={onCancelCustom ? onCancelCustomHandler : null}
      confirmCustomComponent={confirmCustomComponent}
    />
);
});

const mapStateToProps = (state: State) => ({
  stateProps: state.confirm
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  closeConfirm: () => dispatch(closeConfirm())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ConfirmProvider);
