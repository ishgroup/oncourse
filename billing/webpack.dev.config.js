const path = require("path");
const HtmlWebpackPlugin = require('html-webpack-plugin');
const __common = require("./webpack/__common");
const __DEFAULT_ENTRY = "./src/js/app.tsx";

const config = {
  entry: ["./src/js/app.tsx"],
};

module.exports = (options = {}) => {
  options[__common.KEYS.ENTRY] = options[__common.KEYS.ENTRY] || __DEFAULT_ENTRY;
  const commonConfig = __common.common(__dirname, options);
  commonConfig.plugins.push(
      new HtmlWebpackPlugin({
        title: "Billing",
        template: "src/index.html",
        favicon: "src/images/favicon.ico",
        chunksSortMode: 'none'
      }),
  );

  return { ...config, ...commonConfig };
};
