import random

# TODO: Set count of rows of violations (with the lines at the beginning)
max_rows = 0
# TODO: Set count of violations to be randomly selected
selcet_rows = 0


# ------------------ Do not change anything below this line ------------------
counter = 0

ran_list = []

while counter != selcet_rows:
    ran = random.randint(5, max_rows)

    if not ran in ran_list:
        ran_list.append(ran)
        counter += 1
ran_list.sort()
print(ran_list)
