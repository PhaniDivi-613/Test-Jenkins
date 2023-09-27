def E2E_RESULT = "SUCCESS"
def STAGE_DETAILS = []
def RELEASE_FILE = "FAILURE"

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
                                echo ${E2E_TESTS_BYPASS}
                                exit 1
                            """, returnStatus: true)
                        if(status != 0){
                            E2E_RESULT = "FAILURE"
                            STAGE_DETAILS.add("Stage: Trigger E2E tests - Error in Tests, Check the logs.")
                            error("Tests failed - Check the Job logs")
                        }
                    }
                }
            }
        }
        stage('Generate release file for the stage environment') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        if(E2E_RESULT == 'SUCCESS' || env.E2E_TESTS_BYPASS == 'True' ){
                            sh """
                                exit 0
                            """
                        }else{
                            echo "Release file is not being genrated as tests failed and both EE2E_TESTS_BYPASS and E2E_TESTS_BYPASS_OVERRIDE are set to False"
                            STAGE_DETAILS.add("\nStage: Generate release file for the stage environment - skipped due to earlier failure")
                        }
                        RELEASE_FILE = "SUCCESS"
                    } 
                }
            }
        }  
    }
    post {
        always {
            script {
                println STAGE_DETAILS
                if(RELEASE_FILE == "FAILURE"){
                    println("Release file failed")
                }
            }
        }
    }
}
