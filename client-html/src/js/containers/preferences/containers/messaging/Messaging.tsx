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
          form={<MessagingForm />}
          emailQueued={this.props.emailQueued.bind(this)}
          smsQueued={this.props.smsQueued.bind(this)}
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
