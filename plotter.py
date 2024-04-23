from math import sqrt, ceil
import matplotlib.pyplot as plt


def get_plot_data(timestamp):

    filename = "experiments/"+timestamp+"/plot.csv"

    plot_data = {
        "Generation": [],
        "TimeElapsed": [],
        "Average": [],
        "Min": [],
        "Max": [],
    }

    with open(filename) as f:

        f.readline()  # skip header

        for line in f:
            Generation, TimeElapsed, Average, Min, Max = line.split(",")
            plot_data["Generation"].append(int(Generation))
            plot_data["TimeElapsed"].append(float(TimeElapsed))
            plot_data["Average"].append(float(Average))
            plot_data["Min"].append(float(Min))
            plot_data["Max"].append(float(Max))

    return plot_data


with open("experiments/list.txt") as f:
    all_timestamps = [i.strip() for i in f]

# ["202404111_070518", "202404112_012337"]
selected_timestamps = [all_timestamps[-1]]  # [-1-(3)**2:-1]

n = len(selected_timestamps)

side = ceil(sqrt(n))

fig, ax = plt.subplots(nrows=side, ncols=side)

for i in range(n):
    t = selected_timestamps[i]
    part = ax
    if "__getitem__" in dir(part):
        part = part[i // side]
        if "__getitem__" in dir(part):
            part = part[i % side]
    try:
        data = get_plot_data(t)
        x = data["TimeElapsed"]
        part.plot(x, data["Average"], "b")
        part.plot(x, data["Min"], "r")
        part.plot(x, data["Max"], "g")
    except FileNotFoundError:
        pass

plt.show()
