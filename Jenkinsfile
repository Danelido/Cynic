pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building..."'
                sh 'chmod +x build.sh'
                sh './build.sh'
                archiveArtifacts artifacts: '**/target/*'
            }
        }
        stage('Cynic Server Test') {
            steps {
                sh 'echo "CST..."'
                sh 'chmod +x ServerTest.sh'
                sh './ServerTest.sh'
            }
        }
    }
}