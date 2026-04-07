pipeline {
    agent any

    options {
        // Keep only the last 10 builds to save disk space
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Prevent multiple concurrent builds of the same pipeline to avoid conflicts with shared resources
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    triggers {
        githubPush()
    }

    environment {
        SLACK_CHANNEL = "#fakestore-jenkins"
    }

    stages {

        stage('Checkout') {
            agent any
            steps {
                // Checkout using Jenkins' built-in SCM support
                checkout scm
            }
        }

        stage('Test') {
            agent {
                // Override default agent to run tests in a Docker container with Maven and Java 17
                // Args '-u root' allows the container to write files as root, which is necessary for generating reports without permission issues
                docker {
                    image 'maven:3.9.5-eclipse-temurin-17-alpine'
                    args '-u root'
                }
            }
            steps {
                script {
                    // Catch test failures but continue pipeline
                    try {
                        sh 'mvn clean test'
                    } catch (Exception e) {
                        echo "Tests failed, but continuing to generate reports..."
                        // Mark build as UNSTABLE instead of FAILURE to allow post steps to run
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    // Copy any root-level allure-results into target/allure-results for Jenkins
                    sh 'if [ -d allure-results ]; then mkdir -p target/allure-results && cp -r allure-results/* target/allure-results/; fi'

                    // Save results for the next stage - include both Allure results and Surefire reports
                    stash name: 'results', includes: 'target/allure-results/**, target/surefire-reports/**'
                    
                    // Apply full permissions so Jenkins can clean up root-owned files
                    sh 'chmod -R 777 ${WORKSPACE}'
                }
            }
        }

        stage('Reports') {
            agent any
            steps {
                // Remove existing files before unstashing to avoid mixed/stale artifacts from previous runs
                sh 'rm -rf ${WORKSPACE}/* ${WORKSPACE}/.[!.]* 2>/dev/null || true'

                // Restore results from the previous stage
                unstash 'results'
                
                // Publish Allure report - uses pre-generated report from Maven
                allure([
                    includeProperties: false,
                    // Uses the default Allure command line tool installed on Jenkins, so no need to specify a JDK
                    jdk: '',
                    commandline: 'allure',
                    results: [[path: 'target/allure-results']],
                    // Publish report after every run
                    reportBuildPolicy: 'ALWAYS'
                ])
                
                // Publish JUnit XML results
                junit '**/target/surefire-reports/*.xml'
                
                // Publish HTML report for Surefire
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'API Test Reports'
                ])
            }
            post {
                always {
                    // Fix permissions so Jenkins can clean up on next run
                    // This is necessary because the Docker container ran as root and created files owned by root
                    sh 'chmod -R 777 ${WORKSPACE} 2>/dev/null || true'
                }
            }
        }

    }

    post {

        always {
            script {
                def status = currentBuild.result ?: 'SUCCESS'
                def isPassed = (status == 'SUCCESS')
                def color = isPassed ? 'good' : (status == 'UNSTABLE' ? 'warning' : 'danger')
                def statusEmoji = isPassed ? '✅' : '❌'
                def statusText = isPassed ? 'PASSED' : 'FAILED'
                def pipelineStatus = isPassed ? 'CI Pipeline Passed' : 'CI Pipeline Failed'
                
                // Get commit SHA (first 7 characters)
                def commitSha = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
                
                // Get repository name from GIT_URL
                def repoName = env.GIT_URL ? env.GIT_URL.replaceAll(/^.*[\/:]([^\/]+\/[^\/]+?)(\.git)?$/, '$1') : env.JOB_NAME
                
                // Build notification message
                def message = """${statusEmoji} ${pipelineStatus}

Repository: ${repoName}
Branch: ${env.BRANCH_NAME ?: 'develop'}
Commit: ${commitSha}
Status: ${statusText}

Run Details:
${env.BUILD_URL}console

Allure Report:
${env.BUILD_URL}allure/
""".stripIndent().trim()
                
                // Slack Notification
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: color,
                    message: message
                )
                
                // Email Notification
                emailext(
                    subject: "CI ${statusEmoji} ${statusText}: ${repoName}",
                    body: message,
                    to: 'nksarps@gmail.com',
                )
            }
        }

    }
}
