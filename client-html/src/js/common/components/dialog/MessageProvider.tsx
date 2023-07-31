/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../reducers/state";
import { Message } from "ish-ui";
import { clearFetch, clearMessage } from "../../actions";

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
