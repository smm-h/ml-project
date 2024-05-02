import matplotlib.pyplot as plt
from math import sqrt, ceil

path = "../../../experiments/"


def get_plot_data(timestamp):
    filename = path + timestamp + "/plot.csv"

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


with open(path + "index.txt") as f:
    all_timestamps = [i.strip() for i in f]

last_count = 1

selected_timestamps = all_timestamps[-5:-4]  # [-last_count:None]

print(*selected_timestamps, sep="\n")

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
        x = data["Generation"]
        part.plot(x, data["Max"], "g")
        part.plot(x, data["Min"], "r")
        part.plot(x, data["Average"], "b")
    except FileNotFoundError:
        pass

plt.show()
