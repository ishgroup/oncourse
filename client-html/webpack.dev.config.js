/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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

  commonConfig.devServer.contentBase = './build/dist';

  return { ...config, ...commonConfig };
};
