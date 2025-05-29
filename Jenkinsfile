pipeline {
  agent any

  environment {
    DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
    SONARQUBE_TOKEN = credentials('sonarqube-token')
    SONARQUBE_SERVER = 'SonarQube'  // Must match name in Jenkins → "Configure SonarQube Servers"
  }

  tools {
    maven 'maven3'      // Ensure this name exists in Global Tools config
    nodejs 'node16'     // Ensure this name exists in Global Tools config
    jdk 'jdk17'         // Ensure this name exists in Global Tools config
  }

  stages {
    stage('Frontend: Install') {
      steps {
        dir('frontend') {
          sh 'npm install'
        }
      }
    }

    stage('Frontend: SonarQube Scan') {
      steps {
        dir('frontend') {
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            sh """
              npx sonar-scanner \
              -Dsonar.projectKey=frontend \
              -Dsonar.sources=src \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.token=$SONARQUBE_TOKEN
            """
          }
        }
      }
    }

    stage('Backend: Build & Test') {
      steps {
        dir('backend') {
          sh 'mvn clean verify'
        }
      }
    }

    stage('Backend: SonarQube Scan') {
      steps {
        dir('backend') {
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            sh """
              mvn sonar:sonar \
              -Dsonar.projectKey=backend \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.token=$SONARQUBE_TOKEN
            """
          }
        }
      }
    }

    stage('Docker Build & Push - Frontend') {
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
            def image = docker.build("jems007patel/servicehub-frontend", "frontend/")
            image.push('latest')
          }
        }
      }
    }

    stage('Docker Build & Push - Backend') {
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
            def image = docker.build("jems007patel/servicehub-backend", "backend/")
            image.push('latest')
          }
        }
      }
    }
  }

  post {
    always {
      echo '✅ Pipeline completed.'
    }
    failure {
      echo '❌ Pipeline failed.'
    }
  }
}
