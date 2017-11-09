import {IAction} from "../../../../../actions/IshAction";
import {SkillsOnCourseState} from "./State";
import {
  GET_SKILLS_SETTINGS_FULFILLED, GET_SKILLS_SETTINGS_REQUEST, SET_SKILLS_SETTINGS_FULFILLED,
  SET_SKILLS_SETTINGS_REQUEST,
} from "../actions";

export const skillsOnCourseReducer = (state: SkillsOnCourseState = new SkillsOnCourseState(), action: IAction<any>): SkillsOnCourseState => {
  switch (action.type) {

    case GET_SKILLS_SETTINGS_REQUEST:
    case SET_SKILLS_SETTINGS_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case GET_SKILLS_SETTINGS_FULFILLED:
    case SET_SKILLS_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload,
        fetching: false,
      };


    default:
      return state;
  }
};
