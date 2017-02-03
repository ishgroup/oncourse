const path = require('path');
const webpack = require('webpack');
const fs = require('fs');

module.exports = function (options = {}) {
  // Settings
  // --env.API_ROOT root --env.SOURCE_MAP source-map ...
  const NODE_ENV = options.NODE_ENV || 'development'; // 'production'
  const SOURCE_MAP = options.SOURCE_MAP || 'source-map'; // 'eval-source-map'
  const API_ROOT = options.API_ROOT || 'http://localhost:8080/api/v1'; // 'https://ish.com.au/api/v1'

  console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ API_ROOT: ${API_ROOT}
`);

  return {
    entry: {
      polyfill: [
        'babel-polyfill',
      ],
      dynamic: [
        'react',
        'redux',
        'react-redux',
        path.resolve(__dirname, 'src', 'js', 'app.ts')
      ]
    },
    output: {
      path: path.resolve(__dirname, 'dist'),
      filename: '[name].js',
      publicPath: '/'
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
    resolve: {
      extensions: ['.ts', '.tsx', '.js']
    },
    bail: false,
    devtool: SOURCE_MAP,
    module: {
      rules: [{
        test: /\.tsx?$/,
        loader: 'ts-loader',
        exclude: /node_modules/,
      }, {
        test: /\.js$/,
        loader: 'babel-loader',
        options: {
          presets: [['es2015', {loose: true}], 'react'],
          plugins: ['transform-object-rest-spread']
        },
        exclude: /node_modules/
      }]
    },
    plugins: [
      new webpack.DefinePlugin({
        'process.env': {
          'NODE_ENV': JSON.stringify(NODE_ENV)
        },
        API_ROOT: JSON.stringify(API_ROOT)
      })
    ]
  }
};
