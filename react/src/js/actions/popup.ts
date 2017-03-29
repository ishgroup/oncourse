import {IshActions} from "../constants/IshActions";

/**
 * Update popup by passed content
 * @param {VirtualDOM} Content content which you want to display in popup
 * @return {Object} Action UPDATE_POPUP
 */
export function updatePopup(content) {
    return {
        type: IshActions.UPDATE_POPUP,
        content
    }
}

/**
 * Hide popup
 * @return {Object} Action HIDE_POPUP
 */
export function hidePopup() {
    return {
        type: IshActions.HIDE_POPUP
    };
}
