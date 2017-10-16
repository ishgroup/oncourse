import {IAction} from "../../../../../actions/IshAction";
import {SkillsOnCourseState} from "./State";
import {
  GET_SKILLS_SETTINGS_FULFILLED,
} from "../actions";

export const skillsOnCourseReducer = (state: SkillsOnCourseState = new SkillsOnCourseState(), action: IAction<any>): SkillsOnCourseState => {
  switch (action.type) {

    case GET_SKILLS_SETTINGS_FULFILLED:

      return {
        ...state,
        ...action.payload,
      };

    default:
      return state;
  }
};
