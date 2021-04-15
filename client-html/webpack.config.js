/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
  const SOURCE_MAP = options.SOURCE_MAP || "none";
  const BUILD_NUMBER = options.BUILD_NUMBER || "latest";
  __common.info(NODE_ENV, SOURCE_MAP, BUILD_NUMBER);

  const main = _main(NODE_ENV, SOURCE_MAP, BUILD_NUMBER);
  main.module.rules = [
    ...main.module.rules,
    ...__common.styleModule(__dirname),
  ];
  return main;
};

const _main = (NODE_ENV, SOURCE_MAP, BUILD_NUMBER) => {
  const appEntry = NODE_ENV === "mock"
      ? path.resolve(__dirname, "src", "dev", "app.tsx")
      : path.resolve(__dirname, "src", "js", "app.tsx");

  return {
    entry: {
      client: [appEntry],
    },
    mode: "production",
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
    },
    module: {
      rules: [
        {
          test: /\.ts(x?)$/,
          use: [
            {
              loader: "ts-loader",
              options: {
                transpileOnly: true,
                happyPackMode: true,
              },
            },
          ],
        },
      ],
    },
    bail: false,
    cache: false,
    plugins: plugins(NODE_ENV, BUILD_NUMBER),
    devServer: {
      inline: false,
      writeToDisk: true,
      port: 1707,
      stats: {
        chunkModules: false,
        colors: true,
      },
      historyApiFallback: true,
      contentBase: "./build/dist",
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
        }),

        new ForkTsCheckerWebpackPlugin({
          async: false,
        }),
        new webpack.SourceMapDevToolPlugin({
          filename: `[file].map`,
          test: /^[a-zA-Z-]*.js/,
          exclude: [/vendor/],
          noSources: true,
        }),
        new webpack.EnvironmentPlugin({
          RELEASE_VERSION: BUILD_NUMBER,
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
