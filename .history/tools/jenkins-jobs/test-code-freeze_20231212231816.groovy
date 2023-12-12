import groovy.json.JsonSlurper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

boolean isInCodeFreeze(String region) {
    def jsonData = readFile 'tools/codefreeze-timings.json'
    def freezeData = new groovy.json.JsonSlurper().parseText(jsonData)

    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    def currentDateTime = ZonedDateTime.now(ZoneId.of('UTC')).format(formatter)

    def inFreeze = freezeData.find { event ->
        def freezeStart = ZonedDateTime.parse(event."Freeze Start").format(formatter)
        def freezeEnd = ZonedDateTime.parse(event."Freeze End").withZoneSameInstant(ZoneId.of("UTC")).format(formatter)
        def regions = event."Regions"

        println "Freeze Start: ${freezeStart}"
        println "Current DateTime: ${currentDateTime}"
        println "Freeze End: ${freezeEnd}"
        println "Regions: ${regions}"

        currentDateTime.isAfter(freezeStart) &&
            currentDateTime.isBefore(freezeEnd) &&
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
        REGION = "${env.DEPLOYMENT.split('_')[1]}"
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
