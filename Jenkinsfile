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
                sh 'echo "CST..."'
                sh 'chmod +x scripts/ServerTest.sh'
                sh './scripts/ServerTest.sh'
            }
        }
    }
}