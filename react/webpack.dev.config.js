const path = require("path");
const __common = require("./webpack/__common");
const __DEFAULT_ENTRY = "./src/dev/checkout/CheckoutApp.tsx";

const config = {
    entry: ["./src/dev/checkout/CheckoutApp.tsx"],
    plugins: [ __common.DefinePlugin('development', 'http://localhost:10080') ]
};

module.exports = (options = {}) => {
  options[__common.KEYS.ENTRY] = options[__common.KEYS.ENTRY] || __DEFAULT_ENTRY;
  return Object.assign({}, config, __common.common(__dirname, options));
};
