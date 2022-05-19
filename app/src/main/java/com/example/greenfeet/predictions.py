from scipy import stats
import csv
import datetime
import matplotlib.pyplot as plt

outfile = open("food.csv", "r")
data = csv.reader(outfile)
data = [row for row in data]
outfile.close()


dates = [datetime.datetime.strptime(e[0], '%Y-%m-%d') for e in data[1:]]
numbers = [eval(e[2]) for e in data[1:]]

x = [(e - min(dates)).days for e in dates] 
gradient, intercept, r_value, p_value, std_err = stats.linregress(x, numbers)


projected_date = min(dates) + datetime.timedelta(days=(600 - intercept) / gradient)


extra_date = min(dates) + datetime.timedelta(days=100)
projection = gradient * (extra_date - min(dates)).days + intercept


plt.figure(1, figsize=(15, 6))
plt.plot_date(dates, numbers, label="Total CO2eq emmitted")
plt.plot_date(dates + [extra_date], numbers + [projection], '-', label="Forecast (%.2f join per day)" % gradient)
plt.legend(loc="upper left")
plt.grid(True)
plt.ylabel('CO2EQ kg')
plt.xlabel('Dates')

plt.title("CO2eq emmitted from diet projection")
plt.savefig('predictions.png')
plt.show()
