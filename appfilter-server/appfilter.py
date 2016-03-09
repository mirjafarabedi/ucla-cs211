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
      "1e100": "google",
      "akamai": "akamai"
    }

    # Time threshold in milliseconds for writing statistics to a file
    self._fileWriteThreshold = 1000
    self._lastWriteTime = 0
    self._outputFile = "/tmp/output.txt"
    return

  def extractHeaderRead(self, line):
    components = line.split()
    try:
      return {"pktCnt": components[0], "timestamp": components[1], 
              "srcAddr": components[2], "dstAddr": components[4],
              "protocol": components[5], "length": int(components[6]),
              "payload": (" ").join(components[6:]), "srcIsAddr": True, "dstIsAddr": True}
    except:
      return None

  def isAddrIP(self, addr):
    addrs = addr.split(".")
    isAddr = True
    if len(srcAddrs) == 5:
      try:
        socket.inet_aton((".").join(addrs.split(".")[:-1]))
      except socket.error:
        isAddr = False
    elif len(srcAddrs) == 4:
      try:
        socket.inet_aton((".").join(addr)
      except socket.error:
        isAddr = False
    else:
      isAddr = False
    return {"isAddr": isAddr, "addr": addrs.split(".")[:-1]}

  def extractHeaderFollow(self, line):
    components = line.split()
    try:
      srcIsAddr = self.isAddrIP(components[2])
      dstIsAddr = self.isAddrIP(components[4])

      return {"timestamp": components[0],
              "srcAddr": srcIsAddr["addr"], 
              # even in the case of dst being a domain name, having this split doesn't matter as .com gets removed
              #"dstAddr": (".").join(components[4].split(".")[:-1]),
              "dstAddr": dstIsAddr["addr"],
              "protocol": components[1],
              "length": int(components[-1]),
              "srcIsAddr": srcIsAddr["isAddr"],
              "dstIsAddr": dstIsAddr["isAddr"]}
    except:
      return None

  def getApplication(self, domain):
    for key, value in self._applicationMapping.iteritems():
      if key in domain:
        return value
    return

  def getStat(self, line, extractHeader):
    components = extractHeader(line)
    currentTime = int(time.time() * 1000)
    if currentTime - self._lastWriteTime > self._fileWriteThreshold:
      print "Dumping output file"
      self.writeStat(self._outputFile)
      self._lastWriteTime = currentTime

    if not components:
      return

    srcAddr = components["srcAddr"]
    dstAddr = components["dstAddr"]
    length = components["length"]
    srcIsAddr = components["srcIsAddr"]
    dstIsAddr = components["dstIsAddr"]
    
    if dstIsAddr:
      # This filters only outgoing traffic
      if dstAddr not in self._dnsMapping:
        output = subprocess.Popen(['nslookup', dstAddr], stdout = subprocess.PIPE, stderr = subprocess.STDOUT).stdout.read()
        if self._dnsReplyCharacteristicString in output:
          # This characteristic string based approach works for my NsLookup on OSX, but not on OpenWRT yet
          idx = output.find(self._dnsReplyCharacteristicString)
          nextLineIdx = output.find("\n", idx)
          self._dnsMapping[dstAddr] = output[idx + len(self._dnsReplyCharacteristicString):nextLineIdx]
        else:
          # use self._dnsNoReplyString so that query for same address would not be sent again
          self._dnsMapping[dstAddr] = self._dnsNoReplyString

      # After the DNS reverse query, if we have this entry, we'll add it up to our statistics
      # This is expected to be synchronous
      if dstAddr in self._dnsMapping:
        dstDomain = self._dnsMapping[dstAddr]
      else:
        # DNS reverse query did not bring names back, we ignore this packet; 
        # this should not happen for now, we leave a "none" string so that this address is not tried later
        pass
    else:
      dstDomain = dstAddr

    if dstDomain != self._dnsNoReplyString:
      application = self.getApplication(dstDomain)
      if application:
        print "Adding length " + str(length) + " to " + application + " : " + srcAddr + "; because of " + dstDomain
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
        self.getStat(f.stdout.readline(), self.extractHeaderFollow)
      time.sleep(0.01)

  def debug(self):
    for application in self._trafficDict:
      for srcAddr in self._trafficDict[application]:
        print application + ":" + srcAddr + ":" + str(self._trafficDict[application][srcAddr])

  def readFile(self, filename):
    f = open(filename, 'r')
    for line in f:
      self.getStat(line, self.extractHeaderRead)
    f.close()

if __name__ == "__main__":
  appFilterServer = AppFilterServer()

  # debug code
  if __debug__:
    appFilterServer.readFile("/tmp/log.txt")
    appFilterServer.debug()
    while True:
      appFilterServer.getStat("", appFilterServer.extractHeaderRead)
      time.sleep(1)
  else:
    # production code
    appFilterServer.followFile("/tmp/dump.txt")
