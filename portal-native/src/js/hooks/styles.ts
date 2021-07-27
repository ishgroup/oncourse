import { StyleSheet } from 'react-native';
import { IshTheme, useThemeContext } from '../styles';

export const createStyles = <T extends StyleSheet.NamedStyles<T>>
  (styles: (theme: IshTheme) => T): () => T => () => StyleSheet.create(styles(useThemeContext()));

export const useCommonStyles = () => {
  const theme = useThemeContext();
  return StyleSheet.create({
    strong: {
      fontWeight: 'bold'
    },
    fontWeightNormal: {
      fontWeight: 'normal'
    },
    relative: {
      position: 'relative'
    },
    absolute: {
      position: 'absolute'
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
      paddingRight: theme.spacing(6)
    },
    p4: {
      padding: theme.spacing(4)
    },
    pr4: {
      paddingRight: theme.spacing(4)
    },
    p3: {
      padding: theme.spacing(3)
    },
    pt3: {
      paddingTop: theme.spacing(3)
    },
    pl3: {
      paddingLeft: theme.spacing(3)
    },
    pr3: {
      paddingRight: theme.spacing(3)
    },
    pb3: {
      paddingBottom: theme.spacing(3)
    },
    p2: {
      padding: theme.spacing(2)
    },
    pl2: {
      paddingLeft: theme.spacing(2)
    },
    pr2: {
      paddingRight: theme.spacing(2)
    },
    pb2: {
      paddingBottom: theme.spacing(2)
    },
    pt2: {
      paddingTop: theme.spacing(2)
    },
    p1: {
      padding: theme.spacing(1)
    },
    pt1: {
      paddingTop: theme.spacing(1)
    },
    pb1: {
      paddingBottom: theme.spacing(1)
    },
    pl1: {
      paddingLeft: theme.spacing(1)
    },
    pr1: {
      paddingRight: theme.spacing(1)
    },
    p05: {
      padding: theme.spacing(0.5)
    },
    pl05: {
      paddingLeft: theme.spacing(0.5)
    },
    pt05: {
      paddingTop: theme.spacing(0.5)
    },
    pr05: {
      paddingRight: theme.spacing(0.5)
    },
    pb05: {
      paddingBottom: theme.spacing(0.5)
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
      margin: theme.spacing(3)
    },
    mt3: {
      marginTop: theme.spacing(3)
    },
    ml3: {
      marginLeft: theme.spacing(3)
    },
    mr3: {
      marginRight: theme.spacing(3)
    },
    mb3: {
      marginBottom: theme.spacing(3)
    },
    m2: {
      margin: theme.spacing(2)
    },
    mt2: {
      marginTop: theme.spacing(2)
    },
    ml2: {
      marginLeft: theme.spacing(2)
    },
    mr2: {
      marginRight: theme.spacing(2)
    },
    mb2: {
      marginBottom: theme.spacing(2)
    },
    m1: {
      margin: theme.spacing(1)
    },
    mt1: {
      marginTop: theme.spacing(1)
    },
    ml1: {
      marginLeft: theme.spacing(1)
    },
    mr1: {
      marginRight: theme.spacing(1)
    },
    mb1: {
      marginBottom: theme.spacing(1)
    },
    mt05: {
      marginTop: theme.spacing(0.5)
    },
    mr05: {
      marginRight: theme.spacing(0.5)
    },
    ml05: {
      marginLeft: theme.spacing(0.5)
    },
    mb05: {
      marginBottom: theme.spacing(0.5)
    },
    m0: {
      margin: 0
    },
    mtAuto: {
      marginTop: 'auto'
    },
    mbAuto: {
      marginBottom: 'auto'
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
      textTransform: 'uppercase'
    },
    overflowVisible: {
      overflow: 'visible'
    },
    overflowHidden: {
      overflow: 'hidden'
    },
    dFlex: {
      display: 'flex'
    },
    flexRow: {
      display: 'flex',
      flexDirection: 'row'
    },
    flexColumn: {
      display: 'flex',
      flexDirection: 'column'
    },
    flexCenter: {
      display: 'flex',
      alignItems: 'center'
    },
    dFlexStart: {
      display: 'flex',
      alignItems: 'flex-start'
    },
    dNone: {
      display: 'none'
    },
    flex1: {
      flex: 1
    },
    flexNowrap: {
      flexWrap: 'nowrap'
    },
    alignItemsBaseline: {
      alignItems: 'baseline'
    },
    alignItemsCenter: {
      alignItems: 'center'
    },
    alignItemsStart: {
      alignItems: 'flex-start'
    },
    alignItemsEnd: {
      alignItems: 'flex-end'
    },
    alignContentBetween: {
      alignContent: 'space-between'
    },
    alignContentStart: {
      alignContent: 'flex-start'
    },
    justifyContentEnd: {
      justifyContent: 'flex-end'
    },
    justifyContentStart: {
      justifyContent: 'flex-start'
    },
    justifyContentCenter: {
      justifyContent: 'center'
    },
    justifyContentSpaceBetween: {
      justifyContent: 'space-between'
    },
    op075: {
      opacity: 0.75
    },
    op05: {
      opacity: 0.5
    },
    textLeft: {
      textAlign: 'left'
    },
    textCenter: {
      textAlign: 'center'
    },
    h100: {
      height: '100%'
    },
    w100: {
      width: '100%'
    },
    mw100: {
      maxWidth: '100%'
    },
    mw800: {
      maxWidth: theme.spacing(100)
    },
    mw500: {
      maxWidth: theme.spacing(62.5)
    },
    mw600: {
      maxWidth: theme.spacing(75)
    },
    mw400: {
      maxWidth: theme.spacing(50)
    },
    textWhite: {
      color: '#fff'
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
    },
    colorText: {
      color: theme.colors.text
    },
    bgTransp: {
      backgroundColor: 'transparent',
    },
  });
};
