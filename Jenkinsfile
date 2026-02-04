pipeline {
    agent any

    environment {
        VERSION = "1.0.0"
        IMAGE_NAME = "my-nexcart-web-app"
        DOCKERHUB_USER = "ullash997"
    }
    stages {
        stage('Stop Existing Containers') {
            steps {
                sh '''
                    docker compose --profile dev down
                '''
            }
        } 
        stage('Prepare Secrets') {
            steps {
                sh '''
                    mkdir -p secrets
                    echo "root" > secrets/db_password.txt
                    chmod 600 secrets/db_password.txt
                '''
            }
        }
        stage('Start Containers') {
            steps {
                sh '''
                    docker compose --profile dev up -d
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployment completed successfully'
            sh 'docker compose ps'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
    // post {
    //     always {
    //         echo 'Pipeline job finished.'
    //     }
    // }
