import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {ResultComp} from "./components/ResultComp";

const PropsBy = (state: IshState): any => {
  return {}
};

export const ActionsBy = (dispatch: Dispatch<any>): any => {
  return {};
};

export const Result = connect(PropsBy, ActionsBy)(ResultComp);

