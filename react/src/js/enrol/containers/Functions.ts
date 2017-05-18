import {connect} from "react-redux";
import Messages from "../components/Messages";
import {Phase} from "../reducers/State";
import {Model, Progress, Tab} from "../components/Progress";

export const MessagesRedux = connect((state) => {
  return {error: state.checkout.error}
})(Messages);




export const ProgressRedux = connect((state) => {
  return {model: progressModelBy(state.checkout.phase)}
}, (dispatch) => {
  return {onChange: (tab:Tab) => console.log(tab) }
})(Progress);


export const progressModelBy = (phase: Phase): Model => {
  const result: Model = {
    active: null,
    disabled: [Tab.Details, Tab.Summary, Tab.Payment]
  };

  switch (phase) {
    case Phase.Init:
      result.active = null;
      result.disabled = [Tab.Details, Tab.Summary, Tab.Payment];
      break;
    case Phase.AddContact:
      result.active = Tab.Details;
      result.disabled = [Tab.Summary, Tab.Payment];
      break;
    case Phase.EditContactDetails:
      result.active = Tab.Details;
      result.disabled = [Tab.Summary, Tab.Payment];
      break;
    case Phase.Summary:
      result.active = Tab.Summary;
      result.disabled = [Tab.Details, Tab.Payment];
      break;
  }
  return result;
};