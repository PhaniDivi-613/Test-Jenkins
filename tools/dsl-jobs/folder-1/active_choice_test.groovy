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
        activeChoiceParam('REGION') {
            description('Select the region based on date')
            script {
                groovyScript("""
                    def date = new Date()
                    def calendar = new GregorianCalendar()
                    calendar.setTime(date)
                    def dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    def items = []

                    if (dayOfMonth % 3 == 0) {
                        items.add("au-syd")
                    }
                    if (dayOfMonth % 5 == 0) {
                        items.add("eu-fr2")
                    }
                    return items
                """)
            }
        }
        stringParam('CRON_EXPRESSION', 'H * * * *', 'Cron Expression')
    }
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
            cron("${params.CRON_EXPRESSION}")
        }
    }
}
