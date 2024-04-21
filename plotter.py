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


# with open("experiments/list.txt") as f:
#     timestamps = [i.strip() for i in f]

timestamps = ["202404111_070518", "202404112_012337"]

plots = [get_plot_data(i) for i in timestamps]

fig, ax = plt.subplots(nrows=2, ncols=2)

for row_id in range(len(ax)):
    row = ax[row_id]
    for col_id in range(len(row)):
        col = row[col_id]
        data = plots[col_id]
        x = data["TimeElapsed"]
        col.plot(x, data["Average"], "b")
        col.plot(x, data["Min"], "r")
        col.plot(x, data["Max"], "g")


plt.show()
