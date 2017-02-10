import time
import socketserver
import socket, threading, string
import subprocess, sys, os, signal
class MyTCPHandler(socketserver.BaseRequestHandler):
    BUFFER_SIZE = 4096

    def handle(self):
        logOptions = self.server.logOptions

        open("temp", 'w')
        fw = open("temp", 'a')

        client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client.connect((self.server.server_name, self.server.dstPort))

        fw.write("Port logger running: srcPort=" + str(self.server.dstPort) + " host=" + self.server.server_name + "\n")
        ServAdd, ServPort = self.server.server_address
        fw.write("New connection:" + time.strftime("%c") + ",from:"  + ServAdd + "\n")
        #creates thread to recieve
        def recv():
            while True:
                data = client.recv(self.BUFFER_SIZE)
                data_decode = data.decode("utf-8")
                print("from server" + data.decode("utf-8"))
                if(logOptions=="-raw"):
                    print("raw")
                elif(logOptions == "-strip"):
                    data_printable = ""
                    for char in data_decode:
                        if not (char in string.printable):
                            data_printable += "."
                        else:
                            data_printable += char
                    fw.write("<---" +data_printable + '\n')
                    fw.flush()
                    print("strip")
                elif(logOptions == "-hex"):
                    print('derp')
                    data_hex = ":".join("{:02x}".format(ord(c)) for c in data_decode)
                    fw.write("<---" + data_hex + '\n')
                    fw.flush()
                    print(data_hex)
                elif(logOptions.startswith('-auto')):
                    nVal = int(logOptions[5:])
                    def arSplit(l, n):
                        n = max(1, n)
                        return (l[i:i+n] for i in range(0, len(l), n))
                    dSplit = arSplit(data, nVal)
                    for vals in dSplit:
                        outstring = ""
                        counter = 0
                        for byte in vals:
                            if (byte == 92): # Backslash
                                outstring += '\\\\'
                            elif (byte == 9): # Tab
                                outstring += '\\t'
                            elif (byte == 10): # Newline
                                outstring += '\\n'
                            elif (byte == 13): # Carriage return
                                outstring += '\\r'
                            elif (32 <= byte <= 127):
                                outstring += chr(byte)
                            else:
                                outstring += '\\' + str(hex(byte))[2:]
                            counter += 1
                        print(outstring + '\n')
                        fw.write('<---' + outstring + '\n')
                        fw.flush()
                self.request.sendall(data)
        threading.Thread(target=recv).start()

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
            data_decode = data.decode("utf-8")
            # TODO over 9000 if statements
            if(logOptions == "-raw"):
                fw.write("--->" + data_decode.strip() + '\n')
            elif(logOptions == "-strip"):
                data_printable = ""
                for char in data_decode:
                    if not (char in string.printable):
                        data_printable += "."
                    else:
                        data_printable += char
                fw.write("--->" +data_printable)
                fw.flush()
                print("strip")
            elif(logOptions == "-hex"):
                data_hex = ":".join("{:02x}".format(ord(c)) for c in data_decode)
                fw.write("--->" + data_hex + '\n')
                fw.flush()
                print(data_hex)
            """data = data.decode( "utf-8")
            self.request.sendall( bytearray( "You said: " + data, "utf-8"))
            print("%s (%s) wrote: %s" % (self.client_address[0], 
            threading.currentThread().getName(), data.strip()))
            fw.write("--->" + data.strip() + '\n')
            fw.flush()"""
            #client.send(bytearray(data, 'utf-8'))
            #print("from client" + data.decode ("utf-8"))
            client.send(data)



if __name__ == "__main__":
    print (time.strftime("%m-%d-%Y--%H-%M-%S")) #Filename format
    argLen1 = 4
    argLen2 = 5

    #print(len(sys.argv))
    if(len(sys.argv) == argLen1): #no log option
        srcPort = int (sys.argv[argLen1-3])
        server_name = sys.argv[argLen1-2]
        dstPort = int (sys.argv[argLen1-1])
    elif(len(sys.argv) == 5): #with log option  
        logOptions = sys.argv[argLen2-4]         
        srcPort = int (sys.argv[argLen2-3])
        server_name = sys.argv[argLen2-2]
        dstPort = int (sys.argv[argLen2-1])
    else:
        print("Usage: [logOptions] <srcPort> <server> <dstPort>")
    #print(str(srcPort) + " " + server_name + " " + str(dstPort))
    HOST, PORT = "localhost", srcPort
    server = socketserver.ThreadingTCPServer((HOST, PORT), MyTCPHandler)
    if(len(sys.argv) == 5):
        server.logOptions = logOptions
    else:
        server.logOptions = ""
    server.dstPort = dstPort
    server.server_name = server_name
    server.serve_forever()
