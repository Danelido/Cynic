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
        stage('Deploy') {
            steps {
                sh 'echo "Deploying..."'
                sh 'chmod +x deploy.sh'
                sh './deploy.sh'
                sh 'echo "Deployed :)"'
            }
        }
    }
}