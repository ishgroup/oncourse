const __common = require('./webpack/__common');
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
  __common.info(NODE_ENV, API_ROOT, API_ROOT);

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
    resolve: {
      modules: [path.resolve(__dirname, "src/js"),
        "node_modules"],
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
                loader: 'css-loader'
            }]
        }]
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
    plugins: createListOfPlugins({NODE_ENV, API_ROOT}),
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
  }
};

function createListOfPlugins({NODE_ENV, API_ROOT}) {
  const plugins = [ __common.DefinePlugin(NODE_ENV, API_ROOT) ];

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
      getHtmlWebpackPlugin("courses/one_class.html"),
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
