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
node('agent-1') {
    environment {
        REGION = "${env.DEPLOYMENT.split('_')[1]}"
    }
    if (isInCodeFreeze(${REGION})) {
        echo "Code freeze detected in the specified region (${region}). Skipping the job."
        currentBuild.result = 'ABORTED'
        error('Code freeze detected')
    }
    stages{
        stage('List All Files') {
            script {
                sh 'cd . && ls -la /'
                sh 'find . -name "codefreeze-timings.json"'
            }
        }

        stage('Stage 2') {
            scirpt {
                echo "Stage 2 is executing"
            }
        }
    } 
}