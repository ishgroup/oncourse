import { TimetableState } from "../../../model/timetable";
import { IAction } from "../../../common/actions/IshAction";
import {
  CLEAR_TIMETABLE_MONTHS,
  FIND_TIMETABLE_SESSIONS,
  FIND_TIMETABLE_SESSIONS_FULFILLED,
  GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED,
  GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
  SET_TIMETABLE_SEARCH,
  SET_TIMETABLE_USERS_SEARCH,
  SET_TIMETABLE_SAVING_FILTER,
  SET_TIMETABLE_FILTERS,
  GET_TIMETABLE_SESSIONS_TAGS_FULFILLED,
  SET_TIMETABLE_SEARCH_ERROR
} from "../actions";

const TimetableInitialState: TimetableState = {
  months: [],
  filters: [],
  sessionsLoading: false,
  search: "",
  usersSearch: "",
  searchError: false,
  savingFilter: null
};

export const timetableReducer = (
  state: TimetableState = TimetableInitialState,
  action: IAction<any>
): TimetableState => {
  switch (action.type) {
    case FIND_TIMETABLE_SESSIONS: {
      return {
        ...state,
        sessionsLoading: true
      };
    }

    case FIND_TIMETABLE_SESSIONS_FULFILLED: {
      const months = state.months.concat(action.payload.months);

      months.sort((a, b) => (a.month > b.month ? 1 : -1));

      return {
        ...state,
        months,
        sessionsLoading: false
      };
    }

    case GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED: {
      const { sessions, monthIndex, dayIndex } = action.payload;

      return {
        ...state,
        months: state.months.map((m, i) => {
          if (i === monthIndex) {
            return {
              ...m,
              days: m.days.map((d, dIn) => {
                if (dIn === dayIndex) {
                  return {
                    ...d,
                    updated: true,
                    sessions
                  };
                }
                return d;
              })
            };
          }
          return m;
        })
      };
    }

    case GET_TIMETABLE_SESSIONS_TAGS_FULFILLED: {
      const { tags, monthIndex, dayIndex } = action.payload;

      return {
        ...state,
        months: state.months.map((m, i) => {
          if (i === monthIndex) {
            return {
              ...m,
              days: m.days.map((d, dIn) => {
                if (dIn === dayIndex) {
                  return {
                    ...d,
                    tagsUpdated: true,
                    sessions: d.sessions.map((s, sIn) => ({
                      ...s,
                      tags: tags[sIn]
                    }))
                  };
                }
                return d;
              })
            };
          }
          return m;
        })
      };
    }

    case SET_TIMETABLE_SEARCH:
    case SET_TIMETABLE_SEARCH_ERROR:
    case SET_TIMETABLE_USERS_SEARCH:
    case SET_TIMETABLE_SAVING_FILTER:
    case GET_TIMETABLE_SESSIONS_DAYS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case SET_TIMETABLE_FILTERS: {
      return {
        ...state,
        filters: action.payload.filters
      };
    }

    case CLEAR_TIMETABLE_MONTHS: {
      return {
        ...state,
        months: []
      };
    }

    default:
      return state;
  }
};
