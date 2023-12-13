pipelineJob("Testing Job for code freeze") {
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
        choiceParam('DEPLOYMENT', ["prod_au-syd", "prod_eu-fr2"], 'choose the region')
    }
    description()
    keepDependencies(false)
    definition {
        // Define params outside the cpsScm block
        def deploymentParam = params.DEPLOYMENT
        
        cpsScm {
            scm {
                git {
                    remote {
                        url("https://github.com/PhaniDivi-613/Test-Jenkins.git")
                    }
                    branch("*/main")
                }
            }
            // Use the defined params within the block
            if (deploymentParam == 'prod_au-syd') {
                scriptPath("tools/jenkins-jobs/test-code-freeze.groovy")
            } else {
                scriptPath("tools/jenkins-jobs/test-job-2.groovy")
            }
        }
    }
}
