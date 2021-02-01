# Raspberry-Pi-SNMP-

This project uses SNMP to recieve metrics from a raspberry pi along with a sense hat. 

You would use a Raspberry Pi with a sense hat attached to it. The sense hat will take in the tempertaure, humidity and pressure. The application uses a python script which is in the repository. You will leave the script on the Pi itself. The main application is wrote in Java. It has a GUI which will pop up and you can work it from there. This application  uses SNMP. The manager will run the java application and the agent (Raspberry pi) will gather the metrics. The java code will execute the python script and return back the metrics to the manager. From here the manager will export the metrics to a CSV file and a txt file. Here you can easily view the metrics.

Note: you will have to change the IP address of the agent to make it work with your certain Pi.
