/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Message } from "ish-ui";
import { MessageProps } from "ish-ui/dist/model/Message";
import debounce from "lodash.debounce";
import { useEffect, useState } from "react";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Fetch } from "../../../model/common/Fetch";
import { AppMessage } from "../../../model/common/Message";
import { State } from "../../../reducers/state";
import { clearFetch, clearMessage } from "../../actions";

type MessageProviderState = Omit<MessageProps, "clearMessage" >

const getState = (message: AppMessage, fetch: Fetch): MessageProviderState => ({
  opened: Boolean(fetch.message || message.message),
  text: fetch.message || message.message,
  success: fetch.success || message.success,
  persist: fetch.persist || message.persist
});

const MessageProvider = props => {
  const {
    fetch, message, clearFetch, clearMessage
  } = props;

  const [messageState, setMessageState] = useState<MessageProviderState>(
    getState(fetch, message)
  );

  const updateDebounced = debounce(newState => setMessageState(newState), 500);

  useEffect(() => {
    const updated = getState(fetch, message);
    if (messageState.opened && !updated.opened) {
      setMessageState({ ...messageState, opened: false });
      updateDebounced(updated);
      return;
    }
    setMessageState(updated);
  }, [fetch, message]);

  return (
    <Message
      {...messageState}
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
