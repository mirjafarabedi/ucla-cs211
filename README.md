UCLA CS 211 Course Project, Winter 2016
==================================

Topic: Android app for remote configuration of OpenWRT box

Contacts
--------------------
Zhehao Wang <zhehao@remap.ucla.edu>

Haitao Zhang <zhtaoxiang@gmail.com>

Jeffrey Chen <jc4556@g.ucla.edu>

Repository branch and folder structure
--------------------
### withAppAnalysis branch
 - **android-client**    : _The frontend application that configures OpenWRT (initial attempt with WebView, <a href="http://memoria.ndn.ucla.edu/openwrt2.mp4">screen recording with changing SSID and seeing app-specific statistics</a>)_

### Master branch
 - **android-client**    : _The frontend application that configures OpenWRT (migration from WebView to more native Android UI widgets, doesn't have AppFilter frontend yet, <a href="http://memoria.ndn.ucla.edu/openwrt1.mp4">screen recording with login and browsing</a>)_
 - **OpenWRT\_app**      : _The frontend application that only contains the UI framework_
 - **proposal**          : _TeX source for the project opening proposal_
 - **report**            : _Tex source for the project report_

### Appfilter branch
 - **appfilter-server**  : _The Python backend for application specific filtering_

Notes and instructions
--------------------
### Android applications
 * Dependency: Volley library
<pre>
git submodule init
git submodule update
</pre>
 * How to use (shown in corresponding screen recordings):
   * Open project folder in Android Studio, sync gradle, compile, and deploy
   * Login to OpenWRT with IP address, port number, username, and password
   * Browser and configure the OpenWRT box! (Network -> Wifi should be configurable, if the device has a WLAN interface)
 * Built and tested with Android Studio on Mac OSX, please refer to each gradle file for dependencies
 * Applications tested for Android 4.4.4 and 5.1

### OpenWRT setup
 * For OpenWRT running on virtual box, please refer to <a href="https://gist.github.com/zhehaowang/628b26bceb80ed11b6b2">this script</a> (Important: please configure Firewall and port forwarding correctly, for LuCI and AppFilter backend to work).
 * For OpenWRT on a TP-link WDR 4300 router, please flush OpenWRT according to the instructions <a href="https://wiki.openwrt.org/toh/tp-link/tl-wdr4300">here</a>, and set up AppFilter backend by following the steps described below.

### AppFilter backend
 * Dependency: tcpdump, python-light, python-codecs, python-logging and python-openssl packages
<pre>
opkg install tcpdump, python-light, python-codecs, python-logging, python-openssl
</pre>
 * How to use:
  * Install git and other dependencies if not existing, make sure OpenWRT has access to outside network
  * Clone the specific "appfilter" branch
  * Run "tcpdump -i eth3 > /tmp/dump.txt" on OpenWRT (here eth3 is the WAN interface to the outside)
  * Run "python -O appfilter.py"
  * JSON output should be written to "/tmp/output.txt" around every second
  * Generate real traffic (for now, please only use services provided by Google or Facebook)
  * run "python server.py" to serve the results; Symlink output to the directory where the program's running ("ln -s /tmp/output.txt ."), and the JSON output would be available with "GET [OpenWRT IP address]:8000" (if access from a virtual machine, need port forwarding for port 8000 of OpenWRT)
 * Tested on TP-Link WDR 4300 flushed with OpenWRT 15.05, and OpenWRT 15.05 virtual box

