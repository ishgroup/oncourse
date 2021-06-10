const createExpoWebpackConfigAsync = require('@expo/webpack-config');
const path = require('path');

// Expo CLI will await this method so you can optionally return a promise.
module.exports = async function(env, argv) {

  const config = await createExpoWebpackConfigAsync(env, argv);

  const buildPath = path.resolve(__dirname, 'build', 'web');

  config.output.path = buildPath;
  config.plugins[1].patterns[0].to = buildPath;
  config.plugins[1].patterns[1].to = buildPath;
  config.plugins[2].options.filename = buildPath + "/index.html";

  return config;
};
