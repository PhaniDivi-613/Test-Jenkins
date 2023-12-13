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

        currentDateTime.isAfter(freezeStart) && currentDateTime.isBefore(freezeEnd) && regions.contains(region)
    }

    println "In Freeze: ${inFreeze}"
    return inFreeze != null
}
pipeline { 
    agent {
        label 'agent-1'
    }
    environment{
        REGION = "${env.DEPLOYMENT.split('_')[1]}"
    }
    stages{
        stage('List All Files') {
            when {
                expression {
                    isInCodeFreeze(REGION) == False
                } 
            }
            steps{
                script {
                    sh 'cd . && ls -la /'
                    sh 'find . -name "codefreeze-timings.json"'
                }
            }
        }
        stage('simple stage') {
            steps{
                script {
                    sh 'echo "phani"'
                }
            }
        }
    }
}