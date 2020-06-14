pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building..."'
                sh 'chmod +x build.sh'
                sh './build.sh'
                archiveArtifacts artifacts: 'target/*'
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo "Running..."'
                sh 'chmod +x run-dev.sh'
                sh './run-dev.sh'
            }
        }
    }
}