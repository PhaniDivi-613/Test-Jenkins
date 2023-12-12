pipeline {
    agent {
        label 'agent-1'
    }
    stages {
        stage('Stage 1') {
            steps {
                script{
                    sh """
                        exit 0
                    """
                }
                
            }
        }
    }
}
