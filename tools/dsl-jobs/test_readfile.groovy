import groovy.json.JsonSlurper

def choicesString = readFileFromWorkspace('options.txt')
def choices = choicesString.split('\n').collect { "$it" }

def jsonSlurper = new JsonSlurper()
def services = ["atracker", "metrics-router"]
def cron = "TZ=America/Toronto\n\n"
println(readFileFromWorkspace('promotion-cron-timings.json'))
def cronTimings = jsonSlurper.parseText(readFileFromWorkspace('promotion-cron-timings.json'))
println(cronTimings)
for (service in services) {
    cronTimings["$service"].each{ region, cronExp ->
        cron += "$cronExp" + " %DEPLOYMENT=" + "$region" + ";OBSERVABILITY_SERVICE=" + "$service\n"
    }
}
println(cron)

pipelineJob("Testing the reading of a file for parameter options") {
    properties {
        githubProjectUrl('git@github.com:PhaniDivi-613/Test-Jenkins.git')
        buildDiscarder {
            strategy {
                logRotator {
                    daysToKeepStr("-1")
                    numToKeepStr("199")
                    artifactDaysToKeepStr("-1")
                    artifactNumToKeepStr("-1")
                }
            }
        }
    }
    parameters {
        choiceParam('OPTION', choices, 'Choose the option')
    }
    description()
    keepDependencies(false)
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url("https://github.com/PhaniDivi-613/Test-Jenkins.git")
                    }
                    branch("*/main")
                }
            }
            scriptPath("tools/jenkins-jobs/test-readfile.groovy")
        }
    }
}