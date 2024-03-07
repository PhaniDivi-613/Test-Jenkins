def RELEASE_FILE_AVAILABLE = "true"
pipeline {
    agent {
        label 'agent-1'
    }
    stages {
        stage('Set Skip Parameter') {
            steps {
                script {
                    def currentDate = new Date()
                    def calendar = Calendar.getInstance()
                    calendar.setTime(currentDate)
                    def dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    
                    // Set parameter to skip successive stages based on odd/even day
                    RELEASE_FILE_AVAILABLE = (dayOfMonth % 2 == 0) ? "true" : "false"
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
