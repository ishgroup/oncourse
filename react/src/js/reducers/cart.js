import { combineReducers } from 'redux';
import ACTIONS from '../constants';

function addItemReducer(params) {
    const handleActions = {
        [params.action](state, action) {
            let item = state.find((item) => item[params.fieldId] === action.data[params.fieldId]);

            if(item) {
                return state;
            }

            return [...state, action.data];
        }
    };

    return function(state = [], action) {
        return handleActions[action.type] ? handleActions[action.type](state, action) : state;
    }
}

function removeItemReducer(params) {
    const handleActions = {

        [params.action](state, action) {
            let item = state.find((item) => {
                return item[params.fieldId] === action[params.fieldId];
            });

            if(!item) {
                return state;
            }

            let index = state.indexOf(item);

            return [
                ...state.slice(0, index),
                ...state.slice(index + 1)
            ];
        }
    };

    return function(state = [], action) {
        return handleActions[action.type] ? handleActions[action.type](state, action) : state;
    }
}

function concatReducers(...reducers) {
    return function (state, action) {
        return reducers.reduce((state, reducer) => {
            return reducer(state, action);
        }, state);
    };
}

export default combineReducers({
    courses: concatReducers(
        addItemReducer({
            fieldId: 'id',
            fieldItem: 'data',
            action: ACTIONS.ADD_CLASS_TO_CART_SUCCESS
        }),
        removeItemReducer({
            action: ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS,
            fieldId: 'id'
        })
    ),
    products: concatReducers(
        addItemReducer({
            fieldId: 'id',
            fieldItem: 'data',
            action: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS
        }),
        removeItemReducer({
            action: ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS,
            fieldId: 'id'
        })
    )
});