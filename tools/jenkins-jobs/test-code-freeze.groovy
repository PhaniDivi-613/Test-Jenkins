pipeline { 
    agent {
        label 'agent-1'
    }
    environment {
        LOCATION = "${env.DEPLOYMENT.split('_')[1]}"
        ENVIRONMENT = "${env.DEPLOYMENT.split('_')[0]}"
        BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription}"
    }
    stages {
        stage('Check Code Freeze') {
            steps {
                script {
                    sh """
                        echo "BUILD_TRIGGER_BY: ${BUILD_TRIGGER_BY}"
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
