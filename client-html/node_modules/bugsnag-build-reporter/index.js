const makePayload = require('./lib/payload')
const send = require('./lib/send')
const logger = require('./lib/logger')

module.exports = (build, opts) => {
  opts = opts || {}
  const logLevel = opts.logLevel ? opts.logLevel : 'info'
  const log = opts.logger ? opts.logger : logger(logLevel)
  const path = opts.path ? opts.path : process.cwd()
  const endpoint = opts.endpoint ? opts.endpoint : 'https://build.bugsnag.com'

  return new Promise((resolve, reject) => {
    const onError = (error) => {
      log.error(`${error.message}`)
      if (error.errors) {
        log.error(`  ${error.errors.join(', ')}`)
      } else {
        log.error(`Error detail…\n${error.stack}`)
      }
      reject(new Error('bugsnag-build-reporter failed'))
    }

    const onSuccess = () => {
      log.info(`build info sent`)
      resolve()
    }
    makePayload(build, path, log, (err, data) => {
      if (err) return onError(err)
      log.info('sending', data)
      send(endpoint, data, onSuccess, onError)
    })
  })
}
