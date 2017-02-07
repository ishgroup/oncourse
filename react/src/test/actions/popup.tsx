import * as React from 'react';
import ACTIONS from '../../js/constants';
import { hidePopup, updatePopup } from '../../js/actions/popup';

describe('popup actions', () => {
    it('should create HIDE_POPUP actions', () => {
        expect(hidePopup()).toEqual({
            type: ACTIONS.HIDE_POPUP
        });
    });

    it('should create UPDATE_POPUP actions', () => {
        const content = <div>Hello, world!</div>;

        expect(updatePopup(content)).toEqual({
            type: ACTIONS.UPDATE_POPUP,
            content
        });
    });
});
