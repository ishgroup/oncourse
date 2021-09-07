import * as Linking from 'expo-linking';
import { LinkingOptions } from '@react-navigation/native/lib/typescript/src/types';
import { RootStackParamList } from '../model/Navigation';
import { Platform } from 'react-native';

const subdir = Platform.OS === 'web' ? 'new/' : '';

const LinkingConfig: LinkingOptions<RootStackParamList> = {
  prefixes: [Linking.makeUrl('/')],
  config: {
    screens: {
      Root: {
        screens: {
          Timetable: `${subdir}timetable`,
          Logout: `${subdir}logout`,
        },
      },
      ClassRoll: `${subdir}classRoll/:sessionId`,
      Login: `${subdir}login`,
      NotFound: `${subdir}*`,
    },
  },
};

export default LinkingConfig;
