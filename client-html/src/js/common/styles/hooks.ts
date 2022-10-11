/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { makeStyles } from '@mui/styles';
import { useEffect, useState } from "react";

export const useHoverShowStyles = makeStyles({
  container: {
    "&:hover $target": {
      visibility: "visible"
    }
  },
  target: {
    visibility: "hidden"
  }
});

export const useWindowSize = () => {
  const [windowSize, setWindowSize] = useState({
    width: undefined,
    height: undefined,
  });
  
  useEffect(() => {
    const handleResize = () => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight,
      });
    };
    window.addEventListener("resize", handleResize);
    handleResize();
    return () => window.removeEventListener("resize", handleResize);
  }, []); 
  
  return windowSize;
};