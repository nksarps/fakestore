pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
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
                checkout scm
            }
        }

        stage('Test') {
            agent {
                docker {
                    image 'maven:3.9.5-eclipse-temurin-17-alpine'
                    args '-u root'
                }
            }
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    // Copy any root-level allure-results into target/ as a fallback
                    sh 'if [ -d allure-results ]; then mkdir -p target/allure-results && cp -r allure-results/* target/allure-results/; fi'
                    
                    stash name: 'results', includes: 'target/allure-results/**, target/surefire-reports/**'
                    
                    // Apply full permissions so Jenkins can clean up root-owned files
                    sh 'chmod -R 777 ${WORKSPACE}'
                }
            }
        }

        stage('Reports') {
            agent any
            steps {
                // Clean workspace before unstashing (ignore errors from previous root-owned files)
                sh 'rm -rf ${WORKSPACE}/* ${WORKSPACE}/.[!.]* 2>/dev/null || true'
                
                unstash 'results'
                
                // Publish Allure report using Allure plugin
                allure([
                    includeProperties: false,
                    jdk: '',
                    commandline: 'allure',
                    results: [[path: 'target/allure-results']],
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
                    // Fix permissions so Jenkins can clean up
                    sh 'chmod -R 777 ${WORKSPACE} 2>/dev/null || true'
                }
            }
        }

    }

    post {

        always {
            script {
                // Fetch test summary from the build's test result action
                def total = 0, passed = 0, failed = 0, skipped = 0
                
                // Use currentBuild.currentResult for test data (no approval needed)
                // Note: Test counts will be extracted from junit step return value
                echo "Build result: ${currentBuild.result ?: 'SUCCESS'}"
                
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
                
                // Build Slack message
                def slackMessage = """${statusEmoji} *${pipelineStatus}*

                    📦 *Repository:* ${repoName}
                    🌿 *Branch:* ${env.BRANCH_NAME ?: 'develop'}
                    🧾 *Commit:* ${commitSha}
                    📊 *Status:* ${statusEmoji} ${statusText}

                    ━━━━━━━━━━━━━━━━━━
                    *Test Summary*
                    • Total Tests: ${total}
                    • Passed: ${passed}
                    • Failed: ${failed}
                    • Skipped: ${skipped}
                    ━━━━━━━━━━━━━━━━━━

                    ${isPassed ? '✅ All Tests Passed' : '🚨 Tests Failed'}

                    *Run Details:*
                    ${env.BUILD_URL}console

                    *Allure Report:*
                    ${env.BUILD_URL}allure/
                    """.stripIndent().trim()

                                    // Build email body (plain text version)
                                    def emailBody = """${statusEmoji} ${pipelineStatus}

                    Repository: ${repoName}
                    Branch: ${env.BRANCH_NAME ?: 'develop'}
                    Commit: ${commitSha}
                    Status: ${statusEmoji} ${statusText}

                    ━━━━━━━━━━━━━━━━━━
                    Test Summary
                    • Total Tests: ${total}
                    • Passed: ${passed}
                    • Failed: ${failed}
                    • Skipped: ${skipped}
                    ━━━━━━━━━━━━━━━━━━

                    ${isPassed ? 'All Tests Passed' : 'Tests Failed'}

                    Run Details:
                    ${env.BUILD_URL}console

                    Allure Report:
                    ${env.BUILD_URL}allure/
                    """.stripIndent().trim()
                
                // Slack Notification
                slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: color,
                    message: slackMessage
                )
                
                // Email Notification
                emailext(
                    subject: "CI ${statusEmoji} ${statusText}: ${repoName}",
                    body: emailBody,
                    to: 'nksarps@gmail.com',
                    recipientProviders: [culprits(), developers(), upstreamDevelopers()]
                )
            }
        }

    }
}
