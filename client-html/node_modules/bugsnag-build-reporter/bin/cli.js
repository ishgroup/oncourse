#!/usr/bin/env node

const meow = require('meow')

const argv = meow(`
  Usage
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

    bugsnag-build-reporter \\
      --api-key cc814aead128d38d0767094327b4784a \\
      --app-version 1.3.5

    bugsnag-build-reporter \\
      -k cc814aead128d38d0767094327b4784a \\
      -v 1.3.5
`, {
  flags: {
    apiKey: {
      type: 'string',
      alias: 'k'
    },
    appVersion: {
      type: 'string',
      alias: 'v'
    },
    releaseStage: {
      type: 'string',
      alias: 's'
    },
    sourceControlProvider: {
      type: 'string',
      alias: 'p'
    },
    sourceControlRepository: {
      type: 'string',
      alias: 'r'
    },
    sourceControlRevision: {
      type: 'string',
      alias: 'e'
    },
    appVersionCode: {
      type: 'string',
      alias: 'c'
    },
    appBundleVersion: {
      type: 'string',
      alias: 'b'
    },
    builderName: {
      type: 'string',
      alias: 'n'
    },
    autoAssignRelease: {
      type: 'boolean',
      alias: 'a'
    },
    endpoint: {
      type: 'string',
      alias: 'u'
    }
  }
})

if (argv.flags.sourceControlProvider && argv.flags.sourceControlRepository && argv.flags.sourceControlRevision) {
  argv.flags.sourceControl = {
    provider: argv.flags.sourceControlProvider,
    repository: argv.flags.sourceControlRepository,
    revision: argv.flags.sourceControlRevision
  }
}

if (argv.flags.h === true) {
  argv.showHelp()
} else {
  require('../index')(argv.flags, { path: process.cwd(), endpoint: argv.flags.endpoint })
    .then(() => {})
    .catch(() => { process.exitCode = 1 })
}
