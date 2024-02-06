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
        cpsScm {
            scm {
                git {
                    remote {
                        url("https://github.com/PhaniDivi-613/Test-Jenkins.git")
                    }
                    branch("*/main")
                }
            }
            scriptPath("tools/jenkins-jobs/test-code-freeze.groovy")
        }
        triggers {
            cron('* * * * *') // Executes the job every minute
        }
    }
    configure {
        def jobDSL = it / 'builders' / 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        jobDSL / 'using' / 'scriptText' << '''
if (params.DEPLOYMENT != 'prod_au-syd') {
    build job: 'Testing Job for code', parameters: [string(name: 'DEPLOYMENT', value: params.DEPLOYMENT)]
}
'''
    }
}
