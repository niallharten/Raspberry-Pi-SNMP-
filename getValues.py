from sense_hat import SenseHat
from time import sleep
from time import asctime

sense = SenseHat()

temp = round(sense.get_temperature())
humidity = round(sense.get_humidity())
pressure = round(sense.get_pressure())


print(temp,humidity,pressure)
print(temp)
print(humidity)
print(pressure)
