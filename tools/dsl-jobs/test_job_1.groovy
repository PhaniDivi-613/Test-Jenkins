
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
        pipelineTriggers {
            triggers {
                parameterizedCron {
                    parameterizedSpecification('''TZ=America/Toronto\nH H * * * %DEPLOYMENT=dev_us-south;ARTIFACTORY_DOCKERHUB=docker-na-public.artifactory.swg-devops.com/wcp-argonauts-kirkman-team-observability-docker-virtual;DOCKER_IMAGE=ubi8/ubi-init;DOCKER_TAG=8.9;REGISTRY=us.icr.io;REGISTRY_NAMESPACE=atracker''')
                }
            }
        }
    }
    parameters {
        stringParam('TRAIN_ID', '', 'Leave it blank for dev environment. Will be used in stage/prod environment only. <br>If you already have a valid train id, put it here and the job will skip Train request/approval.' )
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
            scriptPath("tools/jenkins-jobs/test-job-1.groovy")
        }
    }
}