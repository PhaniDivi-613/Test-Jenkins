
pipelineJob("Testing Job 1") {
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
        stringParam('PARAM', 'False', 'True or False')
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
            scriptPath("tools/jenkins-jobs/test-job-2.groovy")
        }
    }
}