import SimpleHTTPServer
import SocketServer

PORT = 8000

Handler = SimpleHTTPServer.SimpleHTTPRequestHandler

httpd = SocketServer.TCPServer(("", PORT), Handler)

# Expected get request:
# http://localhost:8000/output.txt

print "serving at port", PORT
httpd.serve_forever()