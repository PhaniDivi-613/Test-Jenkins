pipeline { 
    agent {
        label 'agent-1'
    }
    environment {
        REGION = "${env.DEPLOYMENT.split('_')[1]}"
    }
    stages {
        stage('Check Code Freeze') {
            steps {
                script {
                    if(currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)) {
                        echo "Triggered by a user"
                    } else if(currentBuild.rawBuild.getCause(hudson.triggers.TimerTrigger$TimerTriggerCause)) {
                        echo "Triggered by a timer/cron"
                    }
                    def inCodeFreeze = isInCodeFreeze(env.REGION)
                    if (inCodeFreeze) {
                        error "Code freeze is active for ${env.REGION}. Halting the job."
                    } else {
                        echo "No code freeze active for ${env.REGION}. Continuing the job."
                    }
                }
            }
        }
        stage('List All Files') {
            steps {
                script {
                    sh 'cd . && ls -la /'
                    sh 'find . -name "codefreeze-timings.json"'
                }
            }
        }
        stage('Simple Stage') {
            steps {
                script {
                    sh 'echo "phani"'
                }
            }
        }
    }
}
