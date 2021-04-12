/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AppTheme } from "./themeInterface";
import { prefixer } from "./mixins/prefixer";

export const bootstrap = (theme: AppTheme) => ({
  strong: {
    fontWeight: "bold"
  },
  ".fontWeight-normal": {
    fontWeight: "normal"
  },
  ".fontWeight600": {
    fontWeight: 600
  },
  ".text-bold": {
    fontWeight: theme.typography.fontWeightBold
  },
  ".text-bolder": {
    fontWeight: "bolder"
  },
  ".fs2": {
    fontSize: theme.spacing(2)
  },
  ".fs4": {
    fontSize: theme.spacing(4)
  },
  ".fs8": {
    fontSize: theme.spacing(8)
  },
  ".fs10": {
    fontSize: theme.spacing(10)
  },
  ".fs19": {
    fontSize: theme.spacing(19)
  },
  ".fsInherit": {
    fontSize: "inherit"
  },
  ".relative": {
    position: "relative"
  },
  ".absolute": {
    position: "absolute"
  },
  ".fixed": {
    position: "fixed"
  },
  ".sticky": {
    position: "sticky"
  },
  ".left-0": {
    left: 0
  },
  ".top-0": {
    top: 0
  },
  ".right-0": {
    right: 0
  },
  ".bottom-0": {
    bottom: 0
  },
  ".pr-6": {
    paddingRight: theme.spacing(6)
  },
  ".p-4": {
    padding: theme.spacing(4)
  },
  ".pr-4": {
    paddingRight: theme.spacing(4)
  },
  ".p-3": {
    padding: theme.spacing(3)
  },
  ".pt-3": {
    paddingTop: theme.spacing(3)
  },
  ".pl-3": {
    paddingLeft: theme.spacing(3)
  },
  ".pr-3": {
    paddingRight: theme.spacing(3)
  },
  ".pb-3": {
    paddingBottom: theme.spacing(3)
  },
  ".p-2": {
    padding: theme.spacing(2)
  },
  ".pl-2": {
    paddingLeft: theme.spacing(2)
  },
  ".pr-2": {
    paddingRight: theme.spacing(2)
  },
  ".pb-2": {
    paddingBottom: theme.spacing(2)
  },
  ".pt-2": {
    paddingTop: theme.spacing(2)
  },
  ".p-1-5": {
    padding: theme.spacing(1.5)
  },
  ".pt-1-5": {
    paddingTop: theme.spacing(1.5)
  },
  ".pb-1-5": {
    paddingBottom: theme.spacing(1.5)
  },
  ".pl-1-5": {
    paddingLeft: theme.spacing(1.5)
  },
  ".pr-1-5": {
    paddingRight: theme.spacing(1.5)
  },
  ".p-1": {
    padding: theme.spacing(1)
  },
  ".pt-1": {
    paddingTop: theme.spacing(1)
  },
  ".pb-1": {
    paddingBottom: theme.spacing(1)
  },
  ".pl-1": {
    paddingLeft: theme.spacing(1)
  },
  ".pr-1": {
    paddingRight: theme.spacing(1)
  },
  ".p-0-5": {
    padding: theme.spacing(0.5)
  },
  ".pl-0-5": {
    paddingLeft: theme.spacing(0.5)
  },
  ".pt-0-5": {
    paddingTop: theme.spacing(0.5)
  },
  ".pr-0-5": {
    paddingRight: theme.spacing(0.5)
  },
  ".pb-0-5": {
    paddingBottom: theme.spacing(0.5)
  },
  ".p-0": {
    padding: 0
  },
  ".pl-0": {
    paddingLeft: 0
  },
  ".pt-0": {
    paddingTop: 0
  },
  ".pr-0": {
    paddingRight: 0
  },
  ".pb-0": {
    paddingBottom: 0
  },
  ".m-3": {
    margin: theme.spacing(3)
  },
  ".mt-3": {
    marginTop: theme.spacing(3)
  },
  ".ml-3": {
    marginLeft: theme.spacing(3)
  },
  ".mr-3": {
    marginRight: theme.spacing(3)
  },
  ".mb-3": {
    marginBottom: theme.spacing(3)
  },
  ".m-2": {
    margin: theme.spacing(2)
  },
  ".mt-2": {
    marginTop: theme.spacing(2)
  },
  ".ml-2": {
    marginLeft: theme.spacing(2)
  },
  ".mr-2": {
    marginRight: theme.spacing(2)
  },
  ".mb-2": {
    marginBottom: theme.spacing(2)
  },
  ".m-1-5": {
    margin: theme.spacing(1.5)
  },
  ".mt-1-5": {
    marginTop: theme.spacing(1.5)
  },
  ".ml-1-5": {
    marginLeft: theme.spacing(1.5)
  },
  ".mr-1-5": {
    marginRight: theme.spacing(1.5)
  },
  ".mb-1-5": {
    marginBottom: theme.spacing(1.5)
  },
  ".m-1": {
    margin: theme.spacing(1)
  },
  ".mt-1": {
    marginTop: theme.spacing(1)
  },
  ".ml-1": {
    marginLeft: theme.spacing(1)
  },
  ".mr-1": {
    marginRight: theme.spacing(1)
  },
  ".mb-1": {
    marginBottom: theme.spacing(1)
  },
  ".mt-0-5": {
    marginTop: theme.spacing(0.5)
  },
  ".mr-0-5": {
    marginRight: theme.spacing(0.5)
  },
  ".ml-0-5": {
    marginLeft: theme.spacing(0.5)
  },
  ".mb-0-5": {
    marginBottom: theme.spacing(0.5)
  },
  ".m-0": {
    margin: 0
  },
  ".mt-auto": {
    marginTop: "auto"
  },
  ".ml-0": {
    marginLeft: 0
  },
  ".mt-0": {
    marginTop: 0
  },
  ".mr-0": {
    marginRight: 0
  },
  ".mb-0": {
    marginBottom: 0
  },
  ".text-uppercase": {
    textTransform: "uppercase"
  },
  ".overflow-visible": {
    overflow: "visible"
  },
  ".overflow-hidden": {
    overflow: "hidden"
  },
  ".overflow-y-auto": {
    overflowY: "auto"
  },
  ".overflow-auto": {
    overflow: "auto"
  },
  ".overflow-initial": {
    overflow: "initial"
  },
  ".d-block": {
    display: "block"
  },
  ".d-inline": {
    display: "inline"
  },
  ".d-inline-block": {
    display: "inline-block"
  },
  ".d-grid": {
    display: "grid"
  },
  ".d-flex": {
    display: "flex"
  },
  ".d-inline-flex": {
    display: "inline-flex"
  },
  ".flex-row": {
    display: "flex",
    flexDirection: "row"
  },
  ".flex-column": {
    display: "flex",
    flexDirection: "column"
  },
  ".centeredFlex": {
    display: "flex",
    alignItems: "center"
  },
  ".d-flex-start": {
    display: "flex",
    alignItems: "flex-start"
  },
  ".d-inline-flex-center": {
    display: "inline-flex",
    alignItems: "center"
  },
  ".d-none": {
    display: "none"
  },
  ".flex-fill": {
    flex: 1
  },
  ".flex-nowrap": {
    flexWrap: "nowrap"
  },
  ".align-items-baseline": {
    alignItems: "baseline"
  },
  ".align-items-center": {
    alignItems: "center"
  },
  ".align-items-start": {
    alignItems: "flex-start"
  },
  ".align-items-end": {
    alignItems: "flex-end"
  },
  ".align-content-between": {
    alignContent: "space-between"
  },
  ".align-content-start": {
    alignContent: "flex-start"
  },
  ".justify-content-end": {
    justifyContent: "flex-end"
  },
  ".justify-content-start": {
    justifyContent: "flex-start"
  },
  ".justify-content-center": {
    justifyContent: "center"
  },
  ".justify-content-space-between": {
    justifyContent: "space-between"
  },
  ".outline-none": {
    outline: "none"
  },
  ".disabled": {
    opacity: 0.6,
    pointerEvents: "none"
  },
  ".op075": {
    opacity: 0.75
  },
  ".op05": {
    opacity: 0.5
  },
  ".invisible": {
    visibility: "hidden"
  },
  ".visible": {
    visibility: "visible"
  },
  ".text-left": {
    textAlign: "left"
  },
  ".text-center": {
    textAlign: "center"
  },
  ".text-end": {
    textAlign: "end"
  },
  ".text-disabled": {
    color: theme.palette.text.disabled
  },
  ".cursor-grab": {
    cursor: "grab"
  },
  ".cursor-pointer": {
    cursor: "pointer"
  },
  ".pointer-events-none": {
    pointerEvents: "none"
  },
  ".text-truncate": {
    overflow: "hidden",
    textOverflow: "ellipsis"
  },
  ".text-nowrap": {
    whiteSpace: "nowrap"
  },
  ".text-pre-wrap": {
    whiteSpace: "pre-wrap"
  },
  ".text-pre-line": {
    whiteSpace: "pre-line"
  },
  ".word-break-all": {
    wordBreak: "break-all"
  },
  ".h-100": {
    height: "100%"
  },
  ".w-100": {
    width: "100%"
  },
  ".mw-100": {
    maxWidth: "100%"
  },
  ".mw-800": {
    maxWidth: theme.spacing(100)
  },
  ".mw-500": {
    maxWidth: theme.spacing(62.5)
  },
  ".mw-600": {
    maxWidth: theme.spacing(75)
  },
  ".mw-400": {
    maxWidth: theme.spacing(50)
  },
  ".text-reset": {
    color: "inherit"
  },
  ".text-white": {
    color: "#fff"
  },
  ".zIndex1401": {
    zIndex: theme.zIndex.snackbar + 1
  },
  ".zIndex9": {
    zIndex: 9
  },
  ".zIndex2": {
    zIndex: 2
  },
  ".zIndex1": {
    zIndex: 1
  },
  ".zIndex0": {
    zIndex: 0
  },
  ".box-shadow-none": {
    boxShadow: "none"
  },
  ".user-select-none": {
    WebkitTouchCallout: "none",
    KhtmlUserSelect: "none",
    ...prefixer("userSelect", "none")
  },
  ".vert-align-mid": {
    verticalAlign: "middle"
  },
  ".grid-temp-col-3-fr": {
    gridTemplateColumns: "1fr 1fr 1fr"
  },
});
