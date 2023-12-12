import groovy.json.JsonSlurper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

boolean isInCodeFreeze(String region) {
    def jsonData = readFileFromWorkspace("codefreeze-timings.json")
    1701340796768
    def freezeData = new JsonSlurper().parseText(jsonData)

    def currentDateTime = ZonedDateTime.now()
    def inFreeze = freezeData.find { event ->
        def freezeStart = ZonedDateTime.parse(event."Freeze Start")
        def freezeEnd = ZonedDateTime.parse(event."Freeze End")
        def regions = event."Regions"

        currentDateTime.isAfter(freezeStart) &&
        currentDateTime.isBefore(freezeEnd) &&
        regions.contains(region)
    }

    return inFreeze != null
}

pipeline {
    agent {
        label 'agent-1'
    }
    environment {
        REGION = "${env.REGION}"
    }
    stages {
        stage('List All Files') {
            steps{
                sh 'cd . && ls -la /'
                sh 'find . -name "codefreeze-timings.json"'
            }   
        }
        stage('Halt Job based on Codefreeze') {
            when {
                expression {
                    isInCodeFreeze(env.REGION)
                }
            }
            steps {
                script {
                    currentBuild.result = 'ABORTED'
                    error "Job halted due to code freeze in the specified region (${env.REGION})"
                }
            }
        }
        stage('Stage 2') {
            steps {
                script {
                    echo "Stage 2 is executing"
                    // Your Stage 2 steps here
                }
            }
        }
    }
}
