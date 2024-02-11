import json
from datetime import datetime
import os
import subprocess

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
        groovy_script = """
        def userInput = input(
           id: 'codeFreezeConfirmation',
           message: "Code freeze is active for {}. Do you want to proceed?",
           parameters: [choice(choices: ['Yes', 'No'], description: 'Choose whether to proceed', name: 'Confirmation')]
        )
        """.format(region)
        subprocess.run(['groovy', '-e', groovy_script])
    else:
        groovy_script = """
        def userInput = input(
           id: 'codeFreezeConfirmation',
           message: "Code freeze is not active for {}. Do you want to proceed?",
           parameters: [choice(choices: ['Yes', 'No'], description: 'Choose whether to proceed', name: 'Confirmation')]
        )
        """.format(region)
        subprocess.run(['groovy', '-e', groovy_script])
        print("{} is not in code freeze".format(region))
