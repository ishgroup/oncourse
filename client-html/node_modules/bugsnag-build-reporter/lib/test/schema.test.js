const test = require('tape')
const schema = require('../schema')
const validate = require('../validate')

test('schema: invalid', t => {
  validate(schema)({}, (err) => {
    if (!err) return t.fail('error expected')
    t.ok(err)
    t.ok(Array.isArray(err.errors))
    t.equal(err.errors.length, 2)
    t.deepEqual(err.errors, [ 'apiKey is required (got "undefined")', 'appVersion is required (got "undefined")' ])
    t.end()
  })
})

test('schema: invalid 2', t => {
  validate(schema)({
    apiKey: '123',
    appVersion: '1.2.3',
    sourceControl: { foo: 10 },
    metadata: { nested: { objs: [ 'are', 'not', 'allowed' ] } }
  }, (err) => {
    if (!err) return t.fail('error expected')
    t.ok(err)
    t.ok(Array.isArray(err.errors))
    t.equal(err.errors.length, 2)
    t.deepEqual(err.errors, [
      'metadata may be an object with key/value pairs where the only permitted values are strings (got "{ nested: { objs: [ \'are\', \'not\', \'allowed\' ] } }")',
      'sourceControl must be an object containing { provider, repository, revision } (got "{ foo: 10 }")'
    ])
    t.end()
  })
})

test('schema: invalid 3', t => {
  validate(schema)({
    apiKey: '123',
    appVersion: '1.2.3',
    sourceControl: 1,
    metadata: 'aaaa'
  }, (err) => {
    if (!err) return t.fail('error expected')
    t.ok(err)
    t.ok(Array.isArray(err.errors))
    t.equal(err.errors.length, 2)
    t.deepEqual(err.errors, [
      'metadata may be an object with key/value pairs where the only permitted values are strings (got "\'aaaa\'")',
      'sourceControl must be an object containing { provider, repository, revision } (got "1")'
    ])
    t.end()
  })
})

test('schema: valid', t => {
  validate(schema)({ apiKey: '123', appVersion: '1.2.3' }, (err) => {
    t.ok(!err)
    t.end(err)
  })
})

test('schema: valid 2', t => {
  validate(schema)({
    apiKey: '123',
    appVersion: '1.2.3',
    metadata: { 'foo': 'bar', 'meta': 'data' },
    sourceControl: {
      repository: 'http://github.com/org/repo',
      revision: 'ababababa',
      provider: 'github'
    },
    builderName: null,
    releaseStage: 'qa',
    autoAssignRelease: false
  }, (err) => {
    t.ok(!err)
    t.end(err)
  })
})

test('schema: valid 3', t => {
  validate(schema)({
    apiKey: '123',
    appVersion: '1.2.3',
    metadata: { 'foo': 'bar', 'meta': 'data' },
    sourceControl: {
      repository: 'http://github.com/org/repo',
      revision: 'ababababa',
      provider: 'github'
    },
    builderName: null,
    releaseStage: 'qa',
    autoAssignRelease: null
  }, (err) => {
    t.ok(!err)
    t.end(err)
  })
})
