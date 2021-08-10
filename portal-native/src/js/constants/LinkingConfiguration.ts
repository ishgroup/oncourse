import * as Linking from 'expo-linking';
import { LinkingOptions } from '@react-navigation/native/lib/typescript/src/types';
import { RootStackParamList } from '../model/Navigation';

const LinkingConfig: LinkingOptions<RootStackParamList> = {
  prefixes: [Linking.makeUrl('/')],
  config: {
    screens: {
      Root: {
        screens: {
          Timetable: 'timetable',
          Logout: 'logout',
        },
      },
      ClassRoll: 'classRoll/:sessionId',
      Login: 'login',
      NotFound: '*',
    },
  },
};

export default LinkingConfig;
