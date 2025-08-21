pipeline {
    agent any
    tools {
        maven 'Maven3'
        jdk 'JDK17'
        jfrog 'jfrog-cli'
    }
    environment {
        // Adjust SonarQube installation name to match Jenkins global config
        SONARQUBE_ENV = 'SonarQubeServer'
        SERVER_ID="innoimpex"
        REPO="maven-libs-release-local"
        REMOTE_PATH="com/example/demo-webapp/1.0.1/demo-webapp-1.0.1.war"
        LOCAL_PATH="/tmp/app-1.0.0.war"
    }
    

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Building project with Maven..."
                sh "mvn clean compile"
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo "Running tests and generating JaCoCo report..."
                sh "mvn test jacoco:report"
            }
            post {
                always {
                    echo "Publishing JaCoCo report..."
                    jacoco execPattern: '**/target/jacoco.exec',
                           classPattern: '**/target/classes',
                           sourcePattern: '**/src/main/java',
                           inclusionPattern: '**/*.class',
                           exclusionPattern: ''
                }
            }
        }

        stage('SonarQube Analysis') {
                steps {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        withSonarQubeEnv("${SONARQUBE_ENV}") {
                            sh """
                                mvn sonar:sonar \
                                    -Dsonar.projectKey=demo-project \
                                    -Dsonar.login=$SONAR_TOKEN
                            """
                        }
                    }
                }
            }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline failed because Quality Gate was: ${qg.status}"
                        }
                    }
                }
            }
        }

        stage('Publish artifacts') {
            steps {
                echo "Packaging WAR file..."
                sh "mvn deploy"
            }
        }
        stage('Pull artifacts') {
            steps {
                echo "Packaging WAR file..."
                sh """
                jf rt dl "${REPO}/${REMOTE_PATH}" "${LOCAL_PATH}" --server-id=${SERVER_ID}
                """
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying WAR to Tomcat..."
                sh """
                export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
                    ~/tomcat/bin/shutdown.sh
                    cp target/*.war ~/tomcat/webapps/
                    ~/tomcat/bin/startup.sh 
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed. Check the logs."
        }
    }
}
