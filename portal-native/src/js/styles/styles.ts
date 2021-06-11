import {StyleSheet} from "react-native";
import {SPACING_UNIT} from "../constants/Layout";
import {theme} from "./theme";

export const spacing = (unit: number) => unit * SPACING_UNIT;

export const cs = StyleSheet.create({
  strong: {
    fontWeight: "bold"
  },
  fontWeightNormal: {
    fontWeight: "normal"
  },
  relative: {
    position: "relative"
  },
  absolute: {
    position: "absolute"
  },
  left0: {
    left: 0
  },
  top0: {
    top: 0
  },
  right0: {
    right: 0
  },
  bottom0: {
    bottom: 0
  },
  pr6: {
    paddingRight: spacing(6)
  },
  p4: {
    padding: spacing(4)
  },
  pr4: {
    paddingRight: spacing(4)
  },
  p3: {
    padding: spacing(3)
  },
  pt3: {
    paddingTop: spacing(3)
  },
  pl3: {
    paddingLeft: spacing(3)
  },
  pr3: {
    paddingRight: spacing(3)
  },
  pb3: {
    paddingBottom: spacing(3)
  },
  p2: {
    padding: spacing(2)
  },
  pl2: {
    paddingLeft: spacing(2)
  },
  pr2: {
    paddingRight: spacing(2)
  },
  pb2: {
    paddingBottom: spacing(2)
  },
  pt2: {
    paddingTop: spacing(2)
  },
  p1: {
    padding: spacing(1)
  },
  pt1: {
    paddingTop: spacing(1)
  },
  pb1: {
    paddingBottom: spacing(1)
  },
  pl1: {
    paddingLeft: spacing(1)
  },
  pr1: {
    paddingRight: spacing(1)
  },
  p05: {
    padding: spacing(0.5)
  },
  pl05: {
    paddingLeft: spacing(0.5)
  },
  pt05: {
    paddingTop: spacing(0.5)
  },
  pr05: {
    paddingRight: spacing(0.5)
  },
  pb05: {
    paddingBottom: spacing(0.5)
  },
  p0: {
    padding: 0
  },
  pl0: {
    paddingLeft: 0
  },
  pt0: {
    paddingTop: 0
  },
  pr0: {
    paddingRight: 0
  },
  pb0: {
    paddingBottom: 0
  },
  m3: {
    margin: spacing(3)
  },
  mt3: {
    marginTop: spacing(3)
  },
  ml3: {
    marginLeft: spacing(3)
  },
  mr3: {
    marginRight: spacing(3)
  },
  mb3: {
    marginBottom: spacing(3)
  },
  m2: {
    margin: spacing(2)
  },
  mt2: {
    marginTop: spacing(2)
  },
  ml2: {
    marginLeft: spacing(2)
  },
  mr2: {
    marginRight: spacing(2)
  },
  mb2: {
    marginBottom: spacing(2)
  },
  m1: {
    margin: spacing(1)
  },
  mt1: {
    marginTop: spacing(1)
  },
  ml1: {
    marginLeft: spacing(1)
  },
  mr1: {
    marginRight: spacing(1)
  },
  mb1: {
    marginBottom: spacing(1)
  },
  mt05: {
    marginTop: spacing(0.5)
  },
  mr05: {
    marginRight: spacing(0.5)
  },
  ml05: {
    marginLeft: spacing(0.5)
  },
  mb05: {
    marginBottom: spacing(0.5)
  },
  m0: {
    margin: 0
  },
  mtAuto: {
    marginTop: "auto"
  },
  ml0: {
    marginLeft: 0
  },
  mt0: {
    marginTop: 0
  },
  mr0: {
    marginRight: 0
  },
  mb0: {
    marginBottom: 0
  },
  textUppercase: {
    textTransform: "uppercase"
  },
  overflowVisible: {
    overflow: "visible"
  },
  overflowHidden: {
    overflow: "hidden"
  },
  dFlex: {
    display: "flex"
  },
  flexRow: {
    display: "flex",
    flexDirection: "row"
  },
  flexColumn: {
    display: "flex",
    flexDirection: "column"
  },
  flexCenter: {
    display: "flex",
    alignItems: "center"
  },
  dFlexStart: {
    display: "flex",
    alignItems: "flex-start"
  },
  dNone: {
    display: "none"
  },
  flex1: {
    flex: 1
  },
  flexNowrap: {
    flexWrap: "nowrap"
  },
  alignItemsBaseline: {
    alignItems: "baseline"
  },
  alignItemsCenter: {
    alignItems: "center"
  },
  alignItemsStart: {
    alignItems: "flex-start"
  },
  alignItemsEnd: {
    alignItems: "flex-end"
  },
  alignContentBetween: {
    alignContent: "space-between"
  },
  alignContentStart: {
    alignContent: "flex-start"
  },
  justifyContentEnd: {
    justifyContent: "flex-end"
  },
  justifyContentStart: {
    justifyContent: "flex-start"
  },
  justifyContentCenter: {
    justifyContent: "center"
  },
  justifyContentSpaceBetween: {
    justifyContent: "space-between"
  },
  op075: {
    opacity: 0.75
  },
  op05: {
    opacity: 0.5
  },
  textLeft: {
    textAlign: "left"
  },
  textCenter: {
    textAlign: "center"
  },
  h100: {
    height: "100%"
  },
  w100: {
    width: "100%"
  },
  mw100: {
    maxWidth: "100%"
  },
  mw800: {
    maxWidth: spacing(100)
  },
  mw500: {
    maxWidth: spacing(62.5)
  },
  mw600: {
    maxWidth: spacing(75)
  },
  mw400: {
    maxWidth: spacing(50)
  },
  textWhite: {
    color: "#fff"
  },
  zIndex9: {
    zIndex: 9
  },
  zIndex2: {
    zIndex: 2
  },
  zIndex1: {
    zIndex: 1
  },
  zIndex0: {
    zIndex: 0
  },
  colorPrimary: {
    color: theme.colors.primary
  }
})
