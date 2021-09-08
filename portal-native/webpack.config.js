const createExpoWebpackConfigAsync = require('@expo/webpack-config');
const path = require('path');
const ZipPlugin = require('zip-webpack-plugin');

// Expo CLI will await this method so you can optionally return a promise.
module.exports = async function (env, argv) {
  const config = await createExpoWebpackConfigAsync({
    ...env,
    babel: {
      dangerouslyAddModulePathsToTranspile: ['@bugsnag/plugin-react-native-unhandled-rejection']
    }
  }, argv);

  if (config.mode === 'production') {
    const buildPath = path.resolve(__dirname, 'build', 'web');
    config.output.path = buildPath;
    config.plugins[1].patterns[0].to = buildPath;
    config.plugins[1].patterns[1].to = buildPath;
    config.plugins[2].options.filename = `${buildPath}/index.html`;
    config.plugins.push(
      new ZipPlugin({
        path: '../distribution',
        filename: 'portal-native.zip',
        fileOptions: {
          mtime: new Date(),
          mode: 0o100664,
          compress: true,
          forceZip64Format: false,
        },
      }),
    );

    // Placing fonts in static folder
    config.module.rules[1].oneOf[4].use[0].options.name = './static/fonts/[name].[ext]';
  }

  return config;
};
