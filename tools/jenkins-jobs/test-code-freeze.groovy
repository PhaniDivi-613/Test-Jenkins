pipeline { 
    agent {
        label 'agent-1'
    }
    environment {
        LOCATION = "${env.DEPLOYMENT.split('_')[1]}"
        ENVIRONMENT = "${env.DEPLOYMENT.split('_')[0]}"
        BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription}"
        TRAINID_OVERRIDE = "${TRAINID_OVERRIDE}"
    }
    stages {
        stage('stage 1') {
            when {
                expression { env.TRAINID_OVERRIDE == 'false'}
            }
            steps {
                script {
                    sh """
                        echo "stage 1 running"
                        echo "stage 1 finished"
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
