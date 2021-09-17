export default {
  version: '1.0.0',
  name: 'skillsOnCourse',
  slug: 'skillsOnCourse',
  orientation: 'portrait',
  icon: './src/assets/images/icon.png',
  scheme: 'ish.oncourse.willow.portal',
  userInterfaceStyle: 'automatic',
  splash: {
    image: './src/assets/images/splash.png',
    backgroundColor: '#fbf9f0'
  },
  updates: {
    fallbackToCacheTimeout: 0
  },
  assetBundlePatterns: [
    '**/*'
  ],
  ios: {
    buildNumber: '1.0.1',
    supportsTablet: true,
    bundleIdentifier: 'ish.oncourse.willow.portal',
    icon: './src/assets/images/ios-icon-1024.png'
  },
  android: {
    versionCode: 1,
    package: 'ish.oncourse.willow.portal',
    intentFilters: [
      {
        action: 'VIEW',
        autoVerify: true,
        data: [
          {
            scheme: 'https',
            host: 'www.skillsoncourse.com.au',
            pathPrefix: '/new/',
            path: '/new'
          }
        ],
        category: [
          'BROWSABLE',
          'DEFAULT'
        ]
      }
    ]
  },
  web: {
    favicon: './src/assets/images/favicon-32x32.png'
  },
  description: '',
  extra: {
    bugsnag: {
      apiKey: '39edd3b25913061f92c1966a1017181e'
    },
    enableMockedApi: process.env.mockApi
  },
  hooks: {
    postPublish: [
      {
        file: '@bugsnag/expo/hooks/post-publish.js',
        config: {}
      }
    ]
  }
};
