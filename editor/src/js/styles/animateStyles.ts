/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { transform } from "./mixins/common";
import { keyframes } from "./mixins/keyframes";
import { prefixer } from "./mixins/prefixer";

// const duration = "AnimationDuration";

export const animateStyles = {
  ".animated": {
    ...prefixer("animationDuration", "1s", ["webkit", "spec"]),
    ...prefixer("animationFillMode", "both", ["webkit", "spec"])
  },
  ...keyframes("bounceInRight", {
    "from, 60%, 75%, 90%, to": {
      ...prefixer("animationTimingFunction", "cubic-bezier(0.215, 0.61, 0.355, 1)", ["webkit", "spec"])
    },
    from: {
      opacity: 0,
      ...transform("translate3d(3000px, 0, 0)")
    },
    "60%": {
      opacity: 1,
      ...transform("translate3d(-25px, 0, 0)")
    },
    "75%": {
      ...transform("translate3d(10px, 0, 0)")
    },
    "90%": {
      ...transform("translate3d(-5px, 0, 0)")
    },
    to: {
      ...transform("translate3d(0, 0, 0)")
    }
  }),
  ...keyframes("bounceOutLeft", {
    "20%": {
      opacity: 1,
      ...transform("translate3d(20px, 0, 0)")
    },
    to: {
      opacity: 0,
      ...transform("translate3d(-2000px, 0, 0)")
    }
  }),
  ...keyframes("bounceOutLeftCustom", {
    from: {
      ...transform("translate3d(-80px, 0, 0)"),
      ...prefixer("animationDuration", "0.5s", ["webkit", "spec"])
    },
    to: {
      ...transform("translate3d(0px, 0, 0)"),
      ...prefixer("animationDuration", "0s", ["webkit", "spec"])
    }
  }),
  ...keyframes("bounceOutRightCustom", {
    from: {
      ...transform("translate3d(80px, 0, 0)"),
      ...prefixer("animationDuration", "0.5s", ["webkit", "spec"])
    },
    to: {
      ...transform("translate3d(0px, 0, 0)"),
      ...prefixer("animationDuration", "0s", ["webkit", "spec"])
    }
  }),
  ...keyframes("rotateOutCustom", {
    from: {
      ...prefixer("transform-origin", "center", ["webkit", "spec"])
    },
    to: {
      ...transform("rotate3d(0, 0, 1, 720deg)"),
      ...prefixer("transform-origin", "center", ["webkit", "spec"])
    }
  }, {
    ...prefixer("animationDuration", "2.5s", ["webkit", "spec"])
  }),
  ...keyframes("shake", {
    "10%, 90%": {
      ...transform("translate3d(-2px, 0, 0)")
    },
    "20%, 80%": {
      ...transform("translate3d(4px, 0, 0)")
    },
    "30%, 50%, 70%": {
      ...transform("translate3d(-6px, 0, 0)")
    },
    "40%, 60%": {
      ...transform("translate3d(6px, 0, 0)")
    },
  })
};
