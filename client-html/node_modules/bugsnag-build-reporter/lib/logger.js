const pfx = `[bugsnag-build-reporter]`
const chalk = require('chalk')

module.exports = logLevel => {
  logLevel = logLevel || 'info'
  return {
    debug: function () {
      if ([ 'debug' ].indexOf(logLevel) === -1) return
      console.log.bind(console, chalk.gray(pfx)).apply(console, arguments)
    },
    info: function () {
      if ([ 'debug', 'info' ].indexOf(logLevel) === -1) return
      console.log.bind(console, chalk.blue(pfx)).apply(console, arguments)
    },
    warn: function () {
      if ([ 'debug', 'info', 'warn' ].indexOf(logLevel) === -1) return
      console.log.bind(console, chalk.yellow(pfx)).apply(console, arguments)
    },
    error: function () {
      if ([ 'debug', 'info', 'warn', 'error' ].indexOf(logLevel) === -1) return
      console.log.bind(console, chalk.red(pfx)).apply(console, arguments)
    }
  }
}
