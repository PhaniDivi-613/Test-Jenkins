pipeline { 
    agent {
        label 'agent-1'
    }
    environment {
        LOCATION = "${env.DEPLOYMENT.split('_')[1]}"
        ENVIRONMENT = "${env.DEPLOYMENT.split('_')[0]}"
    }
    stages {
        stage('Check Code Freeze') {
            steps {
                script {
                    // def isCronTrigger = currentBuild.rawBuild.getCause(hudson.triggers.TimerTrigger$TimerTriggerCause) != null
                    // def codeFreezeActive = isInCodeFreeze(env.LOCATION)

                    // if (codeFreezeActive && isCronTrigger) {
                    //     error "Code freeze is active for ${env.LOCATION} and job triggered by cron. Halting the job."
                    // } else if (codeFreezeActive && !isCronTrigger) {
                    //     def userInput = input(
                    //         id: 'codeFreezeConfirmation',
                    //         message: "Code freeze is active for ${env.LOCATION}. Do you want to proceed?",
                    //         parameters: [choice(choices: ['Yes', 'No'], description: 'Choose whether to proceed', name: 'Confirmation')]
                    //     )
                        
                    //     if (userInput == 'No') {
                    //         error "User opted not to proceed during code freeze. Halting the job."
                    //     } else {
                    //         echo "Continuing the job despite code freeze."
                    //     }
                    // } else {
                    //     echo "No code freeze active for ${env.LOCATION}. Continuing the job."
                    // }

                    sh """

                        echo "BUILD_CAUSE_USERIDCAUSE: ${BUILD_CAUSE_USERIDCAUSE}"
                        echo "BUILD_CAUSE_SCMTRIGGER: ${BUILD_CAUSE_SCMTRIGGER}"
                        echo "BUILD_CAUSE_UPSTREAMTRIGGER: ${BUILD_CAUSE_UPSTREAMTRIGGER}"
                        echo "BUILD_CAUSE_MANUALTRIGGER: ${BUILD_CAUSE_MANUALTRIGGER}"

                        cd scripts
                        python3 code-freeze.py
                    """
                }
            }
        }       
        stage('Stage 2') {
            steps {
                script {
                    sh 'echo "Stage 2 executed"'
                }
            }
        }
    }
}
