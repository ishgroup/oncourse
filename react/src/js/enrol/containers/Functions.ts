import {connect} from "react-redux";
import {Messages as MessagesComp} from "../components/Messages";
import {Phase} from "../reducers/State";
import {Model, Progress as ProgressComp, Tab} from "../components/Progress";
import {changePhase, changePhaseRequest} from "../actions/Actions";

export const Messages = connect(state => (
  {error: state.checkout.error}
))(MessagesComp);


export const Progress = connect(
  state => ({model: progressModelBy(state.checkout.phase)}),
  dispatch => ({onChange: (tab: Tab): void => {
    dispatch(changeTab(tab));
  }}),
)(ProgressComp);


const changeTab = (tab: Tab): { type: string, payload: Phase } => {
  switch (tab) {
    case Tab.Payment:
      return changePhaseRequest(Phase.Payment);
    case Tab.Summary:
      return changePhase(Phase.Summary);
    case Tab.Details:
      return changePhaseRequest(Phase.Payment);
    default:
      throw new Error();
  }
};

export const progressModelBy = (phase: Phase): Model => {
  const result: Model = {
    active: null,
    disabled: [Tab.Details, Tab.Summary, Tab.Payment],
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
    case Phase.EditContact:
      result.active = Tab.Details;
      result.disabled = [Tab.Summary, Tab.Payment];
      break;
    case Phase.Summary:
      result.active = Tab.Summary;
      result.disabled = [Tab.Details, Tab.Payment];
      break;
    case Phase.AddAdditionalContact:
      result.active = Tab.Summary;
      result.disabled = [Tab.Details, Tab.Payment];
      break;
    case Phase.Payment:
      result.active = Tab.Payment;
      result.disabled = [Tab.Details];
      break;
  }
  return result;
};
