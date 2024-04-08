
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
                    parameterizedSpecification('''TZ=America/Toronto\nH H 2,16 * * %DEPLOYMENT=dev_us-south;ARTIFACTORY_DOCKERHUB=docker-na-public.artifactory.swg-devops.com/wcp-argonauts-kirkman-team-observability-docker-virtual;DOCKER_IMAGE=ubi8/ubi-init;DOCKER_TAG=8.9;REGISTRY=us.icr.io;REGISTRY_NAMESPACE=atracker''')
                }
            }
        }
    }
    parameters {
        choiceParam('DEPLOYMENT', ['prod_us-south', 'prod_us-east', 'prod_au-syd'], 'Choose the DEPLOYMENT from the Dropdown List')
        stringParam {
            name('ARTIFACTORY_DOCKERHUB')
            defaultValue('docker-na-public.artifactory.swg-devops.com/wcp-argonauts-kirkman-team-observability-docker-virtual')
            description('Name of the Artifactory from where the image will be pulled')
            trim(true)
        }
        stringParam {
            name('DOCKER_IMAGE')
            defaultValue('ubi8/ubi')
            description('Docker Image Name (e.g ubuntu or ubi8/ubi)')
            trim(true)
        }
        stringParam {
            name('DOCKER_TAG')
            defaultValue('8.3')
            description('Docker Image Tag (e.g latest)')
            trim(true)
        }
        stringParam {
            name('REGISTRY')
            defaultValue('us.icr.io')
            description('This is the target container registry (e.g us.icr.io)')
            trim(true)
        }
        stringParam('REGISTRY_NAMESPACE', 'atracker-prod', 'This is the target namespace (e.g atracker-prod)')
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