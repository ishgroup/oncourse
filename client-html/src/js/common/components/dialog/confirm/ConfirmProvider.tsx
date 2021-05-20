/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import ConfirmBase from "./ConfirmBase";
import { State } from "../../../../reducers/state";
import { closeConfirm } from "../../../actions";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { ConfirmState } from "../../../../model/common/Confirm";

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
