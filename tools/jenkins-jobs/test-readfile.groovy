pipeline {
    agent {
        label 'agent-1'
    }
    stages {
        stage('stage 1') {
            steps {
                script{
                    sh """
                        echo ${PARAM}
                        exit 0
                    """
                }
                
            }
        }
        stage('stage 2') {
            steps {
                script{
                    sh """
                        exit 0
                    """
                }
                
            }
        }  
    }
    post {
        success {
            script {
                environment {
                Job2 = "../folder-1/Testing Job 2"
            }
            steps {
                build job: Job2, parameters: [
                    [$class: 'StringParameterValue', name: 'PARAM', value: "True"]
                ]
            }
                
            }
        }
    }
}
