import * as React from 'react';
import { Snackbar } from 'react-native-paper';
import '@expo/match-media';
import { useMediaQuery } from 'react-responsive';
import { useAppDispatch, useAppSelector } from '../../hooks/redux';
import { setMessage } from '../../actions/MessageActions';

const Message = () => {
  const message = useAppSelector((state) => state.message.message);
  const isBigScreen = useMediaQuery({ query: '(min-device-width: 420px)' });
  const dispatch = useAppDispatch();

  const onDismissSnackBar = () => {
    dispatch(setMessage(null));
  };

  return (
    <Snackbar
      style={{ width: isBigScreen ? 340 : null }}
      visible={Boolean(message)}
      onDismiss={onDismissSnackBar}
      action={{
        label: 'Close'
      }}
    >
      {message}
    </Snackbar>
  );
};

export default Message;
