def choicesString = readFileFromWorkspace('options.txt').replace('\n', ',')
def choicesArray = choicesString.split('\n').collect { "$it" }
println choicesArray
def cronRegex = '''TZ=America/Toronto\n
H */6 * * * %OPTION={}
H */6 * * * %OPTION{}
'''.format(deployments)
println cronRegex


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
                    parameterizedSpecification(cronRegex)
                }
            }
        }
    }
    parameters {
        choiceParam('OPTION', choicesArray, 'Choose the option')
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