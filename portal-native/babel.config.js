const path = require('path');

module.exports = (api) => {
  api.cache(true);
  return {
    presets: ['babel-preset-expo'],
    plugins: [
      ['module-resolver', {
        alias: {
          '@api/model': path.resolve(__dirname, 'build/generated-sources/api.ts')
        }
      }]
    ]
  };
};
