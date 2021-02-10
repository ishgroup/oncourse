/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import Message from "./Message";
import { clearMessage } from "../../../redux/actions";
// import { clearFetch, clearMessage } from "../../../redux/actions";

const MessageProvider = props => {
  const {
    message, clearMessage
  } = props;

  return (
    <Message
      opened={Boolean(message.message)}
      text={message.message}
      isSuccess={message.success}
      persist={message.persist}
      clearMessage={clearMessage}
    />
  );
};

const mapStateToProps = (state: any) => ({
  message: state.creatingCollege.message,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  clearMessage: () => dispatch(clearMessage())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MessageProvider);
