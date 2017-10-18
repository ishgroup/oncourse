import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_SKILLS_SETTINGS_REQUEST = _toRequestType("settings/get/skillsOnCourse");
export const GET_SKILLS_SETTINGS_FULFILLED = FULFILLED(GET_SKILLS_SETTINGS_REQUEST);

export const SET_SKILLS_SETTINGS_REQUEST = _toRequestType("settings/set/skillsOnCourse");
export const SET_SKILLS_SETTINGS_FULFILLED = FULFILLED(SET_SKILLS_SETTINGS_REQUEST);

export const getSkillsOnCourseSettings = () => ({
  type: GET_SKILLS_SETTINGS_REQUEST,
});

export const setSkillsOnCourseSettings = settings => ({
  type: SET_SKILLS_SETTINGS_REQUEST,
  payload: settings,
});

