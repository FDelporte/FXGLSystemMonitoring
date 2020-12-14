# Device monitoring tool based on JavaFX and FXGL

In a previous post
["Getting Started with FXGL Game Development"](https://foojay.io/today/category/java/javafx/)
we already have taken a look at the [FXGL game development framework](https://github.com/AlmasB/FXGL)
developed by [Almas Baimagambetov](https://twitter.com/AlmasBaim).

But a gaming engine can also be used for other applications. In this post we will be using it for a system monitoring
tool which can run on a Raspberry Pi to monitor any device which can report its state to a queue.

## Diagram

For this post a proof-of-concept has been set up using a Raspberry Pi as the "central system" to host the queue (
Mosquitto) and run the JavaFX dashboard.

![Diagram test setup](images/diagram.png)

### Installing Mosquitto on the Raspberry Pi

```
$ sudo apt update
$ sudo apt install -y mosquitto mosquitto-clients
$ sudo systemctl enable mosquitto.service
```

Now we can check if it is installed correctly and running:

```
$ mosquitto -v
1569780732: mosquitto version 1.5.7 starting
1569780732: Using default config.
1569780732: Opening ipv4 listen socket on port 1883.
1569780732: Error: Address already in use
```

### Testing Mosquitto on the Pi

We can easily test if Mosquitto is running OK on the Pi, by opening two terminal windows. In the first one we start a
listener on topic "testing/TestTopic":

```
$ mosquitto_sub -v -t 'testing/TestTopic'
```

In the second terminal we send multiple commands with a message for this topic, like this:

```
$ mosquitto_pub -t 'testing/TestTopic' -m 'hello world'
$ mosquitto_pub -t 'testing/TestTopic' -m 'hello world'
$ mosquitto_pub -t 'testing/TestTopic' -m 'jieha it works'
```

Every "publish" from the second terminal window will appear in the first one.

{width: 80%}
![Testing Mosquitto on the Pi](images/mosquitto_testing.png)

## Send state application

To send the state from another Raspberry Pi to Mosquitto, a separate app is available on GitHub.

// TODO

## Monitoring application

The [sources of this application are available on GitHub](https://github.com/FDelporte/FXGLSystemMonitoring).

## Conclusion