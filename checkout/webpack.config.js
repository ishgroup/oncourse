const __common = require('./webpack/__common');
const fs = require('fs');
const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CompressionPlugin = require("compression-webpack-plugin");
const ZipPlugin = require('zip-webpack-plugin');
const { BugsnagBuildReporterPlugin, BugsnagSourceMapUploaderPlugin } = require('webpack-bugsnag-plugins');
const TerserPlugin = require("terser-webpack-plugin");

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
  return {
    entry: {
      dynamic: [
        'url-polyfill',
        'custom-event-polyfill',
        'react',
        'redux',
        'react-redux',
        'rxjs',
        path.resolve(__dirname, 'src', 'js', 'app.tsx')
      ]
    },
    output: {
      path: path.resolve(__dirname, 'build', 'dist'),
      filename: '[name].js',
      publicPath: '/'
    },
    resolve: {
      modules: [
        path.resolve(__dirname, "src/js"),
        path.resolve(__dirname, 'src/scss'),
        "node_modules"
      ],
      extensions: [".ts", ".tsx", ".js", ".css", ".scss"]
    },
    optimization: {
      minimizer: [
        new TerserPlugin({
          parallel: 4,
        }),
      ],
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          use: [
            {
              loader: 'babel-loader',
              options: {
                presets: ['@babel/preset-react', "@babel/preset-env"]
              }
            },
            {
              loader: 'ts-loader'
            }
          ]
        },
        {
          test: /\.js$/,
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-react', "@babel/preset-env"]
          }
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
    devServer: {
      inline: false,
      port: 1707,
      stats: {
        chunkModules: false,
        colors: true
      },
      historyApiFallback: true,
      contentBase: './build/dist',
      proxy: [{
        context: '/a',
        target: API_ROOT,
        pathRewrite: {
          '^/a/': ''
        }
      }]
    }
  };
};

const plugins = (NODE_ENV, BUILD_NUMBER) => {
  const plugins = [
    __common.DefinePlugin(NODE_ENV, BUILD_NUMBER),
    new MiniCssExtractPlugin({ filename: "main.css" }),
    new webpack.optimize.ModuleConcatenationPlugin(),
  ];

  switch (NODE_ENV) {
    case "production":
      plugins.push(
        new CompressionPlugin({
          filename: "[file].gz[query]",
          algorithm: "gzip",
          test: /\.(js|html|css|map)$/,
          minRatio: Infinity,
        }),
        new ZipPlugin({
          path: '../distribution',
          filename: 'checkout.zip',

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
        }),
        new BugsnagBuildReporterPlugin({
          apiKey: '08b42469660b2c6a50e998866c7a2bee',
          releaseStage: 'production',
          appVersion: `${BUILD_NUMBER}`
        }),
        new BugsnagSourceMapUploaderPlugin({
          apiKey: '08b42469660b2c6a50e998866c7a2bee',
          releaseStage: 'production',
          appVersion: `${BUILD_NUMBER}`,
          uploadSource: true,
          overwrite: true,
          publicPath: '*/'
        }, {
          logLevel: 'debug'
        })
      );
      break;
    case "development":
      plugins.push(
        htmlPlugin("enrol/checkout/index.html"),
        htmlPlugin("courses/index.html"),
        htmlPlugin("courses/one_class.html"),
        htmlPlugin("products/index.html")
      );
      break;
  }
  return plugins;
};

const htmlPlugin = (name) => {
  return new HtmlWebpackPlugin({
    filename: `${name}`,
    template: path.resolve(__dirname, "./dev-server/", name),
    inject: false
  });
};
