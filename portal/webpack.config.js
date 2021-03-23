const __common = require('./webpack/__common');
const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const TerserPlugin = require('terser-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require("fork-ts-checker-webpack-plugin");
const glob = require("glob");


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
    mode: NODE_ENV,
    entry: {
      main: [
        path.resolve(__dirname, 'src', 'js', 'app.tsx')
      ],
      "portal-min": glob.sync("./src/main/static/js/*.js")
    },
    output: {
      path: path.resolve(__dirname, 'build/resource-assemble/static/js'),
      filename: '[name].js',
      publicPath: '/'
    },
    resolve: {
      modules: [
        path.resolve(__dirname, "src/main/static/js"),
        path.resolve(__dirname, "src/js"),
        "node_modules",
      ],
      extensions: [".ts", ".tsx", ".js", ".css", ".scss"]
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'ts-loader',
          exclude: /node_modules/,
          options: {
            transpileOnly: true
          }
        }
      ]
    },
    optimization: {
      minimizer: [
        new TerserPlugin({
          parallel: 4,
          sourceMap: true
        })
      ]
    },
    bail: false,
    devtool: SOURCE_MAP,
    plugins: plugins(NODE_ENV, BUILD_NUMBER),
    devServer: {
      port: 1707,
      contentBase: path.resolve(__dirname, 'build/resource-assemble/static/js'),
    }
  };
};

const plugins = (NODE_ENV, BUILD_NUMBER) => {
  const plugins = [
    new webpack.IgnorePlugin(/^(\.\/locale|jquery|\.\/item|desandro-matches-selector|ev-emitter|fizzy-ui-utils|get-size|outlayer)$/),
    __common.DefinePlugin(NODE_ENV, BUILD_NUMBER),
    new webpack.EnvironmentPlugin({
      RELEASE_VERSION: BUILD_NUMBER
    }),
    new MiniCssExtractPlugin("[name].css"),
    new webpack.optimize.ModuleConcatenationPlugin(),
  ];

  switch (NODE_ENV) {
    case "production":
      plugins.push(
        new ForkTsCheckerWebpackPlugin(),
        new CompressionPlugin({
          algorithm: "gzip",
          test: /\.(js|html|css|map)$/,
          minRatio: Infinity,
        }),
      );
      break;
    case "development":
      // plugins.push(
      //   htmlPlugin("enrol/checkout/index.html"),
      //   htmlPlugin("courses/index.html"),
      //   htmlPlugin("courses/one_class.html"),
      //   htmlPlugin("products/index.html")
      // );
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
