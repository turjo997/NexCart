pipeline {
    agent any

    options {
        skipDefaultCheckout()
    }
    tools {
        maven "mvn"
    }

    environment {
        RENDER_API_KEY = credentials('render-api-key')
        RENDER_BACKEND_SERVICE_ID = 'srv-d7vc70ugvqtc73chn8qg'
        RENDER_BACKEND_DEPLOY_HOOK = "https://api.render.com/deploy/${RENDER_BACKEND_SERVICE_ID}?key=GcKxW3HEoVs"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'nexCart-ci-cd',
                url: 'https://github.com/turjo997/NexCart.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Deploy to Render') {
            steps {
                httpRequest(
                    url: "${RENDER_BACKEND_DEPLOY_HOOK}",
                    httpMode: 'POST',
                    validResponseCodes: '200:299'
                )
            }
        }
    }

    post {
        success {
            // Actions after the build succeeds
            echo 'Build was successful!'
        }
        failure {
            // Actions after the build fails
            echo 'Build failed. Check logs.'
        }
    }
}