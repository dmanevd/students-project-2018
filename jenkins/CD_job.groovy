def CONTAINER_NAME = "greetings_app"
def DOCKER_HUB_USER = "dmanevd"
def APP_PORT = "5000"
def DB_NAME = "greetings"
def DB_PORT = "5432"
def APP_HTTP_PORT = "80"
def PROXY_CONTAINER_NAME = "nginx-proxy"

node {
    stage('Initialize') {
                def dockerHome = tool 'myDocker'
                env.PATH = "${dockerHome}/bin:${env.PATH}"
        }

    stage('Image Prune') {
        try {
            sh "docker image prune -f"
            sh "docker stop $CONTAINER_NAME"
            sh "docker rm $CONTAINER_NAME"
            currentBuild.result = 'SUCCESS'
          } catch (Exception err) {
            currentBuild.result = 'FAILED'
        }
    }

    stage('Running and Integration tests') {
            withCredentials([usernamePassword(credentialsId: 'postgresql-dmanev', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                sh "docker pull $DOCKER_HUB_USER/$CONTAINER_NAME:${env.git_tags}"
                sh "docker run -d --network greetings_application_default  -p $APP_PORT:$APP_PORT -e 'DB_URL=postgresql://$USER:$PASSWORD@postgresql:$DB_PORT/$DB_NAME' --name $CONTAINER_NAME $DOCKER_HUB_USER/$CONTAINER_NAME:${env.git_tags}"
            }
        try {            
	    status = sh(returnStdout: true, script: "docker inspect --format='{{.State.Status}}' $CONTAINER_NAME").trim()
            if (status != 'running'){
                currentBuild.result = 'FAILED'
                sh "exit 1"
            }
            sleep 5
            currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
            currentBuild.result = 'FAILED'
        }
        try {
            APP_IP_ADDR = sh(returnStdout: true, script: "docker inspect $CONTAINER_NAME --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'").trim()
            curl_status_app = sh(returnStdout: true, script: "curl --silent --connect-timeout 15 --show-error --fail http://$APP_IP_ADDR:$APP_PORT 1>/dev/null && echo \$?")
            if (curl_status_app != '0') {
                currentBuild.result = 'FAILED'
                sh "exit ${curl_status_app}"
            }
            sleep 5
            currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
            currentBuild.result = 'FAILED'
        }
        try {
            grep_status_app = sh(returnStdout: true, script: "curl --silent --connect-timeout 15 --show-error --fail http://$APP_IP_ADDR:$APP_PORT | grep 'Greetings, stranger!' 1>/dev/null && echo \$?").trim()
            if (grep_status_app != '0') {
                currentBuild.result = 'FAILED'
                sh "exit ${grep_status_app}"
            }
            sleep 5
            currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
            currentBuild.result = 'FAILED'
        }
        try {
            PROXY_IP_ADDR = sh(returnStdout: true, script: "docker inspect $PROXY_CONTAINER_NAME --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'").trim()
            curl_status_pr = sh(returnStatus: true, script: "curl --silent --connect-timeout 15 --show-error --fail http://$PROXY_IP_ADDR:$APP_HTTP_PORT 1>/dev/null && echo \$?")
            sh(returnStatus: true, script: "echo http://$APP_IP_ADDR:$APP_PORT")
            if (curl_status_pr != '0') {
                currentBuild.result = 'FAILED'
                sh "exit ${curl_status_pr}"
            }
            sleep 5
            currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
            currentBuild.result = 'FAILED'
        }
        try {
            grep_status_pr = sh(returnStdout: true, script: "curl --silent --connect-timeout 15 --show-error --fail http://$PROXY_IP_ADDR:$APP_HTTP_PORT | grep 'Greetings, stranger!' 1>/dev/null && echo \$?").trim()
            if (grep_status_pr != '0') {
                currentBuild.result = 'FAILED'
                sh "exit ${grep_status_pr}"
            }
            echo "Application started on http://$PROXY_IP_ADDR:$APP_HTTP_PORT"
            currentBuild.result = 'SUCCESS'
        } catch (Exception err) {
            currentBuild.result = 'FAILED'
        }     
    }
}
