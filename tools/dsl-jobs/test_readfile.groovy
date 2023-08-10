pipelineJob("Testing the reading of a file for parameter options") {
    properties {
        githubProjectUrl('git@github.com:PhaniDivi-613/Test-Jenkins.git')
        buildDiscarder {
            strategy {
                logRotator {
                    daysToKeepStr("-1")
                }
            }
        }
    }
    parameters {
        choiceParam('OPTION', ["option_1", "option_2", "option_3"], 'Choose the option')
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
                    branch("*/master")
                }
            }
            scriptPath("tools/jenkins-jobs/test-fileread.groovy")
        }
    }
}