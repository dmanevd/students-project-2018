pipelineJob("CI-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/dmanevd/students-project-2018.git')
                        credentials('git-dmanev')
                    }
                    branch('origin/tags/*.*')
                }
            }
            scriptPath("jenkins/CI_job.groovy")
        }
    }
    triggers {
        periodic(5)
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
                    branch('origin/tags/*.*')
                }
            }
            scriptPath("jenkins/CD_job.groovy")
        }
    }
    triggers {
        upstream('CI_job')
    }

    parameters {
	gitParam('PARAM_GIT') {
	    type('TAG')
	    defaultValue('latest')
        sortMode('DESCENDING_SMART') 
        }
    }

}