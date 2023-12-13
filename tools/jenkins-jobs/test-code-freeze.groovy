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
        LOCATION = "${env.DEPLOYMENT.split('_')[1]}"
    }
    stages {
        stage('Check Code Freeze') {
            steps {
                script {
                    def isCronTrigger = currentBuild.rawBuild.getCause(hudson.triggers.TimerTrigger$TimerTriggerCause)
                    def codeFreezeActive = isInCodeFreeze(env.LOCATION)

                    if (codeFreezeActive && isCronTrigger) {
                        error "Code freeze is active for ${env.LOCATION} and job triggered by cron. Halting the job."
                    } else if (codeFreezeActive && !isCronTrigger) {
                        def userInput = input(
                            id: 'codeFreezeConfirmation',
                            message: "Code freeze is active for ${env.LOCATION}. Do you want to proceed?",
                            parameters: [choice(choices: ['Yes', 'No'], description: 'Choose whether to proceed', name: 'Confirmation')]
                        )
                        
                        if (userInput == 'No') {
                            error "User opted not to proceed during code freeze. Halting the job."
                        } else {
                            echo "Continuing the job despite code freeze."
                        }
                    } else {
                        echo "No code freeze active for ${env.LOCATION}. Continuing the job."
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
