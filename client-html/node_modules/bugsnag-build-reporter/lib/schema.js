const nullUndefinedOr = fn => value => {
  if (value === undefined) return true
  if (value === null) return true
  return fn(value)
}

const optionalString = {
  validate: value => value === undefined || value === null || (typeof value === 'string' && value.length),
  message: 'must be a string, null or undefined'
}

const requiredString = {
  validate: value => typeof value === 'string' && value.length,
  message: 'is required'
}

module.exports = {
  apiKey: requiredString,
  appVersion: requiredString,
  appVersionCode: optionalString,
  appBundleVersion: optionalString,
  builderName: optionalString,
  releaseStage: optionalString,
  buildTool: optionalString,
  metadata: {
    validate: nullUndefinedOr(value => {
      if (value && typeof value === 'object') {
        return Object.keys(value).reduce((accum, k) => {
          return accum && typeof value[k] === 'string'
        }, true)
      }
      return false
    }),
    message: 'may be an object with key/value pairs where the only permitted values are strings'
  },
  sourceControl: {
    validate: nullUndefinedOr(value => {
      if (value && typeof value === 'object') {
        return value.provider && value.repository && value.revision
      }
      return false
    }),
    message: 'must be an object containing { provider, repository, revision }'
  },
  autoAssignRelease: {
    validate: nullUndefinedOr(value => value === true || value === false)
  }
}
