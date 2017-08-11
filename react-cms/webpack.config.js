const path = require("path");
const webpack = require('webpack');

const __common = require("./webpack/__common");
const __DEFAULT_ENTRY = "./src/js/app.tsx";

const config = {
  entry: ["./src/js/app.tsx"],
};

module.exports = (options = {}) => {
  options[__common.KEYS.ENTRY] = options[__common.KEYS.ENTRY] || __DEFAULT_ENTRY;
  const result = Object.assign({}, config, __common.common(__dirname, options));
  result.plugins = [...result.plugins, ...plugins(options)];
  return result;
};


const plugins = (NODE_ENV) => {
  const plugins = [];

  switch (NODE_ENV) {
    case "production":
      plugins.push(__common.CompressionPlugin());
      break;
  }
  return plugins;
};
