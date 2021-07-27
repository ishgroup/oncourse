import { Platform } from 'react-native';
import { createStyles } from '../../hooks/styles';

export const useStyles = createStyles((theme) => ({
  topPart: {
    flex: 3,
    backgroundColor: theme.colors.background,
  },
  bottomPart: {
    flex: 2,
    backgroundColor: theme.colors.accent,
  },
  loginContainerWrapper: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    top: 0,
    alignItems: 'center',
    justifyContent: 'center'
  },
  loginContainer: {
    ...Platform.OS === 'android' ? { minHeight: 460 } : {},
    width: 340,
    borderRadius: theme.spacing(3),
    padding: theme.spacing(2),
    display: 'flex',
    flexDirection: 'row'
  },
  loginContainerFullScreen: {
    width: '100%',
    height: '100%',
    borderRadius: 0,
    paddingLeft: theme.spacing(2),
    paddingRight: theme.spacing(2),
    paddingBottom: theme.spacing(2),
    overflow: 'scroll'
  },
  headline: {
    paddingLeft: 12,
  },
  logo: {
    height: 140,
    width: 140,
  },
  caption: {
    paddingTop: theme.spacing(3),
    width: 340,
  },
  socialNetworkImage: {
    margin: theme.spacing(1),
    height: 30,
    width: 30,
  },
  content: {
    flex: 1,
    padding: theme.spacing(2),
    justifyContent: 'center',
  },
  companySwitch: {
    marginTop: theme.spacing(1),
    justifyContent: 'flex-end',
    flexDirection: 'row',
    zIndex: 1,
    elevation: 1
  }
}));
