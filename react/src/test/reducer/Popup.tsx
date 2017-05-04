import * as React from "react";
import popup from "../../js/reducers/popup";
import {Actions} from "../../js/web/actions/Actions";

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
            type: Actions.HIDE_POPUP
        })).toEqual({
            content: null
        });
    });

    it('should handle UPDATE_POPUP', () => {
        const content = <div>Hello, world!</div>;

        expect(popup(undefined, {
            type: Actions.UPDATE_POPUP,
            content
        }))
    });
});
