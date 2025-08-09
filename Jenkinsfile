pipeline {
    agent any

    environment {
        // Adjust SonarQube installation name to match Jenkins global config
        SONARQUBE_ENV = 'SonarQubeServer'  
        // Adjust Maven settings if needed
        MAVEN_HOME = tool name: 'Maven3', type: 'maven'
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
                sh "${MAVEN_HOME}/bin/mvn clean compile"
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo "Running tests and generating JaCoCo report..."
                sh "${MAVEN_HOME}/bin/mvn test jacoco:report"
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
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar -Dsonar.projectKey=demo-project"
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

        stage('Package WAR') {
            steps {
                echo "Packaging WAR file..."
                sh "${MAVEN_HOME}/bin/mvn package"
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying WAR to Tomcat..."
                sh """
                    sudo systemctl stop tomcat
                    sudo cp target/*.war /opt/tomcat/webapps/
                    sudo systemctl start tomcat
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

