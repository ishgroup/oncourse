const exec = require('child_process').exec
module.exports = cb => exec('whoami', cb)
