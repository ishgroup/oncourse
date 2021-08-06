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
          Class: 'class/:id',
          Logout: 'logout',
        },
      },
      Login: 'login',
      NotFound: '*',
    },
  },
};

export default LinkingConfig;
