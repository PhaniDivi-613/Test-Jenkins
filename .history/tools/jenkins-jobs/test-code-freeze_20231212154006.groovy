import groovy.json.JsonSlurper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

boolean isInCodeFreeze(String region) {
    filePath = 
    def resourcePath = readFileFromWorkspace("tools/codefreeze-timings.json")
    println resourcePath
    if (resourcePath) {
        jsonData = resourcePath.text
    } else {
        println "Resource file 'codefreeze-timings.json' not found or inaccessible"
        // Handle the case where the resource file is not accessible or doesn't exist
        // For example, you might want to set a default value for jsonData or take alternative actions.
    }
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
