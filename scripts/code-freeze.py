import json
from datetime import datetime
import os

def is_code_freeze(region):
    """
    Checks if the specified region is currently under a code freeze based on the schedule
    defined in the 'codefreeze-timings.json' file.
    """

    with open(os.path.abspath('../codefreeze-timings.json'), 'r') as file:
        code_freeze_schedule = json.load(file)

    current_time = datetime.utcnow()

    for event in code_freeze_schedule:
        if region in event["Regions"]:
            freeze_start = datetime.fromisoformat(event["Freeze Start"][:-1])
            freeze_end = datetime.fromisoformat(event["Freeze End"][:-1])

            if freeze_start <= current_time <= freeze_end:
                return True
    return False

if __name__ == "__main__":
    region = os.getenv("LOCATION")
    if(is_code_freeze(region)):
        print("{} is in code freeze".format(region))
    else:
        print("{} is not in code freeze".format(region))
