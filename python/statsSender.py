import datetime
import paho.mqtt.client as paho
import socket
import psutil
import time

# pip install paho-mqtt

# On MacOS: first install Xcode and agree with the Xcode license agreement
# If you get a lot of errors on MacOS, first run 'export ARCHFLAGS="-arch x86_64"'
# pip install psutil

mosquitto = "192.168.0.223"
topicName = "topic/statsCollector"

def getStateMessage(hostname, ipAddress):
    swap = psutil.swap_memory()
    virtual = psutil.virtual_memory()
    network = psutil.net_io_counters()

    json = "{"
    json += " \"hostname\":\"" + hostname + "\","
    json += " \"ipAddress\":\"" + ipAddress + "\","
    json += " \"cpu\":\"" + str(psutil.cpu_percent()) + "\","
    json += " \"swap_memory\": {"
    json += "   \"bytes_sent\":\"" + str(swap.total) + "\","
    json += "   \"bytes_received\":\"" + str(swap.used) + "\","
    json += "   \"packets_sent\":\"" + str(swap.free) + "\","
    json += "   \"packets_received\":\"" + str(swap.percent) + "\""
    json += " },"
    json += " \"virtual_memory\": {"
    json += "   \"total\":\"" + str(virtual.total) + "\","
    json += "   \"available\":\"" + str(virtual.available) + "\","
    json += "   \"used\":\"" + str(virtual.used) + "\","
    json += "   \"free\":\"" + str(virtual.free) + "\","
    json += "   \"percent\":\"" + str(virtual.percent) + "\""
    json += " },"
    json += " \"network\": {"
    json += "   \"bytes_sent\":\"" + str(network.bytes_sent) + "\","
    json += "   \"bytes_received\":\"" + str(network.bytes_recv) + "\","
    json += "   \"packets_sent\":\"" + str(network.packets_sent) + "\","
    json += "   \"packets_received\":\"" + str(network.packets_recv) + "\""
    json += " }"
    json += "}"
    print(json)
    return json

def sendData(client, hostname, ipAddress):
    client.publish(topicName, getStateMessage(hostname, ipAddress))

def main():
    print(getStateMessage("1", "2"))

    # Network info of this device
    hostname = socket.gethostname()
    print("Hostname: ", hostname)

    try:
        address = socket.gethostbyname(hostname + ".local")
    except:
        address = ""
    print("IP address: ", address)

    print("Starting connection to: ", mosquitto)
    try:
        client = paho.Client(hostname + ":" + str(address))
        client.connect(mosquitto)
        while True:
            sendData(client, hostname, address)
            time.sleep(1)
    except Exception as ex:
        print("Could not connect to Mosquitto: " + str(ex))

if __name__ == "__main__":
    main()
