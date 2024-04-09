def RELEASE_FILE_AVAILABLE = "true"
trainOverride = (TRAIN_ID.length() > 0)
pipeline {
    agent {
        label 'agent-1'
    }
    environment {
        TRAIN_ID = "${TRAIN_ID}"
    }
    stages {
        stage('Stage 1') {
            when {
                allOf {
                    expression { trainOverride != false && env.TRAIN_ID == ''}
                }
            }
            steps {
                script {
                    echo "Stage 1 Executed"
                }
            }
        }
        
        stage('Build') {
            steps {
                script{
                    echo "${TRAIN_ID}"
                }
                
                // Your build steps here
            }
        }
        
        stage('Test') {
            when {
                expression { RELEASE_FILE_AVAILABLE == 'true' }
            }
            steps {
                script{
                    println "Test stage executed"
                }
                
                // Your build steps here
            }
        }
        
        stage('Deploy') {
            when {
                expression { RELEASE_FILE_AVAILABLE == 'true' }
            }
            steps {
                script{
                    println "Deploy stage executed"
                }
                
                // Your build steps here
            }
        }
        
        // Add more stages here if needed
    }
}
