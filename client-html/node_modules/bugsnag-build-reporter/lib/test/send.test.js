'use strict'

process.env.BUGSNAG_RETRY_INTERVAL = 50
process.env.BUGSNAG_TIMEOUT = 100

const test = require('tape')
const http = require('http')
const net = require('net')
const send = require('../send')

test('send(): successful', t => {
  t.plan(1)
  const server = http.createServer((req, res) => {
    let body = ''
    req.on('data', (d) => { body += d })
    req.on('end', () => {
      res.end('ok')
      let j
      try {
        j = JSON.parse(body)
      } catch (e) {
        t.fail('failed to parse body as json')
      }
      t.ok(j, 'json body was received')
    })
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.end()
  }, (err) => {
    server.close()
    t.end(err)
  })
})

test('send(): unsuccessful (500)', t => {
  t.plan(2)
  const server = http.createServer((req, res) => {
    res.statusCode = 500
    res.end('error')
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.fail('send should not succeed')
  }, (err) => {
    server.close()
    t.ok(err)
    t.equal(err.message, 'HTTP status 500 received from builds API')
    t.end()
  })
})

test('send(): retry (500)', t => {
  t.plan(1)
  let n = 0
  const server = http.createServer((req, res) => {
    n++
    if (n < 4) {
      res.statusCode = 500
      return res.end('error')
    } else {
      return res.end('ok')
    }
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.equal(n, 4, 'it should make multiple attempts')
    t.end()
  }, (err) => {
    server.close()
    t.end(err)
  })
})

test('send(): unsuccessful (400)', t => {
  t.plan(3)
  const server = http.createServer((req, res) => {
    res.statusCode = 400
    res.setHeader('content-type', 'application/json')
    res.end('{ "errors": [ "flipflop is not a valid crankworble" ] }')
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.fail('send should not succeed')
  }, (err) => {
    server.close()
    t.ok(err)
    t.equal(err.message, 'Invalid payload sent to builds API')
    t.deepEqual(err.errors, [ 'flipflop is not a valid crankworble' ])
    t.end()
  })
})

test('send(): unsuccessful, doesn’t retry (400)', t => {
  t.plan(4)
  let n = 0
  const server = http.createServer((req, res) => {
    n++
    res.statusCode = 400
    res.setHeader('content-type', 'application/json')
    res.end('{ "errors": [ "flipflop is not a valid crankworble" ] }')
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.fail('send should not succeed')
  }, (err) => {
    server.close()
    t.ok(err)
    t.equal(n, 1, 'It should never retry a bad request')
    t.equal(err.message, 'Invalid payload sent to builds API')
    t.deepEqual(err.errors, [ 'flipflop is not a valid crankworble' ])
    t.end()
  })
})

test('send(): unsuccessful (400, bad json)', t => {
  t.plan(3)
  let n = 0
  const server = http.createServer((req, res) => {
    n++
    res.statusCode = 400
    res.setHeader('content-type', 'application/json')
    res.end('{ "err')
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.fail('send should not succeed')
  }, (err) => {
    server.close()
    t.ok(err)
    t.equal(err.message, 'HTTP status 400 received from builds API')
    t.equal(n, 1)
    t.end()
  })
})

test('send(): retry on timeouts', t => {
  t.plan(3)
  let n = 0
  const server = net.createServer((req, res) => {
    n++
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.fail('send should not succeed')
  }, (err) => {
    server.close()
    t.equal(n, 5, 'should retry after timeouts')
    t.ok(err)
    t.equal(err.message, 'Connection timed out')
    t.end()
  })
})

test('send(): retry on socket hangups', t => {
  t.plan(4)
  let n = 0
  const server = http.createServer((req, res) => {
    n++
    req.connection.end()
  })
  server.listen()
  send(`http://localhost:${server.address().port}`, {}, () => {
    server.close()
    t.fail('send should not succeed')
  }, (err) => {
    server.close()
    t.equal(n, 5, 'should retry after timeouts')
    t.ok(err)
    t.equal(err.code, 'ECONNRESET')
    t.equal(err.message, 'socket hang up')
    t.end()
  })
})
