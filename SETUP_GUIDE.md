# Jenkins-WatsonX Pipeline Setup Guide

This guide will help you set up Jenkins with WatsonX AI for automated code quality checks and reviews.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Jenkins Configuration](#jenkins-configuration)
3. [WatsonX API Setup](#watsonx-api-setup)
4. [Pipeline Installation](#pipeline-installation)
5. [Testing the Pipeline](#testing-the-pipeline)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software
- **Jenkins** 2.400+ with the following plugins:
  - Pipeline Plugin
  - Git Plugin
  - Credentials Plugin
  - HTML Publisher Plugin
  - Workspace Cleanup Plugin
  
- **Python** 3.8 or higher
- **Git** 2.0 or higher
- **IBM Cloud Account** with WatsonX access

### Required Credentials
- WatsonX API Key
- WatsonX Project ID
- Git repository access

---

## Jenkins Configuration

### 1. Install Required Jenkins Plugins

Navigate to **Manage Jenkins** → **Manage Plugins** → **Available** and install:

```
- Pipeline
- Git
- Credentials Binding
- HTML Publisher
- Workspace Cleanup
```

### 2. Configure Jenkins Credentials

Go to **Manage Jenkins** → **Manage Credentials** → **(global)** → **Add Credentials**

#### Add WatsonX API Key
- **Kind**: Secret text
- **Scope**: Global
- **Secret**: `<your-watsonx-api-key>`
- **ID**: `watsonx-api-key`
- **Description**: WatsonX API Key

#### Add WatsonX Project ID
- **Kind**: Secret text
- **Scope**: Global
- **Secret**: `<your-watsonx-project-id>`
- **ID**: `watsonx-project-id`
- **Description**: WatsonX Project ID

### 3. Install Python Dependencies on Jenkins Agent

SSH into your Jenkins agent and run:

```bash
# Install Python dependencies
pip3 install -r requirements.txt

# Verify installation
python3 -c "import requests; print('Dependencies installed successfully')"
```

---

## WatsonX API Setup

### 1. Get Your WatsonX Credentials

1. Log in to [IBM Cloud](https://cloud.ibm.com/)
2. Navigate to **WatsonX.ai** service
3. Create or select a project
4. Go to **Manage** → **Access (IAM)** → **API Keys**
5. Create a new API key and save it securely
6. Copy your Project ID from the project settings

### 2. Test WatsonX API Connection

Run this test script to verify your credentials:

```bash
python3 scripts/watsonx_code_review.py \
  --api-key "YOUR_API_KEY" \
  --project-id "YOUR_PROJECT_ID" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "QUICK" \
  --commit "HEAD" \
  --output-file "test-review.json"
```

---

## Pipeline Installation

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd jenkins-watsonx-pipeline
```

### 2. Create Jenkins Pipeline Job

1. Go to Jenkins Dashboard
2. Click **New Item**
3. Enter job name: `watsonx-code-review`
4. Select **Pipeline**
5. Click **OK**

### 3. Configure Pipeline

In the Pipeline configuration:

#### General Settings
- ✅ **GitHub project**: `<your-repo-url>`
- ✅ **This project is parameterized**
  - Add Choice Parameter: `REVIEW_DEPTH` (QUICK, STANDARD, COMPREHENSIVE)
  - Add Boolean Parameter: `SKIP_QUALITY_GATE` (default: false)
  - Add String Parameter: `CUSTOM_THRESHOLD` (default: empty)

#### Build Triggers
- ✅ **Poll SCM**: `H/5 * * * *` (every 5 minutes)
- ✅ **GitHub hook trigger for GITScm polling** (if using GitHub webhooks)

#### Pipeline Definition
- **Definition**: Pipeline script from SCM
- **SCM**: Git
- **Repository URL**: `<your-repo-url>`
- **Credentials**: Select your Git credentials
- **Branch Specifier**: `*/main` (or your default branch)
- **Script Path**: `Jenkinsfile`

### 4. Save and Build

Click **Save** and then **Build Now** to test the pipeline.

---

## Testing the Pipeline

### 1. Manual Test Run

```bash
# Make a code change
echo "# Test change" >> README.md
git add README.md
git commit -m "Test: Trigger pipeline"
git push origin main
```

### 2. Monitor Pipeline Execution

1. Go to your Jenkins job
2. Click on the latest build number
3. Click **Console Output** to see logs
4. Check **Pipeline Report** for detailed results

### 3. Verify Quality Gate

The pipeline will:
1. ✅ Checkout code
2. ✅ Run pre-build analysis
3. ✅ Call WatsonX for code review
4. ✅ Evaluate quality gate
5. ✅ Build (if quality gate passes)
6. ✅ Run tests
7. ✅ Generate HTML report

---

## Configuration Customization

### Adjust Quality Thresholds

Edit `config/quality-thresholds.json`:

```json
{
  "thresholds": {
    "code_quality": {
      "value": 70,
      "severity": "HIGH"
    },
    "security": {
      "value": 80,
      "severity": "CRITICAL"
    },
    "maintainability": {
      "value": 75,
      "severity": "MEDIUM"
    }
  }
}
```

### Modify WatsonX Settings

Edit `config/watsonx-config.json`:

```json
{
  "model": {
    "id": "ibm/granite-13b-chat-v2",
    "parameters": {
      "temperature": 0.3,
      "max_new_tokens": 2000
    }
  }
}
```

### Update Jenkinsfile Environment Variables

Edit `Jenkinsfile` lines 10-13:

```groovy
environment {
    CODE_QUALITY_THRESHOLD = '70'
    SECURITY_THRESHOLD = '80'
    MAINTAINABILITY_THRESHOLD = '75'
}
```

---

## Troubleshooting

### Issue: "Python not found"

**Solution**: Install Python on Jenkins agent
```bash
# Ubuntu/Debian
sudo apt-get update && sudo apt-get install python3 python3-pip

# RHEL/CentOS
sudo yum install python3 python3-pip
```

### Issue: "WatsonX API authentication failed"

**Solution**: Verify credentials
```bash
# Test API key
curl -X POST "https://us-south.ml.cloud.ibm.com/ml/v1/text/generation?version=2023-05-29" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json"
```

### Issue: "Quality gate always fails"

**Solution**: Check thresholds and scores
```bash
# Review the quality gate result
cat quality-gate-result.json

# Adjust thresholds in Jenkinsfile or use CUSTOM_THRESHOLD parameter
```

### Issue: "Git diff fails on first commit"

**Solution**: This is expected. The pipeline handles initial commits gracefully.

### Issue: "Module 'requests' not found"

**Solution**: Install Python dependencies
```bash
pip3 install -r requirements.txt
```

---

## Advanced Configuration

### Email Notifications

Uncomment email sections in `Jenkinsfile` (lines 241-255):

```groovy
emailext subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
         body: "Quality Score: ${env.OVERALL_SCORE}/100",
         to: "${env.GIT_AUTHOR}@company.com"
```

### Slack Integration

Install Slack Notification Plugin and add:

```groovy
slackSend channel: '#builds',
          color: 'good',
          message: "Build ${env.BUILD_NUMBER} passed with score ${env.OVERALL_SCORE}/100"
```

### Custom Build Steps

Add your build commands in the Build stage (lines 168-180):

```groovy
stage('Build') {
    steps {
        script {
            sh 'mvn clean package'  // Java
            // sh 'npm run build'   // Node.js
            // sh 'python setup.py build'  // Python
        }
    }
}
```

---

## Best Practices

1. **Start with STANDARD review depth** - Balance between speed and thoroughness
2. **Set realistic thresholds** - Begin with lenient thresholds and gradually increase
3. **Monitor API usage** - WatsonX has rate limits; adjust polling frequency accordingly
4. **Archive artifacts** - Keep review reports for compliance and tracking
5. **Use branch protection** - Require quality gate to pass before merging
6. **Regular updates** - Keep Jenkins plugins and Python dependencies updated

---

## Support and Resources

- **Jenkins Documentation**: https://www.jenkins.io/doc/
- **WatsonX Documentation**: https://www.ibm.com/docs/en/watsonx-as-a-service
- **Project Issues**: Create an issue in your repository
- **IBM Cloud Support**: https://cloud.ibm.com/unifiedsupport

---

## Next Steps

1. ✅ Complete this setup guide
2. ✅ Run a test build
3. ✅ Review the generated reports
4. ✅ Adjust thresholds based on your needs
5. ✅ Integrate with your CI/CD workflow
6. ✅ Train your team on the new process

---

**Last Updated**: 2026-01-22
**Version**: 1.0