import * as Linking from 'expo-linking';

export default {
  prefixes: [Linking.makeUrl('/')],
  config: {
    screens: {
      Root: {
        screens: {
          Feed: "feed",
          Notifications: "notifications",
        },
      },
      NotFound: '*',
    },
  },
};
