const fs = require('fs');
const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TypedocWebpackPlugin = require('typedoc-webpack-plugin');

module.exports = function (options = {}) {
  // Settings
  // --env.API_ROOT root --env.SOURCE_MAP source-map ...
  const NODE_ENV = options.NODE_ENV || 'development'; // 'production'
  const SOURCE_MAP = options.SOURCE_MAP || 'source-map'; // 'eval-source-map'
  const API_ROOT = options.API_ROOT || 'http://localhost:10080'; // 'https://ish.com.au/api/v1'

  console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ API_ROOT: ${API_ROOT}
`);

  return {
    entry: {
      dynamic: [
        'babel-polyfill',
        'react',
        'redux',
        'react-redux',
        'rxjs',
        path.resolve(__dirname, 'src', 'js', 'app.ts')
      ]
    },
    output: {
      path: path.resolve(__dirname, 'build', 'dist'),
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
        test: /\.css$/,
        use: [{
          loader: 'style-loader'
        }, {
          loader: 'css-loader'
        }]
      }]
    },
    plugins: createListOfPlugins({NODE_ENV, API_ROOT}),
    devServer: {
      inline: false,
      port: 1707,
      stats: {
        chunkModules: false,
        colors: true
      },
      historyApiFallback: true,
      contentBase: './',
      proxy: [{
        context: '/api',
        target: API_ROOT,
        pathRewrite: {
          '^/api/': ''
        }
      }]
    }
  }
};

function createListOfPlugins({NODE_ENV, API_ROOT}) {
  const plugins = [
    new webpack.DefinePlugin({
      'process.env': {
        'NODE_ENV': JSON.stringify(NODE_ENV)
      },
      _API_ROOT: JSON.stringify(API_ROOT),
      _APP_VERSION: JSON.stringify(process.env.BUILD_NUMBER || "DEV")
    })
  ];

  if (NODE_ENV === "production") {
    plugins.push(new TypedocWebpackPlugin({
      jsx: "react",
      target: "es6",
      lib: [
        "lib.dom.d.ts",
        "lib.es5.d.ts",
        "lib.es2015.d.ts",
        "lib.es2016.d.ts",
        "lib.es2017.d.ts"
      ],
      allowSyntheticDefaultImports: true,
      moduleResolution: "node",
      module: "es6",
      out: "../docs" // relative to output
    }, "./src/js/"));
  }

  if (NODE_ENV === "development") {
    plugins.push(
      getHtmlWebpackPlugin("enrol/checkout/index.html"),
      getHtmlWebpackPlugin("courses/index.html"),
      getHtmlWebpackPlugin("products/index.html")
    )
  }

  function getHtmlWebpackPlugin(name) {
    return new HtmlWebpackPlugin({
      filename: `${name}`,
      template: path.resolve(__dirname, "./dev-server/", name),
      inject: false
    });
  }

  return plugins;
}
