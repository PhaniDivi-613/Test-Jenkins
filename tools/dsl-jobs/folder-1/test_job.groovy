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
    description('Your job description goes here') // Add your job description
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
    }
    triggers {
        configure { project ->
	        def currentMinute = new Date().format('mm').toInteger()
            def deploymentToRun = currentMinute % 3 == 0 ? "prod_eu-fr2" : "prod_au-syd"
            def triggerSpec = '''TZ=America/Toronto
* * * * * %DEPLOYMENT=''' + deploymentToRun
            project / 'triggers' << 'hudson.triggers.TimerTrigger' {
                spec(triggerSpec)
            }
        }
    }
}