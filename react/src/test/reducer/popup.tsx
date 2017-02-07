import * as React from 'react';
import ACTIONS from '../../js/constants';
import popup from '../../js/reducers/popup';

describe('popup reducer', () => {
    it('should return initial state', () => {
        expect(popup(undefined, {})).toEqual({
            content: null
        });
    });

    it('should handle HIDE_POPUP', () => {
        const state = {
            content: <div>Hello, world!</div>
        };

        expect(popup(state, {
            type: ACTIONS.HIDE_POPUP
        })).toEqual({
            content: null
        });
    });

    it('should handle UPDATE_POPUP', () => {
        const content = <div>Hello, world!</div>;

        expect(popup(undefined, {
            type: ACTIONS.UPDATE_POPUP,
            content
        }))
    });
});
