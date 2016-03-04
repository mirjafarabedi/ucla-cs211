import time
import subprocess
import select

try:
    import asyncio
except ImportError:
    import trollius as asyncio

class AppFilterServer(object):
  def __init__(self):
    return

  def getStat(self, line):
    print line
    return

  @asyncio.coroutine
  def followFile(self, filename):
    f = subprocess.Popen(['tail','-F', filename],\
          stdout = subprocess.PIPE,stderr = subprocess.PIPE)
    p = select.poll()
    p.register(f.stdout)

    while True:
        if p.poll(1):
            self.getStat(f.stdout.readline())
        time.sleep(0.01)
        yield None

if __name__ == "__main__":
  appFilterServer = AppFilterServer()
  loop = asyncio.get_event_loop()
  loop.run_until_complete(appFilterServer.followFile("log.txt"))