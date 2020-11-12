const inspect = require('util').inspect

module.exports = schema => (opts, cb) => {
  const errors = Object.keys(schema).reduce((accum, key) => {
    if (schema[key].validate(opts[key])) return accum
    return accum.concat({ key, message: schema[key].message, value: inspect(opts[key]) })
  }, [])
  if (!errors.length) return cb(null, opts)
  const err = new Error('Configuration error')
  err.errors = errors.map(e => `${e.key} ${e.message} (got "${e.value}")`)
  cb(err)
}
