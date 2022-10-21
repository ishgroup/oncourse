/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { useEffect, useState } from "react";
import { makeStyles } from '@mui/styles';
import * as Colors from "@mui/material/colors";

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

export const getColor = (i, code) => {
  let colorName = "";
  switch (i) {
    case 1:
      colorName = "pink";
      break;
    case 2:
      colorName = "purple";
      break;
    case 3:
      colorName = "deepPurple";
      break;
    case 4:
      colorName = "indigo";
      break;
    case 5:
      colorName = "blue";
      break;
    case 6:
      colorName = "lightBlue";
      break;
    case 7:
      colorName = "cyan";
      break;
    case 8:
      colorName = "teal";
      break;
    case 9:
      colorName = "green";
      break;
    case 10:
      colorName = "lightGreen";
      break;
    case 11:
      colorName = "lime";
      break;
    case 12:
      colorName = "amber";
      break;
    case 13:
      colorName = "orange";
      break;
    case 14:
      colorName = "deepOrange";
      break;
    case 15:
      colorName = "brown";
      break;
    case 0:
    default:
      colorName = "red";
      break;
  }
  return Colors[colorName][code];
};
