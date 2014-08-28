<h2>Datalogger</h2>
Webapp for automated sensor reading using a Raspberry Pi. <b>Datalogger</b> captures and stores sensor data, presenting this data using either a graph or table view.

<h3>Installation</h3>
<h5>Configure I2C</h5>
<b>Datalogger</b> uses the [BMP180/BMP085](http://www.adafruit.com/products/391) ([datasheet](http://www.adafruit.com/datasheets/BMP085_DataSheet_Rev.1.0_01July2008.pdf)) sensor  connected to the I2C of the Raspberry Pi. Follow the steps below to configure your Raspberry Pi to use the I2C interface:

1. [Configuring the Pi for I2C](https://learn.adafruit.com/using-the-bmp085-with-raspberry-pi/configuring-the-pi-for-i2c)
2. [Configuring I2C](https://learn.adafruit.com/adafruits-raspberry-pi-lesson-4-gpio-setup/configuring-i2c)

<h5>Install software</h5>
1. Install Java (`sudo apt-get install oracle-java7-jdk`)
2. Download and install Tomcat (`sudo apt-get install tomcat7`)
3. The Pi4J library will need root privileges in order to control the GPIO pins, so Tomcat must run under the user root and under the group root. On the Raspberry Pi verify your `/etc/default/tomcat7` configuration file. Find  the `TOMCAT7_USER`   and `TOMCAT7_GROUP` variables and make sure they are both set to root:
	* `TOMCAT7_USER=root`
	* `TOMCAT7_GROUP=root`
4. Restart Tomcat: `sudo service tomcat7 restart`

<h5>Deploy</h5>
Deploy <b>Datalogger</b> by either copying the <i>datalogger.war</i> file to the webapps folder of Tomcat (`/var/lib/tomcat7/webapps`) onto your Raspberry Pi or, if configured, use the Tomcat manager (`http://raspberrypi:8080/manager/`) to deploy the <i>datalogger.war</i> file.
