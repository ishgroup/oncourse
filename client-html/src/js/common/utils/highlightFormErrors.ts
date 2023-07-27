/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { getFirstErrorNodePath } from "./validation";

export const animateFormErrors = (target: Element | Document = document) => {
  const errorNodes = target.querySelectorAll(".shakingError");

  errorNodes.forEach(node => {
    node.classList.add("animated", "shake");
  });

  setTimeout(() => {
    errorNodes.forEach(node => {
      node.classList.remove("animated", "shake");
    });
  }, 1000);
};

export const onSubmitFail = (errors, dispatch, submitError, props, options?) => {
  const firstErrorNode = document.getElementById(getFirstErrorNodePath(errors));

  if (firstErrorNode) {
    firstErrorNode.scrollIntoView(options || {behavior: 'smooth', block: 'center'});

    setTimeout(() => {
      animateFormErrors();
    }, 200);
  }
};
