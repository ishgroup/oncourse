/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

const webpack = require("webpack");
const path = require("path");
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CompressionPlugin = require("compression-webpack-plugin");

module.exports = () => {
  const swaggerPath = process.argv.indexOf("schema") !== -1 && process.argv[process.argv.indexOf("schema") + 1];

  if (!swaggerPath) {
    throw new Error("Swagger schema file is required !");
  }

  return {
    entry: [
      swaggerPath,
      "./src/apiDocs.tsx"
    ],
    output: {
      path: path.resolve(__dirname, "build/api-docs"),
    },
    mode: process.env.NODE_ENV,
    resolve: {
      modules: [
        "node_modules"
      ],
      extensions: [".ts", ".tsx", ".js"]
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
              }
            }
          ],
          include: [
            path.resolve(__dirname, "src/apiDocs.tsx")
          ],
          exclude: [
            path.resolve(__dirname, "node_modules")
          ]
        },
        {
          test: /\.(yaml)$/,
          use: [{
            loader: 'file-loader',
            options: {
              name: '[name].[ext]'
            },
          }]
        },
      ]
    },
    plugins: [
      new webpack.ProvidePlugin({
        process: 'process/browser',
        Buffer: "buffer"
      }),
      new HtmlWebpackPlugin({
        template: "src/api-doc-template.html",
      }),
      new webpack.WatchIgnorePlugin({
        paths: [
          /\.js$/,
          path.resolve(__dirname, "node_modules"),
        ],
      }),
      new MiniCssExtractPlugin({ filename: '[name].css' }),
      new webpack.SourceMapDevToolPlugin({
        filename: "[file].map",
        exclude: [/vendor/, /images/, /hot-update/]
      }),
      new CompressionPlugin({
        filename: `[file].gz`,
        algorithm: "gzip",
        test: /\.(js|html|css)$/,
        threshold: 10240,
        minRatio: 0.8
      })
    ],
    optimization: {
      minimizer: [
        new TerserPlugin({
          parallel: 4
        })
      ]
    },
    devServer: {
      hot: true,
      port: 8100
    },
    devtool: false
  };
};
