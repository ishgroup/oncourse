const path = require("path");
const __common = require("./webpack/__common");

const config = {
    entry: ["./src/dev/CheckoutApp.tsx"],
    plugins: [ __common.DefinePlugin('development', 'http://localhost:10080') ]
};

module.exports = Object.assign({}, config, __common.common(__dirname));
