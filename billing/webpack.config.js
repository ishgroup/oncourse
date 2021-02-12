const __common = require('./webpack/__common');

const fs = require('fs');
const path = require('path');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CompressionPlugin = require("compression-webpack-plugin");
const ZipPlugin = require('zip-webpack-plugin');

module.exports = function (options = {}) {
  const NODE_ENV = options.NODE_ENV || 'development';
  const SOURCE_MAP = options.SOURCE_MAP || 'source-map';
  const API_ROOT = options.API_ROOT || 'http://localhost:10080';
  const BUILD_NUMBER = options.BUILD_NUMBER || 'DEV';
  __common.info(NODE_ENV, SOURCE_MAP, API_ROOT, BUILD_NUMBER);

  const main = _main(NODE_ENV, SOURCE_MAP, API_ROOT, BUILD_NUMBER);
  main.module.rules = [...main.module.rules, ...__common.styleModule(__dirname)]
  return main;
};

const _main = (NODE_ENV, SOURCE_MAP, API_ROOT, BUILD_NUMBER) => {
  const mode = NODE_ENV || 'development';
  const appEntry = NODE_ENV === 'mock'
    ? path.resolve(__dirname, 'src', 'dev', 'app.tsx')
    : path.resolve(__dirname, 'src', 'js', 'app.tsx');

  return {
    entry: {
      billing: [
        appEntry
      ]
    },
    output: {
      path: path.resolve(__dirname, 'build', 'assets'),
      filename: '[name].js',
      publicPath: '/'
    },
    resolve: {
      modules: [
        path.resolve(__dirname, 'node_modules'),
        path.resolve(__dirname, 'build/generated-sources'),
        path.resolve(__dirname, 'src/js'),
        path.resolve(__dirname, 'src/scss'),
      ],
      extensions: [".ts", ".tsx", ".js", ".css"]
    },
    mode: mode,
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'awesome-typescript-loader',
          exclude: /node_modules/,
        }
      ]
    },
    externals: [function (context, request, callback) {
      const p = path.resolve(context, request) + '.js';

      if (/.custom.js$/.test(p)) {
        fs.stat(p, (err) => {
          if (err) {
            callback(null, "{}");
            return;
          }

          callback();
        });
        return;
      }

      callback();
    }],
    bail: false,
    devtool: SOURCE_MAP,
    plugins: plugins(NODE_ENV, BUILD_NUMBER),
  };
};

const plugins = (NODE_ENV, BUILD_NUMBER) => {
  const plugins = [
    new MiniCssExtractPlugin({filename: "[name].css"}),
    __common.DefinePlugin(NODE_ENV, BUILD_NUMBER),
  ];

  plugins.push(
    new HtmlWebpackPlugin({
      title: "Billing",
      template: "src/index.html",
      favicon: "src/images/favicon.ico",
      chunksSortMode: 'none'
    }),
    new CompressionPlugin({
      asset: "[path].gz[query]",
      algorithm: "gzip",
      test: /\.(js|html|css|map)$/,
      minRatio: Infinity,
    }),
    new ZipPlugin({
      path: '../distribution',
      filename: 'billing.zip',

      // OPTIONAL: defaults to excluding nothing
      // can be a string, a RegExp, or an array of strings and RegExps
      // if a file matches both include and exclude, exclude takes precedence
      exclude: [/\.js$/, /\.css$/, /\.map$/],

      // OPTIONAL: see https://github.com/thejoshwolfe/yazl#addfilerealpath-metadatapath-options
      fileOptions: {
        mtime: new Date(),
        mode: 0o100664,
        compress: true,
        forceZip64Format: false,
      },
    })
  );

  return plugins;
};

