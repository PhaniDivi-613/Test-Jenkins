import groovy.json.JsonSlurper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

boolean isInCodeFreeze(String region) {
    def jsonData = readFile 'tools/codefreeze-timings.json'
    def freezeData = new groovy.json.JsonSlurper().parseText(jsonData)

    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    def currentDateTime = ZonedDateTime.now(ZoneId.of('UTC'))

    def inFreeze = freezeData.find { event ->
        def freezeStart = ZonedDateTime.parse(event."Freeze Start").withZoneSameInstant(ZoneId.of("UTC"))
        def freezeEnd = ZonedDateTime.parse(event."Freeze End").withZoneSameInstant(ZoneId.of("UTC"))
        def regions = event."Regions"

        println "Freeze Start: ${freezeStart}"
        println "Current DateTime: ${currentDateTime}"
        println "Freeze End: ${freezeEnd}"
        println "Regions: ${regions}"

        currentDateTime.isAfter(freezeStart) && currentDateTime.isBefore(freezeEnd) && regions.contains(region)
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
        stage('Check Code Freeze') {
            steps {
                script {
                    if(currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)) {
                        echo "Triggered by a user"
                    } else if(currentBuild.rawBuild.getCause(hudson.triggers.TimerTrigger$TimerTriggerCause)) {
                        echo "Triggered by a timer/cron"
                    }
                    def inCodeFreeze = isInCodeFreeze(env.REGION)
                    if (inCodeFreeze) {
                        error "Code freeze is active for ${env.REGION}. Halting the job."
                    } else {
                        echo "No code freeze active for ${env.REGION}. Continuing the job."
                    }
                }
            }
        }
        stage('List All Files') {
            steps {
                script {
                    sh 'cd . && ls -la /'
                    sh 'find . -name "codefreeze-timings.json"'
                }
            }
        }
        stage('Simple Stage') {
            steps {
                script {
                    sh 'echo "phani"'
                }
            }
        }
    }
}
