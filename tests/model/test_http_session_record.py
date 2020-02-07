import unittest

from model.http_session_record import HttpRequest, HttpResponse


class TestHttpRequest(unittest.TestCase):
    def test_parse_valid_message(self):
        data = b'''POST /test.php?a=b+c HTTP/1.2\r
User-Agent: Fooo\r
Host: bar\r
Transfer-Encoding: chunked\r
\r
5\r\nhello\r\n6\r\n world\r
'''
        request = HttpRequest([data])
        success = request.parse()
        self.assertTrue(success)
        self.assertTrue(request.is_invalid)
        self.assertEqual(request.method, 'POST')
        self.assertEqual(request.version, '1.2')
        self.assertEqual(request.url, '/test.php?a=b+c')
        self.assertEqual(request.headers['User-Agent'], 'Fooo')
        self.assertEqual(request.headers['Host'], 'bar')
        self.assertEqual(request.headers['Transfer-Encoding'], 'chunked')

    def test_parse_bad_message(self):
        data = b'''POST /test.php?a=b+c HTTP/1.2\rINVALID
User-Agent: Fooo\r
Host: bar\r
Transfer-Encoding: chunked\r
\r
5\r\nhello\r\n6\r\n world\r
'''
        request = HttpRequest([data])
        success = request.parse()
        self.assertFalse(success)
        self.assertFalse(request.is_invalid)
        self.assertEqual(request.method, '')
        self.assertEqual(request.version, '')
        self.assertEqual(request.url, '/test.php?a=b+c')
        self.assertEqual(request.headers, {})


class TestHttpResponse(unittest.TestCase):
    def test_parse_valid_message(self):
        data = b'''HTTP/1.1 200 OK\r
Date: Mon, 23 May 2005 22:38:34 GMT\r
Server: Apache/1.3.3.7\r
        (Unix) (Red-Hat/Linux)\r
Content-Type: text/html;\r
  charset=UTF-8\r
Content-Length: 130\r
\r
'''
        response = HttpResponse([data])
        success = response.parse()
        self.assertTrue(success)
        self.assertTrue(response.is_invalid)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.version, '1.1')
        headers = response.headers
        self.assertEqual(headers['Date'], 'Mon, 23 May 2005 22:38:34 GMT')
        self.assertEqual(headers['Server'], 'Apache/1.3.3.7        (Unix) (Red-Hat/Linux)')
        self.assertEqual(headers['Content-Type'], 'text/html;  charset=UTF-8')
        self.assertEqual(headers['Content-Length'], '130')

    def test_parse_bad_message(self):
        data = b'''BAD HTTP/1.1 200 OK\r
Date: Mon, 23 May 2005 22:38:34 GMT\r
Server: Apache/1.3.3.7\r
        (Unix) (Red-Hat/Linux)\r
Content-Type: text/html;\r
  charset=UTF-8\r
Content-Length: 130\r
\r
'''
        response = HttpResponse([data])
        success = response.parse()
        self.assertFalse(success)
        self.assertFalse(response.is_invalid)
        self.assertEqual(response.status_code, 0)
        self.assertEqual(response.version, '')
        self.assertEqual(response.headers, {})
