pipeline {
    agent {
        label 'agent-1'
    }
    stages {
        stage('Print options') {
            steps {
                script {
                    sh """
                        echo ${OPTION}
                    """
                }
            }
        }
    }
    post {
        success {
            script { echo "success" }
        }
        aborted {
            script { "aborted" }
        }
        failure {
            script { "failed" }
        }
    }
} 