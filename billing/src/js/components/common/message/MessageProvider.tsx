/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import Message from './Message';
import { clearMessage } from '../../../redux/actions';
import { State } from '../../../models/State';

const MessageProvider = (props) => {
  const {
    message, clear
  } = props;

  return (
    <Message
      opened={Boolean(message.message)}
      text={message.message}
      isSuccess={message.success}
      persist={message.persist}
      clearMessage={clear}
    />
  );
};

const mapStateToProps = (state: State) => ({
  message: state.message,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  clear: () => dispatch(clearMessage())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MessageProvider);
