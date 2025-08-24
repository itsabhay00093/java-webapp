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
        REMOTE_PATH="com/example/demo-webapp/1.0.1/demo-webapp-1.0.0.war"
        LOCAL_PATH="/tmp/demo-webapp-1.0.0.war"
        ARTIFACT_URL = "https://innoimpex.jfrog.io/artifactory/maven-libs-release-local/com/example/demo-webapp/1.0.0/demo-webapp-1.0.0.war"
        OUTPUT_FILE  = "demo-webapp-1.0.0.war"
    }
    

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
            }
        }
        stage('Check and Update Version') {
            when { expression { env.BRANCH_NAME != 'main' } }
            steps {
                script {
                    def branchName = env.BRANCH_NAME ?: 'unknown'                    
                    // Read version from pom.xml
                    def version = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    echo "Current POM version: ${version}"
                    echo "Current Branch: ${branchName}"
                    // If branch is release, remove -SNAPSHOT
                    if (branchName == 'release' && version.endsWith('-SNAPSHOT')) {
                        def newVersion = version.replace('-SNAPSHOT', '')
                        echo "Updating version to: ${newVersion}"
                        sh """
                            mvn versions:set -DnewVersion=${newVersion} -DprocessAllModules
                            mvn versions:commit
                        """
                    } else {
                        echo "No version update required."
                    }
                }
            }
        }
        stage('Build') {
            when { expression { env.BRANCH_NAME != 'main' } }
            steps {
                echo "Building project with Maven..."
                sh "mvn clean compile"
            }
        }

        stage('Test & Code Coverage') {
            when { expression { env.BRANCH_NAME != 'main' } }
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
            when { expression { env.BRANCH_NAME != 'main' } }
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
            when { expression { env.BRANCH_NAME != 'main' } }
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
            when { expression { env.BRANCH_NAME != 'main' } }
            steps {
                echo "Packaging WAR file..."
                sh "mvn deploy"
            }
        }
        stage('Pull artifacts') {
            steps {
                script {
                echo "Pulling WAR file..."
                withCredentials([usernamePassword(credentialsId: 'jfrogid', usernameVariable: 'ART_USER', passwordVariable: 'ART_PASS')]) {
                    sh '''
                        curl -u $ART_USER:$ART_PASS -o $OUTPUT_FILE "$ARTIFACT_URL"
                    '''
            }
            }
        }
        }
        stage('release Approval') {
            when { expression { env.BRANCH_NAME == 'main' } }
            steps {
                script {
                    timeout(time: 30, unit: 'MINUTES') {
                        input message: 'Approve deployment to Production?',
                            ok: 'Approve',
                            submitter: 'releasemaster'
                    }
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying WAR to Tomcat..."
                sh """
                    ~/tomcat/bin/shutdown.sh
                    sleep 5
                    cp ${OUTPUT_FILE} ~/tomcat/webapps/
                    ~/tomcat/bin/startup.sh
                    sleep 5 
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
