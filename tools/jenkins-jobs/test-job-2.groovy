pipeline {
    agent {
        label 'agent-1'
    }

    stages {
        stage('Stage 1') {
            steps {
                echo "Stage 1 Executed"
            }
        }
        stage('Stage 2') {
            steps {
                script {
                    def currentDate = new Date()
                    def calendar = Calendar.getInstance()
                    calendar.setTime(currentDate)
                    def dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    
                    if (dayOfMonth % 2 != 0) {
                        echo "Current date is odd. Skipping subsequent stages."
                        currentBuild.result = 'ABORTED'
                    } else {
                        echo "Current date is even. Continuing with subsequent stages."
                    }
                }
            }
        }
        stage('Stage 3') {
            when {
                expression { currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo "Stage 3 Executed"
            }
        }
        stage('Stage 4') {
            when {
                expression { currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo "Stage 4 Executed"
            }
        }
        stage('Stage 5') {
            when {
                expression { currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo "Stage 5 Executed"
            }
        }
    }
}

