// Entries from webpack.conf.js
require("react");
require("redux");
require("react-redux");

const tests = require.context('./src/test', true, /\.tsx?$/);

// Make sure, that webpack will load all test files
tests.keys().forEach(function (key) {
  return tests(key);
});
