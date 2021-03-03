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
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = () => ({
    entry: ["./src/api-docs/apiDocs.tsx"],
    output: {
      publicPath: "/",
      path: path.resolve(__dirname, "build"),
    },
    mode: "development",
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
                happyPackMode: true
              }
            }
          ],
          include: [
            path.resolve(__dirname, "src/api-docs/apiDocs.tsx")
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
      new HtmlWebpackPlugin({
        template: "src/api-docs/api-doc-template.html",
      }),
      new webpack.WatchIgnorePlugin([
        /\.js$/,
        path.resolve(__dirname, "node_modules")
      ]),
      new MiniCssExtractPlugin({ filename: '[name].css' }),
      new webpack.SourceMapDevToolPlugin({
        filename: "[file].map",
        exclude: [/vendor/, /images/, /hot-update/]
      })
    ],
    devServer: {
      inline: true,
        hot: true,
        port: 8100
    },
    devtool: false,
      node: {
    fs: "empty"
  }
});
