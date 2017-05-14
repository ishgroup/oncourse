import {connect} from "react-redux";
import Messages from "../../components/Messages";
import {Phase} from "../../reducers/State";
import {Model, Progress, Tab} from "../../components/Progress";

export const MessagesRedux = connect((state) => {
  return {error: state.enrol.error}
})(Messages);




export const ProgressRedux = connect((state) => {
  return {model: progressModelBy(state.enrol.phase)}
}, (dispatch) => {
  return {onChange: (tab:Tab) => console.log(tab) }
})(Progress);


export const progressModelBy = (phase: Phase): Model => {
  const result: Model = {
    active: null,
    disabled: [Tab.details, Tab.summary, Tab.payment]
  };

  switch (phase) {
    case Phase.Init:
      result.active = null;
      result.disabled = [Tab.details, Tab.summary, Tab.payment];
      break;
    case Phase.AddContact:
      result.active = Tab.details;
      result.disabled = [Tab.summary, Tab.payment];
      break;
    case Phase.EditContactDetails:
      result.active = Tab.details;
      result.disabled = [Tab.summary, Tab.payment];
      break;
    case Phase.Summary:
      result.active = Tab.details;
      result.disabled = [Tab.summary, Tab.payment];
      break;
  }
  return result;
};