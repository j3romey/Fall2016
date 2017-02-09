import socketserver, time
import socket, threading
class MyTCPHandler(socketserver.BaseRequestHandler):
   BUFFER_SIZE = 4096
   def handle(self):
       
        
       while 1:
           data = self.request.recv(self.BUFFER_SIZE)
           if len(data) == self.BUFFER_SIZE:
               while 1:
                   try:  # error means no more data
                       data += self.request.recv(self.BUFFER_SIZE, socket.MSG_DONTWAIT)
                   except:
                       break
           if len(data) == 0:
               break
           data = data.decode( "utf-8")
           x = 0
           while(x != 5):
               time.sleep(1)
               self.request.sendall( bytearray( "You said: " + data, "utf-8"))
               x += 1
           #print("%s (%s) wrote: %s" % (self.client_address[0], 
           #      threading.currentThread().getName(), data.strip()))

if __name__ == "__main__":
   HOST, PORT = "localhost", 4444
   server = socketserver.ThreadingTCPServer((HOST, PORT), MyTCPHandler)
   server.serve_forever()
