import groovy.json.JsonSlurper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

boolean isInCodeFreeze(String region) {
    def jsonData = readFile 'tools/codefreeze-timings.json'
    def freezeData = new groovy.json.JsonSlurper().parseText(jsonData)

    // Get the current date and time in Asia/Kolkata time zone
    def currentDateTime = ZonedDateTime.now(ZoneId.of('Asia/Kolkata'))

    // Format the currentDateTime to display only date and time without fractional seconds
    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    def formattedCurrentDateTime = currentDateTime.format(formatter)

    // Convert currentDateTime to UTC for comparison with freezeStart
    def currentDateTimeUTC = currentDateTime.withZoneSameInstant(ZoneId.of("UTC"))

    def inFreeze = freezeData.find { event ->
        def freezeStart = ZonedDateTime.parse(event."Freeze Start").withZoneSameInstant(ZoneId.of("UTC"))
        def freezeEnd = ZonedDateTime.parse(event."Freeze End").withZoneSameInstant(ZoneId.of("UTC"))
        def regions = event."Regions"

        println "Freeze Start: ${freezeStart}"
        println "Current DateTime: ${formattedCurrentDateTime}"
        println "Freeze End: ${freezeEnd}"
        println "Regions: ${regions}"

        currentDateTimeUTC.isAfter(freezeStart) &&
                currentDateTimeUTC.isBefore(freezeEnd) &&
                regions.contains(region)
    }

    println "In Freeze: ${inFreeze}"
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
