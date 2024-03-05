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
                    
                    // Check if day is odd
                    def skip = (dayOfMonth % 2 == 1) ? true : false
                    
                    // Set parameter to skip successive stages
                    params.SKIP = skip.toString()
                }
            }
        }
        
        stage('Build') {
            steps {
                // Your build steps here
            }
        }
        
        stage('Test') {
            when {
                expression { params.SKIP != 'true' }
            }
            steps {
                // Your test steps here
            }
        }
        
        stage('Deploy') {
            when {
                expression { params.SKIP != 'true' }
            }
            steps {
                // Your deploy steps here
            }
        }
        
        // Add more stages here if needed
    }
}

