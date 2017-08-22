import * as React from "react";
import {Actions, hidePopup, updatePopup} from "../../js/web/actions/Actions";

describe('popup actions', () => {
  it('should create HIDE_POPUP actions', () => {
    expect(hidePopup()).toEqual({
      type: Actions.HIDE_POPUP
    });
  });

  it('should create UPDATE_POPUP actions', () => {
    const content = <div>Hello, world!</div>;

    expect(updatePopup(content)).toEqual({
      type: Actions.UPDATE_POPUP,
      content
    });
  });
});
