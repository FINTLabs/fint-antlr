pipeline {
    agent {
        docker {
            label 'docker'
            image 'gradle:6.3.0-jdk8'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'gradle --no-daemon clean build'
            }
        }
        stage('Deploy') {
            environment {
                REPOSILITE = credentials('reposilite')
            }
            when {
                tag pattern: "v\\d+\\.\\d+\\.\\d+(-\\w+-\\d+)?", comparator: "REGEXP"
            }
            steps {
                script {
                    VERSION = TAG_NAME[1..-1]
                }
                sh "echo Version is ${VERSION}"
                sh "gradle --no-daemon -Pversion=${VERSION} -PreposiliteUsername=${REPOSILITE_USR} -PreposiliteToken=${REPOSILITE_PSW} publish"
            }
        }
    }
}