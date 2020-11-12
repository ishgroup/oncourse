'use strict'

const https = require('https')
const http = require('http')
const concat = require('concat-stream')
const url = require('url')
const once = require('once')

const MAX_ATTEMPTS = 5
const RETRY_INTERVAL = parseInt(process.env.BUGSNAG_RETRY_INTERVAL) || 1000
const TIMEOUT = parseInt(process.env.BUGSNAG_TIMEOUT) || 30000

module.exports = (endpoint, data, onSuccess, onError) => {
  let attempts = 0
  const maybeRetry = (err) => {
    attempts++
    if (err && err.isRetryable !== false && attempts < MAX_ATTEMPTS) return setTimeout(go, RETRY_INTERVAL)
    return onError(err)
  }
  const go = () => send(endpoint, data, onSuccess, maybeRetry)
  go()
}

const send = (endpoint, data, onSuccess, onError) => {
  onError = once(onError)
  const parsedUrl = url.parse(endpoint)
  const payload = JSON.stringify(data)
  const req = (parsedUrl.protocol === 'https:' ? https : http).request({
    method: 'POST',
    hostname: parsedUrl.hostname,
    path: parsedUrl.path || '/',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(payload)
    },
    port: parsedUrl.port || undefined
  }, res => {
    res.pipe(concat(body => {
      if (res.statusCode === 200) return onSuccess()
      if (res.statusCode !== 400) {
        const err = new Error(`HTTP status ${res.statusCode} received from builds API`)
        if (!isRetryable(res.statusCode)) {
          err.isRetryable = false
        }
        return onError(err)
      }
      try {
        const err = new Error('Invalid payload sent to builds API')
        err.errors = JSON.parse(body).errors
        // never retry a 400
        err.isRetryable = false
        return onError(err)
      } catch (_) {
        const e = new Error(`HTTP status ${res.statusCode} received from builds API`)
        e.isRetryable = false
        return onError(e)
      }
    }))
  })
  req.on('error', onError)
  req.write(payload)
  req.setTimeout(TIMEOUT, () => {
    onError(new Error('Connection timed out'))
    req.abort()
  })
  req.end()
}

const isRetryable = status => {
  return (
    status < 400 ||
    status > 499 ||
    [
      408, // timeout
      429 // too many requests
    ].indexOf(status) !== -1)
}
