pipeline {
    agent any
    stages {
        stage('Build And Test') {
            steps {
                sh 'echo "Building..."'
                sh 'chmod +x build.sh'
                sh './build.sh'
                archiveArtifacts artifacts: 'target/*'
            }
        }
    }
}