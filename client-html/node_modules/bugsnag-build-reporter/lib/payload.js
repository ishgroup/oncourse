const parallel = require('run-parallel')
const autoDetectSourceControl = require('./source-control')
const whoami = require('./whoami')
const schema = require('./schema')
const validate = require('./validate')

module.exports = (opts, path, log, cb) => {
  const obj = Object.keys(schema).reduce((accum, key) => {
    accum[key] = opts[key] || undefined
    return accum
  }, {})

  // special implicit defaults
  const fns = {}

  if (!obj.buildTool) obj.buildTool = 'bugsnag-build-reporter'

  // if sourceControl is not defined, auto detect from repos/manifests
  if (obj.sourceControl === undefined) {
    fns.sourceControl = cb => {
      autoDetectSourceControl(path, log, (err, data) => {
        if (err) {
          log.warn('error detecting repository info from source control', err)
          return cb(null, null)
        }
        cb(null, data)
      })
    }
  }

  // if builderName is not defined, use the result of `whoami`
  if (obj.builderName === undefined) {
    fns.builderName = whoami
  }

  parallel(fns, (err, data) => {
    if (err) return cb(err)
    if (data.sourceControl) obj.sourceControl = data.sourceControl
    if (data.builderName) obj.builderName = data.builderName.trim()
    validate(schema)(obj, cb)
  })
}
