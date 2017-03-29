import * as React from 'react';
import { hidePopup, updatePopup } from '../../js/actions/popup';
import {IshActions} from "../../js/constants/IshActions";

describe('popup actions', () => {
    it('should create HIDE_POPUP actions', () => {
        expect(hidePopup()).toEqual({
            type: IshActions.HIDE_POPUP
        });
    });

    it('should create UPDATE_POPUP actions', () => {
        const content = <div>Hello, world!</div>;

        expect(updatePopup(content)).toEqual({
            type: IshActions.UPDATE_POPUP,
            content
        });
    });
});
