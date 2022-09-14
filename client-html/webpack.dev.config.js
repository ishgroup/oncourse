/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

const __common = require("./webpack/__common");

const __DEFAULT_ENTRY = "./src/js/app.tsx";
const HtmlWebpackPlugin = require('html-webpack-plugin');

const config = {
  entry: ["./src/js/app.tsx"],
};

module.exports = (options = {}) => {
  options[__common.KEYS.ENTRY] = options[__common.KEYS.ENTRY] || __DEFAULT_ENTRY;
  options['BUILD_NUMBER'] = 17;
  const commonConfig = __common.common(__dirname, options);
  commonConfig.plugins.push(
    new HtmlWebpackPlugin({
      title: "Angel client",
      template: "src/index-template.html",
      favicon: "src/images/favicon.ico",
      chunksSortMode: 'none'
    }),
  );

  commonConfig.devServer.static = './build/dist';

  return { ...config, ...commonConfig };
};
