pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building..."'
                sh 'chmod +x scripts/build.sh'
                sh './scripts/build.sh'
                archiveArtifacts artifacts: '**/target/*'
            }
        }
        stage('Cynic Server Test') {
            steps {
                sh 'echo "Cynic Server Test..."'
                sh 'chmod +x scripts/ServerTest.sh'
                sh './scripts/ServerTest.sh'
            }
        }
        stage('Deploying Development build') {
            steps {
                sh 'echo "Deploying Development Build..."'
                sh 'chmod +x scripts/deployDevelopmentServer.sh'
                sh './scripts/deployDevelopmentServer.sh'
            }
        }
    }
}