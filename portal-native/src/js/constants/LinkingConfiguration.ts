import * as Linking from 'expo-linking';

export default {
  prefixes: [Linking.makeUrl('/')],
  config: {
    screens: {
      Root: {
        screens: {
          Dashboard: 'dashboard',
          Timetable: 'timetable',
          Notifications: 'notifications',
        },
      },
      Login: 'login',
      NotFound: '*',
    },
  },
};
