def CONTAINER_NAME = "greetings_app"
def CON_NAME_TEST = "test_greet"
def DOCKER_HUB_USER = "dmanevd"

node {
    stage('Initialize') {
       	def dockerHome = tool 'myDocker'
       	env.PATH = "${dockerHome}/bin:${env.PATH}"
    }

    stage('Checkout') {
        deleteDir()
        checkout scm
    }

    stage('Build') {
        try {
	CURRENT_TAG = sh(returnStdout: true, script: "git describe --tags").trim()
        sh "docker build -t $CONTAINER_NAME:$CURRENT_TAG -t $CONTAINER_NAME --pull --no-cache ."
        echo "Image build complete"
 	currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
                currentBuild.result = 'FAILURE'
        }
    }

    stage('Unit tests') {
	try {
        status = sh(returnStdout:true, script: "docker run --rm --entrypoint bash --name $CON_NAME_TEST $CONTAINER_NAME:$CURRENT_TAG -c 'python greetings_app/test_selects.py 2>/dev/null && echo \$?'").trim()
        sleep 5
        if (status != '0'){
            currentBuild.result = 'FAILED'
            sh "exit ${status}"
        }
	currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
                currentBuild.result = 'FAILURE'
        }
    }

    stage('Push to DockerHub') {
	try {
		withCredentials([usernamePassword(credentialsId: 'docker-hub-dmanevd', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
            	sh "docker login -u $USER -p $PASSWORD"
            	sh "docker tag $CONTAINER_NAME:$CURRENT_TAG $USER/$CONTAINER_NAME:$CURRENT_TAG"
            	sh "docker push $DOCKER_HUB_USER/$CONTAINER_NAME:$CURRENT_TAG"
		sh "docker tag $CONTAINER_NAME:$CURRENT_TAG $USER/$CONTAINER_NAME:latest"
                sh "docker push $DOCKER_HUB_USER/$CONTAINER_NAME:latest"
        	}
		echo "Image push complete!"
		currentBuild.result = 'SUCCESS'
    	} catch (Exception err) {
		currentBuild.result = 'FAILURE'
	}
	}

}
