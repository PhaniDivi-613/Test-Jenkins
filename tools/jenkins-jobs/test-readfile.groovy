E2E_RESULT = "SUCCESS"
STAGE_DETAILS = []

pipeline {
    agent {
        label 'agent-1'
    }
    stages {
        stage('Trigger E2E tests') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        int status = sh(script: """
                                exit 0
                            """, returnStatus: true)
                        if(status != 0){
                            E2E_RESULT = "FAILURE"
                            STAGE_DETAILS.add("Stage: Trigger E2E tests - Error in Tests, Check the logs.")
                        }
                    }
                }
            }
        }
        stage('Generate release file for the stage environment') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                            if(E2E_RESULT == 'SUCCESS' || E2E_TESTS_BYPASS.toBoolean() || E2E_TESTS_BYPASS_OVERRIDE.toBoolean()){
                                sh """
                                    exit 0
                                """
                            }else{
                                currentBuild.result = 'FAILURE'
                                STAGE_DETAILS.add("Stage: Generate release file for the stage environment - skipped due to earlier failure")
                            }
                    } 
                }
            }
        }  
    }
    post {
        always {
            script {
                println STAGE_DETAILS
                slack.sendInfoMessage("Test Cases failed or Error generating release file for staging. Check the Job", slackChannel)
                slack.markProcessingFailure()
            }
        }
    }
}
