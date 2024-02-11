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

                        // echo "BUILD_CAUSE_USERIDCAUSE: ${BUILD_CAUSE_USERIDCAUSE}"
                        // echo "BUILD_CAUSE_SCMTRIGGER: ${BUILD_CAUSE_SCMTRIGGER}"
                        // echo "BUILD_CAUSE_UPSTREAMTRIGGER: ${BUILD_CAUSE_UPSTREAMTRIGGER}"
                        // echo "BUILD_CAUSE_MANUALTRIGGER: ${BUILD_CAUSE_MANUALTRIGGER}"
                        echo "CAUSE: ${CAUSE}"
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
