const path = require("path");
const common = require("./webpack/__common");

const config = {
    entry: ["./src/dev/AddContactApp.tsx"],
};

module.exports = Object.assign({}, config, common.common(__dirname));
