const autoDetectSourceControl = require('../source-control')
const test = require('tape')
const noopLogger = { debug: () => {}, info: () => {}, warn: () => {}, error: () => {} }
const copy = require('directory-copy')
const tempy = require('tempy')
const fs = require('fs')

test('autoDetectSourceControl(): git repo', t => {
  const tmp = tempy.directory()
  copy({
    src: `${__dirname}/fixtures/git`,
    dest: tmp
  }, (err) => {
    t.ok(!err)
    fs.renameSync(`${tmp}/_git`, `${tmp}/.git`)
    autoDetectSourceControl(tmp, noopLogger, (err, sourceControl) => {
      t.ok(!err)
      t.deepEqual(sourceControl, {
        provider: 'github',
        repository: 'git@github.com:bugsnag/fictional-repo.git',
        revision: '436739224cebc54aee8c5a61b8e00d68d4759faf'
      })
      t.end()
    })
  })
})

test('autoDetectSourceControl(): mercurial repo', t => {
  const tmp = tempy.directory()
  copy({
    src: `${__dirname}/fixtures/hg`,
    dest: tmp
  }, (err) => {
    t.ok(!err)
    autoDetectSourceControl(tmp, noopLogger, (err, sourceControl) => {
      t.ok(!err)
      t.deepEqual(sourceControl, {
        provider: 'bitbucket',
        repository: 'https://bugsnag@bitbucket.org/bugsnag/fictional_repo',
        revision: '4d1ccd3c4b12'
      })
      t.end()
    })
  })
})

test('autoDetectSourceControl(): git+package.json repo', t => {
  const tmp = tempy.directory()
  copy({
    src: `${__dirname}/fixtures/pkg`,
    dest: tmp
  }, (err) => {
    t.ok(!err)
    fs.renameSync(`${tmp}/_git`, `${tmp}/.git`)
    autoDetectSourceControl(tmp, noopLogger, (err, sourceControl) => {
      t.ok(!err)
      t.deepEqual(sourceControl, {
        provider: 'github',
        repository: 'https://github.com/bugsnag/not-real.git',
        revision: 'c177f39c361696468126a191ea29e01868ee6c55'
      })
      t.end()
    })
  })
})

test('autoDetectSourceControl(): directory with no repo or manifest', t => {
  const tmp = tempy.directory()
  copy({
    src: `${__dirname}/fixtures/nothing`,
    dest: tmp
  }, (err) => {
    t.ok(!err)
    autoDetectSourceControl(tmp, noopLogger, (err, sourceControl) => {
      t.ok(!err)
      t.equal(sourceControl, null)
      t.end()
    })
  })
})
