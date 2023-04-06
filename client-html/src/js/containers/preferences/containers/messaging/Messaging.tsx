/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Categories } from "../../../../model/preferences";
import { State } from "../../../../reducers/state";
import FormContainer from "../FormContainer";
import MessagingForm from "./components/MessagingForm";
import { getMessageQueued } from "../../../../common/actions";
import { IAction } from "../../../../common/actions/IshAction";

class Messaging extends React.Component<any, any> {
  render() {
    const { messaging } = this.props;
    return (
      <div>
        <FormContainer
          data={messaging}
          category={Categories.messaging}
          form={formRoleName => <MessagingForm formRoleName={formRoleName} />}
          emailQueued={this.props.emailQueued.bind(this)}
          smsQueued={this.props.smsQueued.bind(this)}
          formName="MessagingForm"
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  messaging: state.preferences.messaging
});

const mapDispatchToProps = (dispatch: Dispatch<IAction<any>>) => ({
  smsQueued: () => dispatch(getMessageQueued("sms")),
  emailQueued: () => dispatch(getMessageQueued("email"))
});

export default connect(mapStateToProps, mapDispatchToProps)(Messaging);
