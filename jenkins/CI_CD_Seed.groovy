pipelineJob("CI-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/dmanevd/students-project-2018.git')
                        credentials('git-dmanev')
                    }
                    branch('refs/tags/*.*')
                }
            }
            scriptPath("jenkins/CI_job.groovy")
        }
    }
    triggers {
        scm('* M/55 * * *')
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
                    branch('refs/tags/*.*')
                }
            }
            scriptPath("jenkins/CD_job.groovy")
        }
    }
    triggers {
        upstream('CI-job')
    }

    parameters {
	gitParam('git_tags') {
	    description('image version')
            type('TAG')
	    selectedValue('DEFAULT')
            sortMode('DESCENDING_SMART')
        }
    }

}
