pipelineJob("CI-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/dmanevd/students-project-2018.git')
                        credentials('git-dmanev')
                    }
                    branch('jenkins')
                }
            }
            scriptPath("jenkins/CI_job.groovy")
        }
    }
    triggers {
        scm('H/5 * * * *')
    }
}

pipelineJob("CD-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/dmanevd/students-project-2018.git')
                        credentials('git-dmanev')
                    }
                    branch('jenkins')
                }
            }
            scriptPath("jenkins/CD_job.groovy")
        }
    }
    triggers {
        upstream('CI-job')
    }

    parameters {
	gitParam('PARAM_GIT') {
	    type('TAG')
	    defaultValue('latest')
            sortMode('DESCENDING_SMART') 
        }
    }

}
