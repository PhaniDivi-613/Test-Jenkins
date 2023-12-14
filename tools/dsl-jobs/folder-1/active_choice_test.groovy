job('exampleJob') {
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
        stringParam('CRON_EXPRESSION', 'H * * * *', 'Cron Expression')
    }
    steps {
        // Define your build steps here if needed
    }
    concurrentBuild(false) // Ensure only one build runs at a time (if required)
    scm {
        git {
            remote {
                url('https://github.com/PhaniDivi-613/Test-Jenkins.git')
            }
            branch('*/main')
        }
    }
    triggers {
        cron(spec: "${params.CRON_EXPRESSION}")
    }
}
