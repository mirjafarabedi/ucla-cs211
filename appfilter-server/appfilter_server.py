import time
import subprocess
import select

class AppFilterServer(object):
  def __init__(self):
    return

  def getStat(self, line):
    print line
    return

  def followFile(self, filename):
    f = subprocess.Popen(['tail','-F', filename],\
          stdout = subprocess.PIPE,stderr = subprocess.PIPE)
    p = select.poll()
    p.register(f.stdout)

    while True:
        if p.poll(1):
            self.getStat(f.stdout.readline())
        time.sleep(0.01)

if __name__ == "__main__":
  appFilterServer = AppFilterServer()
  appFilterServer.followFile("log.txt")