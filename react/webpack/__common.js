const webpack = require('webpack');

const path = require("path");

const _info = (NODE_ENV, SOURCE_MAP, API_ROOT) => {
  console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ API_ROOT: ${API_ROOT}
`);
};

const KEYS = {
  ENTRY: "entry"
};

const _common = (dirname, options) => {
  console.log(options);
  return {
    entry: [options[KEYS.ENTRY]],
    output: {
      path: path.resolve(dirname, "build"),
      publicPath: "/assets/",
      filename: "dynamic.js"
    },
    resolve: {
      modules: [
        path.resolve(dirname, 'src/js'),
        path.resolve(dirname, 'src/dev'),
        path.resolve(dirname, 'src/scss'),
        path.resolve(dirname, 'node_modules')
      ],
      extensions: [".ts", ".tsx", ".js", ".css"]
    },
    module: {
      rules: [
        {
          test: /\.css$/,
          loaders: ['style-loader', 'css-loader'],
          include: [
            path.resolve(dirname, 'node_modules')
          ]
        },
        {
          test: /\.scss$/,
          loaders: ['style-loader', 'css-loader', 'sass-loader'],
          include: [
            path.resolve(dirname, "src/scss"),
          ]
        },
        {
          test: /\.tsx?$/,
          loader: 'ts-loader',
          include: [
            path.resolve(dirname, "src/js"),
            path.resolve(dirname, "src/dev"),
          ],
        },
        {
          enforce: "pre", test: /\.js$/, loader: "source-map-loader"
        }
      ]
    },
    devServer: {
      inline: false
    },
    devtool: 'source-map',
  }
};


/**
 * The DefinePlugin allows you to create global constants which can be configured at compile time.
 */
const _DefinePlugin = (NODE_ENV, API_ROOT) => {
    return new webpack.DefinePlugin({
      'process.env': {
        'NODE_ENV': JSON.stringify(NODE_ENV)
      },
      _API_ROOT: JSON.stringify(API_ROOT),
      _APP_VERSION: JSON.stringify(process.env.BUILD_NUMBER || "DEV")
    });
};


module.exports = {
    KEYS: KEYS,
    info: _info,
    common: _common,
    DefinePlugin: _DefinePlugin
};
