pipeline {
    agent any
    
    environment {
        // watsonx.ai Configuration
        WATSONX_API_KEY = credentials('watsonx-api-key')
        WATSONX_PROJECT_ID = credentials('watsonx-project-id')
        WATSONX_API_URL = 'https://us-south.ml.cloud.ibm.com'
        
        // Quality Gate Thresholds
        CODE_QUALITY_THRESHOLD = '70'
        SECURITY_THRESHOLD = '80'
        MAINTAINABILITY_THRESHOLD = '75'
        
        // Build Configuration
        BUILD_TIMESTAMP = sh(script: "date +%Y%m%d-%H%M%S", returnStdout: true).trim()
    }
    
    parameters {
        choice(name: 'REVIEW_DEPTH', choices: ['QUICK', 'STANDARD', 'COMPREHENSIVE'], description: 'Code review depth')
        booleanParam(name: 'SKIP_QUALITY_GATE', defaultValue: false, description: 'Skip quality gate (not recommended)')
        string(name: 'CUSTOM_THRESHOLD', defaultValue: '', description: 'Override quality threshold (0-100)')
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "🔄 Checking out code..."
                    checkout scm
                    
                    // Get commit information
                    env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.GIT_COMMIT_MSG = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                    env.GIT_AUTHOR = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
                }
            }
        }
        
        stage('Pre-Build Analysis') {
            steps {
                script {
                    echo "📊 Running pre-build analysis..."
                    
                    // Count files and lines of code
                    sh '''
                        echo "Files changed in this commit:"
                        git diff --name-only HEAD~1 HEAD || echo "Initial commit"
                        
                        echo "\nTotal lines of code:"
                        find . -name "*.java" -o -name "*.py" -o -name "*.js" -o -name "*.ts" | xargs wc -l | tail -1 || echo "0"
                    '''
                }
            }
        }
        
        stage('watsonx.ai Code Review') {
            steps {
                script {
                    echo "🤖 Initiating watsonx.ai Agent Code Review..."
                    
                    // Prepare code review request
                    def reviewDepth = params.REVIEW_DEPTH
                    
                    // Call watsonx.ai agent for code review
                    def reviewResult = sh(
                        script: """
                            python3 scripts/watsonx_code_review.py \
                                --api-key "\${WATSONX_API_KEY}" \
                                --project-id "\${WATSONX_PROJECT_ID}" \
                                --api-url "\${WATSONX_API_URL}" \
                                --review-depth "${reviewDepth}" \
                                --commit "\${GIT_COMMIT_SHORT}" \
                                --output-file "review-report.json"
                        """,
                        returnStatus: true
                    )
                    
                    if (reviewResult != 0) {
                        error("❌ watsonx.ai code review failed")
                    }
                    
                    // Parse review results
                    def reviewData = readJSON file: 'review-report.json'
                    env.CODE_QUALITY_SCORE = reviewData.scores.code_quality
                    env.SECURITY_SCORE = reviewData.scores.security
                    env.MAINTAINABILITY_SCORE = reviewData.scores.maintainability
                    env.OVERALL_SCORE = reviewData.scores.overall
                    
                    echo "📈 Review Scores:"
                    echo "  - Code Quality: ${env.CODE_QUALITY_SCORE}/100"
                    echo "  - Security: ${env.SECURITY_SCORE}/100"
                    echo "  - Maintainability: ${env.MAINTAINABILITY_SCORE}/100"
                    echo "  - Overall: ${env.OVERALL_SCORE}/100"
                    
                    // Archive review report
                    archiveArtifacts artifacts: 'review-report.json', fingerprint: true
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    echo "🚦 Evaluating Quality Gate..."
                    
                    if (params.SKIP_QUALITY_GATE) {
                        echo "⚠️  Quality Gate SKIPPED by user request"
                        env.QUALITY_GATE_STATUS = 'SKIPPED'
                        return
                    }
                    
                    // Determine thresholds
                    def codeThreshold = params.CUSTOM_THRESHOLD ? params.CUSTOM_THRESHOLD.toInteger() : CODE_QUALITY_THRESHOLD.toInteger()
                    def secThreshold = SECURITY_THRESHOLD.toInteger()
                    def maintThreshold = MAINTAINABILITY_THRESHOLD.toInteger()
                    
                    // Evaluate quality gate
                    def qualityGateResult = sh(
                        script: """
                            python3 scripts/quality_gate.py \
                                --code-quality-score ${env.CODE_QUALITY_SCORE} \
                                --security-score ${env.SECURITY_SCORE} \
                                --maintainability-score ${env.MAINTAINABILITY_SCORE} \
                                --code-threshold ${codeThreshold} \
                                --security-threshold ${secThreshold} \
                                --maintainability-threshold ${maintThreshold} \
                                --output-file "quality-gate-result.json"
                        """,
                        returnStatus: true
                    )
                    
                    def gateData = readJSON file: 'quality-gate-result.json'
                    env.QUALITY_GATE_STATUS = gateData.status
                    env.QUALITY_GATE_MESSAGE = gateData.message
                    
                    echo "Quality Gate Result: ${env.QUALITY_GATE_STATUS}"
                    echo "Message: ${env.QUALITY_GATE_MESSAGE}"
                    
                    if (env.QUALITY_GATE_STATUS == 'FAILED') {
                        echo "❌ Quality Gate FAILED"
                        echo "\nFailed Criteria:"
                        gateData.failed_criteria.each { criterion ->
                            echo "  - ${criterion}"
                        }
                        error("Quality Gate Failed - Build cannot proceed")
                    } else if (env.QUALITY_GATE_STATUS == 'WARNING') {
                        echo "⚠️  Quality Gate PASSED with warnings"
                        unstable(message: "Quality Gate passed with warnings")
                    } else {
                        echo "✅ Quality Gate PASSED"
                    }
                    
                    archiveArtifacts artifacts: 'quality-gate-result.json', fingerprint: true
                }
            }
        }
        
        stage('Build') {
            when {
                expression { env.QUALITY_GATE_STATUS != 'FAILED' }
            }
            steps {
                script {
                    echo "🔨 Building application..."
                    
                    // Example build commands (customize based on your project)
                    sh '''
                        # Java/Maven example
                        # mvn clean package -DskipTests
                        
                        # Node.js example
                        # npm install && npm run build
                        
                        # Python example
                        # pip install -r requirements.txt
                        # python setup.py build
                        
                        echo "Build completed successfully"
                    '''
                }
            }
        }
        
        stage('Test') {
            when {
                expression { env.QUALITY_GATE_STATUS != 'FAILED' }
            }
            steps {
                script {
                    echo "🧪 Running tests..."
                    
                    sh '''
                        # Run your test suite
                        # mvn test
                        # npm test
                        # pytest
                        
                        echo "Tests completed"
                    '''
                }
            }
        }
        
        stage('Generate Report') {
            steps {
                script {
                    echo "📄 Generating comprehensive report..."
                    
                    sh """
                        python3 scripts/generate_report.py \
                            --review-file review-report.json \
                            --quality-gate-file quality-gate-result.json \
                            --commit ${env.GIT_COMMIT_SHORT} \
                            --author "${env.GIT_AUTHOR}" \
                            --output-file pipeline-report.html
                    """
                    
                    // Archive the HTML report as an artifact
                    // Note: To view HTML reports in Jenkins UI, install the "HTML Publisher Plugin"
                    // For now, the report is available as a downloadable artifact
                    archiveArtifacts artifacts: 'pipeline-report.html', fingerprint: true
                    echo "📊 Report archived as artifact: pipeline-report.html"
                    echo "💡 Tip: Install 'HTML Publisher Plugin' to view reports directly in Jenkins UI"
                }
            }
        }
    }
    
    post {
        success {
            script {
                echo "✅ Pipeline completed successfully!"
                echo "Overall Score: ${env.OVERALL_SCORE}/100"
                echo "Quality Gate: ${env.QUALITY_GATE_STATUS}"
                
                // Send notification (customize as needed)
                // emailext subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                //          body: "Quality Score: ${env.OVERALL_SCORE}/100\nQuality Gate: ${env.QUALITY_GATE_STATUS}",
                //          to: "${env.GIT_AUTHOR}@company.com"
            }
        }
        
        failure {
            script {
                echo "❌ Pipeline failed!"
                echo "Quality Gate: ${env.QUALITY_GATE_STATUS}"
                
                // Send failure notification
                // emailext subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                //          body: "Quality Gate: ${env.QUALITY_GATE_STATUS}\nCheck console output for details.",
                //          to: "${env.GIT_AUTHOR}@company.com"
            }
        }
        
        unstable {
            script {
                echo "⚠️  Pipeline completed with warnings"
                echo "Quality Gate: ${env.QUALITY_GATE_STATUS}"
            }
        }
        
        always {
            script {
                echo "🧹 Cleaning up..."
                // Archive all artifacts
                archiveArtifacts artifacts: '*.json,*.html', allowEmptyArchive: true
                
                // Clean workspace if needed
                // cleanWs()
            }
        }
    }
}