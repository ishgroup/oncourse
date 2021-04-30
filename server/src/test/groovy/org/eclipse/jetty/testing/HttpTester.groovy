/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package org.eclipse.jetty.testing

import org.eclipse.jetty.http.*
import org.eclipse.jetty.util.BufferUtil
import org.eclipse.jetty.util.StringUtil

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class HttpTester {
    private HttpTester() {
    }

    static Request newRequest() {
        return new Request()
    }

    static Response parseResponse(ByteBuffer response) {
        Response r = new Response()
        HttpParser parser = new HttpParser(r)
        parser.parseNext(response)
        return r
    }

    abstract static class Message extends HttpFields implements HttpParser.HttpHandler {
        ByteArrayOutputStream _content
        HttpVersion _version = HttpVersion.HTTP_1_0

        HttpVersion getVersion() {
            return _version
        }

        void setVersion(String version) {
            setVersion(HttpVersion.CACHE.get(version))
        }

        void setVersion(HttpVersion version) {
            _version = version
        }

        void setContent(byte[] bytes) {
            try {
                _content = new ByteArrayOutputStream()
                _content.write(bytes)
            }
            catch (IOException e) {
                throw new RuntimeException(e)
            }
        }

        void setContent(String content) {
            try {
                _content = new ByteArrayOutputStream()
                _content.write(StringUtil.getBytes(content))
            }
            catch (IOException e) {
                throw new RuntimeException(e)
            }
        }

        void setContent(ByteBuffer content) {
            try {
                _content = new ByteArrayOutputStream()
                _content.write(BufferUtil.toArray(content))
            }
            catch (IOException e) {
                throw new RuntimeException(e)
            }
        }

        @Override
        void parsedHeader(HttpField field) {
            put(field.getName(), field.getValue())
        }

        @Override
        boolean messageComplete() {
            return true
        }

        @Override
        boolean headerComplete() {
            _content = new ByteArrayOutputStream()
            return false
        }

        @Override
        void earlyEOF() {
        }

        @Override
        boolean content(ByteBuffer ref) {
            try {
                _content.write(BufferUtil.toArray(ref))
            }
            catch (IOException e) {
                throw new RuntimeException(e)
            }
            return false
        }

        @Override
        void badMessage(int status, String reason) {
            throw new RuntimeException(reason)
        }

        ByteBuffer generate() {
            try {
                HttpGenerator generator = new HttpGenerator()
                MetaData info = getInfo()
                // System.err.println(info.getClass());
                // System.err.println(info);

                ByteArrayOutputStream out = new ByteArrayOutputStream()
                ByteBuffer header = null
                ByteBuffer chunk = null
                ByteBuffer content = _content == null ? null : ByteBuffer.wrap(_content.toByteArray())


                loop:
                while (!generator.isEnd()) {
                    HttpGenerator.Result result = info instanceof MetaData.Request
                            ? generator.generateRequest((MetaData.Request) info, header, chunk, content, true)
                            : generator.generateResponse((MetaData.Response) info, header, chunk, content, true)
                    switch (result) {
                        case HttpGenerator.Result.NEED_HEADER:
                            header = BufferUtil.allocate(8192)
                            continue

                        case HttpGenerator.Result.NEED_CHUNK:
                            chunk = BufferUtil.allocate(HttpGenerator.CHUNK_SIZE)
                            continue

                        case HttpGenerator.Result.NEED_INFO:
                            throw new IllegalStateException()

                        case HttpGenerator.Result.FLUSH:
                            if (BufferUtil.hasContent(header)) {
                                out.write(BufferUtil.toArray(header))
                                BufferUtil.clear(header)
                            }
                            if (BufferUtil.hasContent(chunk)) {
                                out.write(BufferUtil.toArray(chunk))
                                BufferUtil.clear(chunk)
                            }
                            if (BufferUtil.hasContent(content)) {
                                out.write(BufferUtil.toArray(content))
                                BufferUtil.clear(content)
                            }
                            break

                        case HttpGenerator.Result.SHUTDOWN_OUT:
                            break loop
                    }
                }

                return ByteBuffer.wrap(out.toByteArray())
            }
            catch (IOException e) {
                throw new RuntimeException(e)
            }

        }

        abstract MetaData getInfo();

        @Override
        int getHeaderCacheSize() {
            return 0
        }

    }

    static class Request extends Message implements HttpParser.RequestHandler {
        private String _method
        private String _uri

        @Override
        boolean startRequest(String method, String uri, HttpVersion version) {
            _method = method
            _uri = uri
            _version = version
            return false
        }

        String getMethod() {
            return _method
        }

        String getUri() {
            return _uri
        }

        void setMethod(String method) {
            _method = method
        }

        void setURI(String uri) {
            _uri = uri
        }

        @Override
        MetaData.Request getInfo() {
            return new MetaData.Request(_method, new HttpURI(_uri), _version, this, _content == null ? 0 : _content.size())
        }

        @Override
        String toString() {
            return String.format("%s %s %s\n%s\n", _method, _uri, _version, super.toString())
        }

        void setHeader(String name, String value) {
            put(name, value)
        }

        @Override
        boolean contentComplete() {
            return false
        }
    }

    static class Response extends Message implements HttpParser.ResponseHandler {
        private int _status
        private String _reason

        @Override
        boolean startResponse(HttpVersion version, int status, String reason) {
            _version = version
            _status = status
            _reason = reason
            return false
        }

        int getStatus() {
            return _status
        }

        String getReason() {
            return _reason
        }

        byte[] getContentBytes() {
            if (_content == null)
                return null
            return _content.toByteArray()
        }

        String getContent() {
            if (_content == null)
                return null
            byte[] bytes = _content.toByteArray()

            String content_type = get(HttpHeader.CONTENT_TYPE)
            String encoding = MimeTypes.getCharsetFromContentType(content_type)
            Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding)

            return new String(bytes, charset)
        }

        @Override
        MetaData.Response getInfo() {
            return new MetaData.Response(_version, _status, _reason, this, _content == null ? -1 : _content.size())
        }

        @Override
        String toString() {
            return String.format("%s %s %s\n%s\n", _version, _status, _reason, super.toString())
        }

        @Override
        boolean contentComplete() {
            return false
        }
    }
}
