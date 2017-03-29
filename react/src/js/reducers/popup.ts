import {IshActions} from "../constants/IshActions";

const initialState = {
    content: null
};

const handleActions = {
    [IshActions.UPDATE_POPUP](state, action) {
        return {
            ...state,
            content: action.content
        };
    },

    [IshActions.HIDE_POPUP](state) {
        return {
            ...state,
            content: null
        };
    }
};

export default function(state = initialState, action) {
    return handleActions[action.type] ? handleActions[action.type](state, action) : state;
}
