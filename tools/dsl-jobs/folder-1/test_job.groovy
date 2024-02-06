// pipelineJob("Testing Job for code freeze") {
//     properties {
//         githubProjectUrl('git@github.com:PhaniDivi-613/Test-Jenkins.git')
//         buildDiscarder {
//             strategy {
//                 logRotator {
//                     daysToKeepStr("-1")
//                     numToKeepStr("199")
//                     artifactDaysToKeepStr("-1")
//                     artifactNumToKeepStr("-1")
//                 }
//             }
//         }
//     }
//     parameters {
//         choiceParam('DEPLOYMENT', ["prod_au-syd", "prod_eu-fr2"], 'choose the region')
//     }
//     description('Your job description goes here') // Add your job description
//     keepDependencies(false)
//     definition {
//         cpsScm {
//             scm {
//                 git {
//                     remote {
//                         url("https://github.com/PhaniDivi-613/Test-Jenkins.git")
//                     }
//                     branch("*/main")
//                 }
//             }
//             scriptPath("tools/jenkins-jobs/test-code-freeze.groovy")
//         }
//     }
//     triggers {
//         parameterizedCron {
//             parameterizedSpecification('''TZ=America/Toronto
// * * * * * %DEPLOYMENT=prod_au-syd
// * * * * * %DEPLOYMENT=prod_eu-fr2
// ''')
//         }
//     }
// }


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
        parameterizedCron {
            parameterizedSpecification('''TZ=America/Toronto
* * * * * %DEPLOYMENT=prod_au-syd
''')
            parameterizedSpecification('''TZ=America/Toronto
* * * * * %DEPLOYMENT=prod_eu-fr2
''')
        }
    }
}

configure { node ->
    def job = node / 'job' / 'triggers' / 'hudson.triggers.ParameterizedCron' / 'parameterizedSpecification'
    job.each { spec ->
        def deployment = spec.text().contains('prod_au-syd') ? 'prod_au-syd' : 'prod_eu-fr2'
        def cronExpression = deployment == 'prod_au-syd' ? '*/5 * * * *' : '0-4,6-7,9-59 * * * *'
        spec.replaceBody('''TZ=America/Toronto
${cronExpression} %DEPLOYMENT=${deployment}
''')
    }
}

