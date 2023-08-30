def choicesString = readFileFromWorkspace('options.txt')
def choices = choicesString.split('\n').collect { "$it" }
println(choices[1])
def cron = '''TZ=America/Toronto\n
40 2 * * 1-4 %OPTION=${choices}'''
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
        pipelineTriggers {
            triggers {
                parameterizedCron {
                    parameterizedSpecification(cron)
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