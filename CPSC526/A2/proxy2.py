import sys, time, string, threading, socket, socketserver

class MyTCPHandler(socketserver.BaseRequestHandler):
    BUFFER_SIZE = 4096
    
    def handle(self):
        logOptions = self.server.logOptions
        logFile = 'temp'
        
        fw = open(logFile, 'w')
        
        # Splits an array into smaller n-length chunks
        def arSplit(l, n):
            n = max(1, n)
            return (l[i:i+n] for i in range(0, len(l), n))
        
        # Saves the specified data to a log and print it out
        def log(string):
            if (logOptions != ''):
                fw.write(string + '\n')
                fw.flush()
            print(string)
            
        # Socket for connecting proxy to remote server
        client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client.connect((self.server.server_name, self.server.dstPort))
        
        log("Port Forwarder/Logger\nSource Port: " + str(self.server.srcPort) + "\nRemote Host: " + self.server.server_name + "\nRemote Port: " + str(self.server.dstPort))
        ServAdd, ServPort = self.server.server_address
        log("New connection: " + time.strftime("%c") + " from "  + ServAdd)
        
        # Main data recieve function and loop
        def recv(inbound, outbound, arrow):
            while True:
                data = inbound.recv(self.BUFFER_SIZE)
                if len(data) == self.BUFFER_SIZE:
                    while 1:
                        try:  # error means no more data
                            data += inbound.recv(self.BUFFER_SIZE, socket.MSG_DONTWAIT)
                        except:
                            break
                if len(data) == 0:
                    break
                
                if (logOptions == '-raw'):
                    log(arrow + data.decode('ascii'))
                elif (logOptions == '-strip'):
                    data_decode = data.decode('utf-8')
                    data_printable = ''
                    for char in data_decode:
                        if not (char in string.printable):
                            data_printable += '.'
                        else:
                            data_printable += char
                    log(arrow +data_printable)
                elif (logOptions == '-hex'):
                    data_decode = data.decode('utf-8')
                    data_decode_split = arSplit(data_decode, 16)
                    for row in data_decode_split:
                        data_hex = ' '.join('{:02x}'.format(ord(c)) for c in row)
                        data_hex += ' |'
                        for char in row:
                            if not (char in string.printable):
                                data_hex += '.'
                            elif (char == '\n'):
                                data_hex += '.'
                            else:
                                data_hex += char
                        data_hex += '|'
                        log(arrow + data_hex)
                elif (logOptions.startswith('-auto')):
                    nVal = int(logOptions[5:])
                    dSplit = arSplit(data, nVal)
                    for vals in dSplit:
                        outstring = ''
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
                        log(arrow + outstring)
                outbound.sendall(data)
                
        # Start separate thread to forward server to user
        threading.Thread(target=recv, args=(client, self.request, '<-- ',)).start()
        
        # Use current thread to forward user to server
        recv(self.request, client, '--> ')
        
if __name__ == "__main__":
    run = True
    if (len(sys.argv) == 4): # No log option
        srcPort = int(sys.argv[1])
        server_name = sys.argv[2]
        dstPort = int(sys.argv[3])
    elif (len(sys.argv) == 5): # Log option
        logOptions = sys.argv[1]
        srcPort = int(sys.argv[2])
        server_name = sys.argv[3]
        dstPort = int(sys.argv[4])
    else:
        print('Usage: [logOptions] <srcPort> <server> <dstPort>')
        run = False
    
    if (run):
        print (time.strftime("%m-%d-%Y--%H-%M-%S")) #Filename format
        HOST, PORT = 'localhost', srcPort
        server = socketserver.ThreadingTCPServer((HOST, PORT), MyTCPHandler)
        if(len(sys.argv) == 5):
            server.logOptions = logOptions
        else:
            server.logOptions = ''
        server.srcPort = srcPort
        server.dstPort = dstPort
        server.server_name = server_name
        server.serve_forever()
