import time
import subprocess
import select
import json

# For ip address parsing
import socket

class AppFilterServer(object):
  def __init__(self):
    self._dnsMapping = dict()
    self._providerMapping = dict()
    self._dnsReplyCharacteristicString = "name = "
    self._dnsNoReplyString = "none"

    self._trafficDict = dict()
    self._applicationMapping = {
      "fbcdn": "facebook", 
      "google": "google", 
      "facebook": "facebook",
      "1e100": "google"
    }

    # Time threshold in milliseconds for writing to file
    self._fileWriteThreshold = 1000
    self._lastPacketTime = 0
    return

  def extractHeader(self, line):
    components = line.split()
    try:
      return {"pktCnt": components[0], "timestamp": components[1], 
              "srcAddr": components[2], "dstAddr": components[4],
              "protocol": components[5], "length": components[6],
              "payload": (" ").join(components[6:])}
    except:
      return None

  def getApplication(self, domain):
    for key, value in self._applicationMapping.iteritems():
      if key in domain:
        return value
    return

  def getStat(self, line):
    components = self.extractHeader(line)
    currentTime = int(time.time() * 1000)
    if currentTime - self._lastPacketTime > self._fileWriteThreshold:
      self.writeStat("output.txt")

    if not components:
      return

    srcAddr = components["srcAddr"]
    dstAddr = components["dstAddr"]
    length = int(components["length"])
    try:
      socket.inet_aton(dstAddr)
    except socket.error:
      print "received domain name in dstAddr field"

    if dstAddr not in self._dnsMapping:
      output = subprocess.Popen(['nslookup', dstAddr], stdout=subprocess.PIPE, stderr=subprocess.STDOUT).stdout.read()
      if self._dnsReplyCharacteristicString in output:
        idx = output.find(self._dnsReplyCharacteristicString)
        nextLineIdx = output.find("\n", idx)
        self._dnsMapping[dstAddr] = output[idx + len(self._dnsReplyCharacteristicString):nextLineIdx]
      else:
        # use self._dnsNoReplyString so that query for same address would not be sent again
        self._dnsMapping[dstAddr] = self._dnsNoReplyString

    # After the DNS reverse query, if we have this entry, we'll add it up to our statistics
    # This is expected to be synchronous
    if dstAddr in self._dnsMapping and self._dnsMapping[dstAddr] != self._dnsNoReplyString:
      application = self.getApplication(self._dnsMapping[dstAddr])
      if application:
        if application in self._trafficDict:
          if srcAddr in self._trafficDict[application]:
            self._trafficDict[application][srcAddr] += length
          else:
            self._trafficDict[application][srcAddr] = length
        else:
          self._trafficDict[application] = dict()
          self._trafficDict[application][srcAddr] = length
    else:
      pass
      #print "do not have dns mapping for dst address: " + dstAddr

    self._lastPacketTime = currentTime
    return

  def writeStat(self, outputFile):
    f = open(outputFile, 'w')
    f.write(json.dumps(self._trafficDict))
    f.close()

  def followFile(self, filename):
    f = subprocess.Popen(['tail', '-F', filename],\
          stdout = subprocess.PIPE,stderr = subprocess.PIPE)
    # This is not supported by Mac OSX's default Python
    p = select.poll()
    p.register(f.stdout)

    while True:
      # This is not supported by Mac OSX's default Python
      if p.poll(1):
        self.getStat(f.stdout.readline())
      time.sleep(0.01)

  def debug(self):
    for application in self._trafficDict:
      for srcAddr in self._trafficDict[application]:
        print application + ":" + srcAddr + ":" + str(self._trafficDict[application][srcAddr])

  def readFile(self, filename):
    f = open(filename, 'r')
    for line in f:
      self.getStat(line)
    f.close()

if __name__ == "__main__":
  appFilterServer = AppFilterServer()

  # debug code
  appFilterServer.readFile("log.txt")
  appFilterServer.debug()
  while True:
    appFilterServer.getStat("")
    time.sleep(1)

  # production code
  #appFilterServer.followFile()
