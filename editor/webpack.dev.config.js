const path = require("path");
const __common = require("./webpack/__common");
const __DEFAULT_ENTRY = "./src/js/app.tsx";

const config = {
  entry: ["./src/js/app.tsx"],
};

module.exports = (options = {}) => {
  options[__common.KEYS.ENTRY] = options[__common.KEYS.ENTRY] || __DEFAULT_ENTRY;
  return Object.assign({}, config, __common.common(__dirname, options));
};
