import ACTIONS from '../constants';

const initialState = {
    courses: []
};

const handleActions = {

    [ACTIONS.ADD_TO_CART](state, action) {
        let course = state.courses.find((course) => {
                return course.id === action.courseId;
            }),
            index;

        if(course) {
            if(course.pending) {
                return state;
            }

            index = state.courses.indexOf(course);
            course = { ...course };
            delete course.error;
        } else {
            course = {};
            index = state.courses.length;
        }

        return {
            ...state,
            courses: [
                ...state.courses.slice(0, index),
                {
                    ...course,
                    id: action.courseId,
                    pending: true
                },
                ...state.courses.slice(index + 1)
            ]
        };
    },

    [ACTIONS.ADD_TO_CART_SUCCESS](state, action) {
        let course = state.courses.find((course) => {
            return course.id === action.course.id;
        });

        if(!course) {
            return state;
        }

        let index = state.courses.indexOf(course);

        return {
            ...state,
            courses: [
                ...state.courses.slice(0, index),
                {
                    ...course,
                    pending: false,
                    data: action.course
                },
                ...state.courses.slice(index + 1)
            ]
        };
    },

    [ACTIONS.ADD_TO_CART_FAILURE](state, action) {
        let course = state.courses.find((course) => {
            return course.id === action.courseId;
        });

        if(!course) {
            return state;
        }

        let index = state.courses.indexOf(course);

        return {
            ...state,
            courses: [
                ...state.courses.slice(0, index),
                {
                    ...course,
                    pending: false,
                    error: action.error
                },
                ...state.courses.slice(index + 1)
            ]
        };
    },

    [ACTIONS.REMOVE_FROM_CART](state, action) {
        let course = state.courses.find((course) => {
                return course.id === action.courseId;
            }),
            index;

        if(course) {
            if(course.pending) {
                return state;
            }

            index = state.courses.indexOf(course);
            course = { ...course };
            delete course.error;
        } else {
            course = {};
            index = state.courses.length;
        }

        return {
            ...state,
            courses: [
                ...state.courses.slice(0, index),
                {
                    ...course,
                    pending: true
                },
                ...state.courses.slice(index + 1)
            ]
        };
    },

    [ACTIONS.REMOVE_FROM_CART_SUCCESS](state, action) {
        let course = state.courses.find((course) => {
            return course.id === action.courseId;
        });

        if(!course) {
            return state;
        }

        let index = state.courses.indexOf(course);

        return {
            ...state,
            courses: [
                ...state.courses.slice(0, index),
                ...state.courses.slice(index + 1)
            ]
        };
    },

    [ACTIONS.REMOVE_FROM_CART_FAILURE](state, action) {
        let course = state.courses.find((course) => {
            return course.id === action.courseId;
        });

        if(!course) {
            return state;
        }

        let index = state.courses.indexOf(course);

        return {
            ...state,
            courses: [
                ...state.courses.slice(0, index),
                {
                    ...course,
                    pending: false,
                    error: action.error
                },
                ...state.courses.slice(index + 1)
            ]
        };
    }
};

export default (state = initialState, action) => {
    return handleActions[action.type] ? handleActions[action.type](state, action) : state;
};