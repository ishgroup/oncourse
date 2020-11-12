# bugsnag-build-reporter
[![Build status](https://travis-ci.com/bugsnag/bugsnag-build-reporter-node.svg?branch=master)](https://travis-ci.com/bugsnag/bugsnag-build-reporter-node)
[![NPM](https://img.shields.io/npm/v/bugsnag-build-reporter.svg)](https://npmjs.org/package/bugsnag-build-reporter)

A tool for reporting your application's builds to Bugsnag. It can auto detect source control from `.git`, `.hg` and `package.json`.
If you are using any of the following tools in your build, you may find the following modules (that use this one under the hood) more convenient:

| Tool    | Module | Version |
| ---     | ---    | ---     |
| Webpack | [webpack-bugsnag-plugins](https://github.com/bugsnag/webpack-bugsnag-plugins) | [![NPM](https://img.shields.io/npm/v/webpack-bugsnag-plugins.svg)](https://npmjs.org/package/webpack-bugsnag-plugins)
| Gulp    | [gulp-bugsnag](https://github.com/bugsnag/gulp-bugsnag) | [![NPM](https://img.shields.io/npm/v/gulp-bugsnag.svg)](https://npmjs.org/package/gulp-bugsnag) |
| Grunt   | [grunt-bugsnag](https://github.com/bugsnag/grunt-bugsnag) | [![NPM](https://img.shields.io/npm/v/grunt-bugsnag.svg)](https://npmjs.org/package/grunt-bugsnag) |

`bugsnag-build-reporter` should be used directly if your application is built by:

- some arbitrary Node-based environment (e.g. a build tool not listed above or something custom)
- a command based tool (e.g. npm scripts, make)

## Usage

This module can be used in a Node environment via the JS API or as a CLI.

#### JS API

To use the JS API, install this module as a development dependency of your project:

```
npm i --save-dev bugsnag-build-reporter
```

Then use like so:

```js
const reportBuild = require('bugsnag-build-reporter')
reportBuild({ apiKey: 'YOUR_API_KEY', appVersion: '1.2.3' }, { /* opts */ })
  .then(() => console.log('success!'))
  .catch(err => console.log('fail', err.messsage))
```

##### `reportBuild(build: object, opts: object) => Promise`

- `build` describes the build you are reporting to Bugsnag
  - `apiKey: string` your Bugsnag API key __[required]__
  - `appVersion: string` the version of the application you are building __[required]__
  - `releaseStage: string` `'production'`, `'staging'` etc. (leave blank if this build can be released to different `releaseStage`s)
  - `sourceControl: object` an object describing the source control of the build (if not specified, the module will attempt to detect source control information from `.git`, `.hg` and the nearest `package.json`)
    - `provider: string` can be one of: `'github'`, `'github-enterprise'`, `'gitlab'`, `'gitlab-onpremise'`, `'bitbucket'`, `'bitbucket-server'`
    - `repository: string` a URL (`git`/`ssh`/`https`) pointing to the repository, or webpage representing the repository
    - `revision: string` the unique identifier for the commit (e.g. git SHA)
  - `builderName: string` the name of the person/machine that created this build (defaults to the result of the `whoami` command)
  - `autoAssignRelease: boolean` automatically associate this build with any new error events and sessions that are received for the `releaseStage` until a subsequent build notification is received. If this is set to `true` and no `releaseStage` is provided the build will be applied to `'production'`.
  - `appVersionCode: string` if you're using this module to report Android app builds, set this option
  - `appBundleVersion: string` if you're using this module to report iOS/macOS/AppleTV app builds, set this option
- `opts`
  - `logLevel: string` the minimum severity of log to output (`'debug'`, `'info'`, `'warn'`, `'error'`)
  - `logger: object` provide a different logger object `{ debug, info, warn, error }`
  - `path: string` the path to search for source control info, defaults to `process.cwd()`
  - `endpoint: string` post the build payload to a URL other than the default (`https://build.bugsnag.com`)

#### CLI

To use the CLI, install it from npm:

```
# locally
npm i --save-dev bugsnag-build-reporter
# or globally
npm i --global bugsnag-build-reporter
```

_If installed locally, recent versions of npm come with a tool called [`npx`](https://github.com/zkat/npx) which will help run it without typing burdensome paths._

```
$ bugsnag-build-reporter <flags> <metadata>

Options
  --api-key, -k  Set your notifier API key [required]
  --app-version, -v  Set the app version [required]
  --release-stage, -s Set the release stage

  --source-control-provider, -p  Set the repo provider
  --source-control-repository, -r  Set the repo URL
  --source-control-revision, -e  Set the source control revision id (e.g commit SHA)

  --builder-name, -n  Set the name of the entity that triggered the build
  --auto-assign-release, -a  Assign any subsequent error reports received to this release
  --endpoint, -u Specify an alternative endpoint

  --app-version-code, -c  Set the version code (Android only)
  --app-bundle-version, -b  Set the bundle version (iOS/macOS/tvOS only)

  metadata
  Arbitrary "key=value" pairs will be passed to the build API as metadata
  e.g. foo=bar

Examples

  bugsnag-build-reporter \
  --api-key cc814aead128d38d0767094327b4784a \
  --app-version 1.3.5

  bugsnag-build-reporter \
  -k cc814aead128d38d0767094327b4784a \
  -v 1.3.5
```

## Examples

Here are some examples of using `bugsnag-build-reporter`:

#### JS API

```js
const reportBuild = require('bugsnag-build-reporter')
reportBuild({
  apiKey: 'YOUR_API_KEY',
  appVersion: '1.2.3',
  builderName: 'Katherine Johnson'
}).then(() => {
  /* success */
}, err => {
  /* error */
})
```

#### npm scripts

```js
scripts: {
  "report-build": "bugsnag-build-reporter -k YOUR_API_KEY -v '1.2.3' -n 'Katherine Johnson'"
}
```

#### make

```
PATH := node_modules/.bin:$(PATH)
SHELL := /bin/bash

report-build:
	bugsnag-build-reporter -k YOUR_API_KEY -v "1.2.3" -n "Katherine Johnson"
```

## Support

- [Search open and closed issues](https://github.com/bugsnag/bugsnag-build-reporter-node/issues?q=is%3Aissue) issues for similar problems
- [Report a bug or request a feature](https://github.com/bugsnag/bugsnag-build-reporter-node/issues/new)
- Email [support@bugsnag.com](mailto:support@bugsnag.com)

## Contributing

All contributors are welcome! See our [contributing guide](CONTRIBUTING.md).

## License

This module is free software released under the MIT License. See [LICENSE.txt](LICENSE.txt) for details.
