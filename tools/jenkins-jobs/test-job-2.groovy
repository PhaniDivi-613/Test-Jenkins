pipeline {
    agent {
        label 'agent-1'
    }
    environment{
        SKIP = true
    }
    stages {
        stage('Set Skip Parameter') {
            steps {
                script {
                    def currentDate = new Date()
                    def calendar = Calendar.getInstance()
                    calendar.setTime(currentDate)
                    def dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    
                    
                    // Set parameter to skip successive stages
                    env.SKIP = (dayOfMonth % 2 == 1) ? true : false
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
                expression { env.SKIP != true }
            }
            steps {
                script{
                    echo "Test stage executed"
                }
                
                // Your build steps here
            }
        }
        
        stage('Deploy') {
            when {
                expression { env.SKIP != true }
            }
            steps {
                script{
                    echo "Deploy stage executed"
                }
                
                // Your build steps here
            }
        }
        
        // Add more stages here if needed
    }
}
