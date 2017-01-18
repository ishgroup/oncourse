import ACTIONS from 'js/constants';
import { hidePopup, updatePopup } from 'js/actions/popup';

describe('cart actions', () => {
    it('should create HIDE_POPUP actions', () => {
        expect(hidePopup()).to.deep.equal({
            type: ACTIONS.HIDE_POPUP
        });
    });

    it('should create UPDATE_POPUP actions', () => {
        const content = <div>Hello, world!</div>;

        expect(updatePopup(content)).to.deep.equal({
            type: ACTIONS.UPDATE_POPUP,
            content
        });
    });
});