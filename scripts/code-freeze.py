import json
from datetime import datetime
import os
import subprocess
import sys

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

            print(freeze_start)
            print(freeze_end)
            print(current_time)
            print(event["Regions"])
            print()
            if freeze_start <= current_time <= freeze_end:
                return True
    return False

if __name__ == "__main__":
    region = os.getenv("LOCATION")

    code_freeze_active = is_code_freeze(region)
    build_trigger_by = os.getenv('BUILD_TRIGGER_BY')
    if build_trigger_by and 'timer' in build_trigger_by.lower():
        cron_trigger = True
    elif build_trigger_by and 'user' in build_trigger_by.lower():
        cron_trigger = False
    else:
        sys.exit(7)
    
    if (code_freeze_active and cron_trigger):
        print("Code freeze is active for {} and job triggered by cron. Halting the job.".format(region))
        sys.exit(6)
    elif(code_freeze_active and not cron_trigger):
        user_input = input("Code freeze is active for {}. Do you want to proceed? (Yes/No): ".format(region))
        if(user_input.lower() == 'no'):
            print("Opted not to proceed during code freeze. Halting the job.")
            sys.exit(6)
        else:
            print("Continuing the job despite code freeze.")
    else:
        print("No code freeze active for {}. Continuing the job".format(region))

