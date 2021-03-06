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
        scm('H/30 * * * *')
    }
}

pipelineJob("CD-job") {
    parameters {
      gitParameterDefinition {
      name('git_tags')
      type('PT_TAG')
      defaultValue('')
      description('Select the version')
      sortMode('DESCENDING_SMART')
      selectedValue('TOP')
      branch('refs/tags/*')
      branchFilter('')
      tagFilter('')
      useRepository('https://github.com/dmanevd/students-project-2018.git')
      quickFilterEnabled(false)

    }
  }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/dmanevd/students-project-2018.git')
                        credentials('git-dmanev')
                    }
                    branch('*/master')
                }
            }
            scriptPath("jenkins/CD_job.groovy")
        }
    }
    triggers {
        upstream('CI-job')
    }
}
