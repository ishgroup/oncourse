/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from 'react';
import { closeConfirm } from 'src/js/redux/actions/Confirm';
import ConfirmBase from './Confirm';
import { useAppDispatch, useAppSelector } from '../../../redux/hooks/redux';

const ConfirmProvider = React.memo(() => {
  const {
    onCancel, onConfirm, onCancelCustom, confirmCustomComponent, ...rest
  } = useAppSelector((state) => state.confirm);

  const dispatch = useAppDispatch();

  const onCancelHandler = useCallback(() => {
    if (onCancel) {
      onCancel();
    }

    dispatch(closeConfirm());
  }, [onCancel]);

  const onCancelCustomHandler = useCallback(() => {
    if (onCancelCustom) {
      onCancelCustom();
    }

    dispatch(closeConfirm());
  }, [onCancelCustom]);

  const onConfirmHandler = useCallback(
    typeof onConfirm === 'function'
      ? () => {
        onConfirm();
        dispatch(closeConfirm());
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

export default ConfirmProvider;
