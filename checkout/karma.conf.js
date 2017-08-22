const path = require('path');
const webpack = require('webpack');
const fs = require('fs');

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    files: [
      'spec.js'
    ],
    webpack: {
      // Shared with webpack.config.js
      // Can be moved to common module...
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
      resolve: {
        extensions: ['.ts', '.tsx', '.js']
      },
      module: {
        rules: [{
          test: /\.tsx?$/,
          loader: 'ts-loader',
          exclude: /node_modules/,
        }, {
          test: /\.css$/,
          use: [{
            loader: 'style-loader'
          }, {
            loader: 'css-loader',
            options: {
              modules: true
            }
          }]
        }]
      },
      devtool: 'inline-source-map',
      plugins: [
        new webpack.DefinePlugin({
          'process.env': {
            'NODE_ENV': JSON.stringify("DEVELOPMENT")
          },
          _API_ROOT: JSON.stringify("http://localhost:8080/api/v1"),
          _APP_VERSION: JSON.stringify("TEST")
        }),
        new webpack.ProvidePlugin({
          'React': 'react',
          '$': 'jquery'
        })
      ]
    },
    webpackMiddleware: {
      stats: {
        chunkModules: false,
        colors: true
      }
    },
    preprocessors: {
      'spec.js': ['webpack', 'sourcemap']
    },
    reporters: ['progress'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: false,
    browsers: ['PhantomJS'],
    singleRun: true
  });
};
