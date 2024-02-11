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

                    sh """
                        echo "BUILD_USER: ${BUILD_USER}"
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
