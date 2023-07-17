/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import Message from "./Message";
import { clearFetch, clearMessage } from "../../../actions";

const MessageProvider = props => {
  const {
 fetch, message, clearFetch, clearMessage
} = props;
  return (
    <Message
      opened={Boolean(fetch.message || message.message)}
      text={fetch.message || message.message}
      isSuccess={fetch.success || message.success}
      persist={fetch.persist || message.persist}
      clearMessage={fetch.message ? clearFetch : clearMessage}
    />
  );
};

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  message: state.message
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    clearFetch: () => dispatch(clearFetch()),
    clearMessage: () => dispatch(clearMessage())
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MessageProvider);
