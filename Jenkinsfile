// Runs on pushes to main, develop, and any feature/* branch.
pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        IMAGE_NAME = "fakestore-ci"
        CONTAINER_NAME = "fakestore-ci-run"
        SLACK_CHANNEL = "#fakestore-jenkins"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test (Docker)') {
            steps {
                script {
                    sh """
                        docker build \
                            --target ci \
                            --tag ${IMAGE_NAME}:${BUILD_NUMBER} \
                            .
                    """
                }
            }
        }

        stage('Extract Reports') {
            steps {
                script {
                    sh """
                        mkdir -p ./target

                        docker create --name ${CONTAINER_NAME} ${IMAGE_NAME}:${BUILD_NUMBER}

                        # Surefire XML results for JUnit plugin
                        docker cp ${CONTAINER_NAME}:/app/target/surefire-reports ./target/surefire-reports || true

                        # Allure raw results for Allure plugin
                        docker cp ${CONTAINER_NAME}:/app/target/allure-results ./target/allure-results || true

                        # Allure HTML report for HTML publisher
                        docker cp ${CONTAINER_NAME}:/app/target/site/allure-maven-plugin ./target/allure-html || true

                        # Test exit code written by the Docker RUN step
                        docker cp ${CONTAINER_NAME}:/test-exit-code ./test-exit-code || true

                        docker rm ${CONTAINER_NAME}
                    """

                    // Fail the build if tests failed, but only after reports are extracted
                    def testExitFile = 'test-exit-code'
                    if (fileExists(testExitFile)) {
                        def testExit = readFile(testExitFile).trim()
                        if (testExit != '0') {
                            currentBuild.result = 'FAILURE'
                            error("Tests failed with exit code ${testExit}")
                        }
                    }
                }
            }
        }

    }

    post {

        always {
            // Publish JUnit XML results
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true

            // Publish Allure HTML report via HTML Publisher plugin
            publishHTML(target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : 'target/allure-html',
                reportFiles          : 'index.html',
                reportName           : 'Allure Report',
                reportTitles         : 'Allure Report'
            ])

            // Clean up the CI image to avoid disk bloat
            sh "docker rmi ${IMAGE_NAME}:${BUILD_NUMBER} || true"
        }

        success {
            slackSend(
                channel: env.SLACK_CHANNEL,
                color: 'good',
                message: """
                    *BUILD PASSED* :white_check_mark:
                    *Job:* ${env.JOB_NAME}
                    *Build:* #${env.BUILD_NUMBER}
                    *Allure Report:* ${env.BUILD_URL}Allure_Report/
                """.stripIndent().trim()
            )

            mail(
                to: 'nksarps@gmail.com',
                subject: "PASSED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build passed.\n\nJob: ${env.JOB_NAME}\nBuild: #${env.BUILD_NUMBER}\nAllure Report: ${env.BUILD_URL}Allure_Report/"
            )
        }

        failure {
            slackSend(
                channel: env.SLACK_CHANNEL,
                color: 'danger',
                message: """
                    *BUILD FAILED* :x:
                    *Job:* ${env.JOB_NAME}
                    *Build:* #${env.BUILD_NUMBER}
                    *Logs:* ${env.BUILD_URL}console
                    *Allure Report:* ${env.BUILD_URL}Allure_Report/
                """.stripIndent().trim()
            )

            mail(
                to: 'nksarps@gmail.com',
                subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed.\n\nJob: ${env.JOB_NAME}\nBuild: #${env.BUILD_NUMBER}\nLogs: ${env.BUILD_URL}console\nAllure Report: ${env.BUILD_URL}Allure_Report/"
            )
        }

    }
}
