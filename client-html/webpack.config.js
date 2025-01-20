/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

const path = require("path");
const webpack = require("webpack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const TerserPlugin = require('terser-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require("fork-ts-checker-webpack-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const { BugsnagBuildReporterPlugin, BugsnagSourceMapUploaderPlugin } = require('webpack-bugsnag-plugins');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const __common = require("./webpack/__common");

module.exports = function (options = {}) {
  const NODE_ENV = options.NODE_ENV || "development";
  const BUILD_NUMBER = options.BUILD_NUMBER || "99-SNAPSHOT";
  __common.info(NODE_ENV, BUILD_NUMBER);

  const main = _main(NODE_ENV, BUILD_NUMBER);
  main.module.rules = [
    ...main.module.rules,
    ...__common.styleModule(__dirname),
  ];
  return main;
};

const _main = (NODE_ENV, BUILD_NUMBER) => {
  const appEntry = NODE_ENV === "mock"
      ? path.resolve(__dirname, "src", "dev", "app.tsx")
      : path.resolve(__dirname, "src", "js", "app.tsx");

  return {
    entry: {
      client: [appEntry],
    },
    mode: NODE_ENV,
    output: {
      path: path.resolve(__dirname, "build", "assets"),
      filename: `[name].${BUILD_NUMBER}.js`,
      chunkFilename: `[name].${BUILD_NUMBER}.js`,
      publicPath: "/",
    },
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: "vendors",
            enforce: true,
            reuseExistingChunk: true,
          },
        },
      },
      minimizer: [
        new TerserPlugin({
          parallel: 4,
        }),
      ],
    },
    resolve: {
      modules: [
        "node_modules",
        path.resolve(__dirname, "src/js"),
      ],
      extensions: [".ts", ".tsx", ".js"],
      plugins: [
        new TsconfigPathsPlugin({ configFile: path.resolve(__dirname, './tsconfig.json') }),
      ],
      fallback: { 'process/browser': require.resolve('process/browser') }
    },
    module: {
      rules: [
        {
          test: /\.ts(x?)$/,
          use: [
            {
              loader: "swc-loader"
            },
          ],
        },
      ],
    },
    bail: false,
    cache: false,
    plugins: plugins(NODE_ENV, BUILD_NUMBER),
    devServer: {
      historyApiFallback: true,
      static: "./build/dist",
      client: {
        overlay: false
      }
    },
    performance: {
      hints: false, // don't keep telling us to make the js smaller
    },
    stats: {
      warningsFilter: /export .* was not found in/,
      warnings: true,
      errorDetails: true,
      colors: true,
    },
  };
};

const plugins = (NODE_ENV, BUILD_NUMBER) => {
  const plugins = [
    new webpack.ProvidePlugin({
      process: 'process/browser',
    }),
    new CleanWebpackPlugin({
      verbose: true,
    }),
    new MiniCssExtractPlugin({ filename: '[name].css' }),
    __common.DefinePlugin(NODE_ENV, BUILD_NUMBER),
    __common.PwaManifestPlugin(),
    __common.GenerateSW(),
  ];

  switch (NODE_ENV) {
    case "production": {
      plugins.push(
        new HtmlWebpackPlugin({
          title: "onCourse client",
          template: "src/index-template.html",
          favicon: "src/images/favicon.ico",
          chunksSortMode: 'none',
          meta: {
            [""]: {
              ["http-equiv"]: "Content-Security-Policy",
              ["content"]: "" +
              " default-src 'self'" +
              " localhost:* ws://localhost:* https://127.0.0.1:8182/a/" +
              " https://*.google-analytics.com" +
              " https://*.googletagmanager.com" +
              " https://*.googleapis.com" +
              " https://*.google.com" +
              " https://*.stripe.com" +
              " https://*.s3.ap-southeast-2.amazonaws.com" +
              " https://*.ish.com.au" +
              " 'unsafe-inline';" +
              " img-src * 'self' data: https:;" +
              " frame-src 'self' data: https:;"
            }
          }
        }),
        new ForkTsCheckerWebpackPlugin({
          async: false,
        }),
        new webpack.EnvironmentPlugin({
          RELEASE_VERSION: BUILD_NUMBER,
        }),
        new webpack.SourceMapDevToolPlugin({
          filename: `[file].map`,
          exclude: [/vendor/]
        }),
        new CompressionPlugin({
          filename: `[file].gz`,
          algorithm: "gzip",
          test: /\.(js|html|css)$/,
          threshold: 10240,
          minRatio: 0.8,
        }),
      );
      if (BUILD_NUMBER !== "99-SNAPSHOT") {
        plugins.push(
          new BugsnagBuildReporterPlugin({
            apiKey: '8fc0c45fd7cbb17b6e8d6cad20738799',
            releaseStage: 'production',
            appVersion: `${BUILD_NUMBER}`,
          }),
          new BugsnagSourceMapUploaderPlugin({
            apiKey: '8fc0c45fd7cbb17b6e8d6cad20738799',
            releaseStage: 'production',
            appVersion: `${BUILD_NUMBER}`,
            uploadSource: true,
            overwrite: true,
            publicPath: '*/',
          }, {
            logLevel: 'debug',
          }),
        );
      }
      break;
    }
    case "development":
      plugins.push(new webpack.HotModuleReplacementPlugin());
      break;
  }
  return plugins;
};