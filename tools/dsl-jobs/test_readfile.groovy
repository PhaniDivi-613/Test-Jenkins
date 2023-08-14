def choicesString = readFileFromWorkspace('options.txt')
def choicesString = deploymentsString.split('\n').collect { "$it" }
println choicesString
for(int i=0; i<deployments.size(); i++){
    println choicesString[i]
}
def cronString = "TZ=America/Toronto\n
H */6 * * * %OPTION=${choicesString}"
println cronString


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
                    parameterizedSpecification(cronString)
                }
            }
        }
    }
    parameters {
        choiceParam('OPTION', choicesString, 'Choose the option')
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