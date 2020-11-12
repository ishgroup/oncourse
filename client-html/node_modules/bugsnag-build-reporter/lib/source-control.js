const findNearest = require('find-nearest-file')
const readFile = require('fs').readFile
const exec = require('child_process').exec
const parallel = require('run-parallel')
const relative = require('path').relative

module.exports = (path, log, cb) => {
  const pkg = findNearest('package.json', path)
  if (pkg) log.info('found a manifest:', relative(path, pkg))
  parallel([
    cb => extractPackageJsonRepoInfo(pkg, log, cb),
    cb => detectSourceInfoFromRepo(path, log, cb)
  ], (err, data) => {
    if (err) {
      log.error(err)
      return cb(null, null)
    }
    if (!data) return cb(null, null)
    const repository = (data[0] && data[0].repository)
        ? data[0].repository
        : ((data[1] && data[1].repository)
          ? data[1].repository
          : undefined)
    const provider = getProviderFromUrl(repository)
    const revision = data[1] ? data[1].revision : undefined
    if (!provider || !repository || !revision) {
      log.warn('no source control info found (looked in package.json, .git, .hg)')
      return cb(null, null)
    }
    cb(null, { provider, repository, revision })
  })
}

const getProviderFromUrl = repo => {
  if (!repo) return null
  if (/^git@github\.com|https:\/\/github.com/.test(repo)) return 'github'
  if (/^git@gitlab\.com|https:\/\/gitlab.com/.test(repo)) return 'gitlab'
  if (/^git@bitbucket\.org|https:\/\/bitbucket.org|ssh:\/\/hg@bitbucket\.org|https:\/\/\w+@bitbucket\.org\//.test(repo)) return 'bitbucket'
  return null
}

const extractPackageJsonRepoInfo = (path, log, cb) => {
  if (!path) return cb(null, null)
  readFile(path, 'utf8', (err, data) => {
    if (err) {
      log.error(err)
      return cb(null)
    }
    try {
      const pkg = JSON.parse(data)
      if (!pkg.repository) return cb(null, null)
      log.debug('repository info in manifest:', { repository: pkg.repository.url })
      cb(null, { repository: pkg.repository.url })
    } catch (e) {
      log.error(err)
      cb(null)
    }
  })
}

const detectSourceInfoFromRepo = (path, log, cb) => {
  const onSuccess = (type, data) => {
    log.debug(`${type} repo info:`, data)
    cb(null, data)
  }

  const onError = () => {
    cb(null, null)
  }

  detectSourceInfoFromGit(path, (err, data) => {
    if (!err && data) return onSuccess('git', data)
    detectSourceInfoFromMercurial(path, (err, data) => {
      if (!err && data) return onSuccess('mercurial', data)
      onError()
    })
  })
}

const detectSourceInfoFromGit = (cwd, cb) => {
  parallel([
    cb => exec('git rev-parse HEAD', { cwd }, cb),
    cb => {
      // eslint-disable-next-line
      exec('git remote get-url origin', { cwd }, (err, data) => {
        // ignore this error in case origin doesn't exist
        cb(null, data)
      })
    },
    cb => exec('git remote -v', { cwd }, cb)
  ], (err, data) => {
    cb(err, {
      revision: data[0] ? data[0].trim() : null,
      repository: data[1] ? data[1].trim() : (data[2] ? data[2].split(/\t|\s|\n/)[1] : null)
    })
  })
}

const detectSourceInfoFromMercurial = (cwd, cb) => {
  parallel([
    cb => exec('hg id -i', { cwd }, cb),
    cb => exec('hg paths default', { cwd }, cb)
  ], (err, data) => {
    cb(err, {
      revision: data[0] ? data[0].trim() : null,
      repository: data[1] ? data[1].trim() : null
    })
  })
}
