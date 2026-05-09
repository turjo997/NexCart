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
        RENDER_BACKEND_SERVICE_ID = 'srv-cv2udl2j1k6c739pp0lg'
        RENDER_BACKEND_DEPLOY_HOOK = "https://api.render.com/deploy/${RENDER_BACKEND_SERVICE_ID}?key=HH45VpzmZPA"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'nexCart-ci-cd',
                credentialsId: 'github-creds',
                url: 'https://github.com/turjo997/NexCart.git'
            }
        }
        stage('Build') {
            parallel {
                stage('Java') {
                    steps {
                       sh 'mvn clean install'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh 'mvn test'
                }
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