# Legacy-browser
Browser for nokia! Browser in Alpha. Browser by Claude AI and me!
# Nokia 301 browser (thin client + proxy)

## Files
- proxy.py        - server (Termux on the tablet)
- src/Browser.java, src/PageCanvas.java - MIDlet for the phone (MIDP 2.1 / CLDC 1.1)

## 1. Server (Termux)
    cp /sdcard/Download/proxy.py ~/proxy.py     # or wherever you saved it
    python ~/proxy.py
Test on the tablet: http://localhost:8080/m?u=ru.wikipedia.org
You should see plain text lines starting with P / T / L.

## 2. Build the .jar (needs a computer, JDK 8 + Sun WTK 2.5.2 or Java ME SDK)
    mkdir -p build/classes build/pre
    javac -encoding UTF-8 -source 1.3 -target 1.3 -bootclasspath <WTK>/lib/cldcapi11.jar:<WTK>/lib/midpapi21.jar -d build/classes src/*.java
    <WTK>/bin/preverify -classpath <WTK>/lib/cldcapi11.jar:<WTK>/lib/midpapi21.jar -d build/pre build/classes
    printf 'MIDlet-1: Browser,,Browser\nMIDlet-Name: NokiaBrowser\nMIDlet-Vendor: me\nMIDlet-Version: 1.0\nMicroEdition-Configuration: CLDC-1.1\nMicroEdition-Profile: MIDP-2.1\n' > MANIFEST.MF
    jar cfm Browser.jar MANIFEST.MF -C build/pre .

Then copy Browser.jar to the phone (Bluetooth or memory card) and open it.

## 3. On the phone
Menu > Server: enter http://<tablet IP>:8080 (IP is shown when proxy.py starts).
Menu > Go to: address or search words.
Keys: up/down = links and scroll, OK = open link, left/right (or 1/3) = page, Back = history.
