import ACTIONS from '../constants';

const initialState = {
    content: null
};

const handleActions = {
    [ACTIONS.UPDATE_POPUP](state, action) {
        return {
            ...state,
            content: action.content
        };
    },

    [ACTIONS.HIDE_POPUP](state) {
        return {
            ...state,
            content: null
        };
    }
};

export default function(state = initialState, action) {
    return handleActions[action.type] ? handleActions[action.type](...arguments) : state;
}