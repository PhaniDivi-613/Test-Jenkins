pipeline { 
    agent {
        label 'agent-1'
    }
    environment {
        LOCATION = "${env.DEPLOYMENT.split('_')[1]}"
        ENVIRONMENT = "${env.DEPLOYMENT.split('_')[0]}"
        BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription}"
        DESPITE_CODE_FREEZE = "${DESPITE_CODE_FREEZE}"
    }
    stages {
        stage('Check Code Freeze') {
            steps {
                script {
                    sh """
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
