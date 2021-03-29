import {connect} from "react-redux";
import {Messages as MessagesComp} from "../components/Messages";
import {Phase} from "../reducers/State";
import {Model, Progress as ProgressComp, Tab} from "../components/Progress";
import {changePhase} from "../actions/Actions";
import {IshState} from "../../services/IshState";

export const Messages = connect<any, any, any, IshState>(state => (
  {error: state.checkout.error}
))(MessagesComp as any);


export const Progress = connect<any, any, any, IshState>(
  state => ({
    model: progressModelBy(state.checkout.phase),
    haveTotalAmount: !!(state.checkout.amount && Number(state.checkout.amount.total)),
  }),
  dispatch => ({
    onChange: (tab: Tab): void => {
      dispatch(changeTab(tab));
    }}),
)(ProgressComp);


const changeTab = (tab: Tab): { type: string, payload: Phase } => {
  switch (tab) {
    case Tab.Payment:
      return changePhase(Phase.Payment);
    case Tab.Summary:
      return changePhase(Phase.Summary);
    case Tab.Details:
      return changePhase(Phase.EditContact);
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
    case Phase.Summary:
      result.active = Tab.Summary;
      result.disabled = [Tab.Details, Tab.Payment];
      break;
    case Phase.AddPayer:
    case Phase.AddContact:
    case Phase.EditContact:
    case Phase.AddContactAsPayer:
    case Phase.AddContactAsCompany:
    case Phase.AddConcession:
      result.active = Tab.Details;
      result.disabled = [Tab.Summary, Tab.Payment];
      break;
    case Phase.Payment:
      result.active = Tab.Payment;
      result.disabled = [Tab.Details];
      break;
    case Phase.Result:
      result.active = Tab.Payment;
      result.disabled = [Tab.Details, Tab.Summary, Tab.Payment];
      break;
  }
  return result;
};
