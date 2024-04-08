def RELEASE_FILE_AVAILABLE = "true"
pipeline {
    agent {
        label 'agent-1'
    }
    stages {
        stage('Stage 1') {
            steps {
                script {
                    echo "${DEPLOYMENT}"
                    echo "${ARTIFACTORY_DOCKERHUB}"
                    echo "${DOCKER_IMAGE}"
                    echo "${DOCKER_TAG}"
                    echo "${REGISTRY}"
                    echo "${REGISTRY_NAMESPACE}"
                }
            }
        }
        
        stage('Build') {
            steps {
                script{
                    echo "Build stage executed"
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
