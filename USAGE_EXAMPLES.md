# Usage Examples - Jenkins WatsonX Pipeline

This document provides practical examples of using the Jenkins-WatsonX pipeline for code quality checks.

## Table of Contents
1. [Basic Usage](#basic-usage)
2. [Script Examples](#script-examples)
3. [Pipeline Parameters](#pipeline-parameters)
4. [API Integration Examples](#api-integration-examples)
5. [Common Scenarios](#common-scenarios)

---

## Basic Usage

### Running the Pipeline from Jenkins UI

1. Navigate to your Jenkins job
2. Click **Build with Parameters**
3. Select options:
   - **REVIEW_DEPTH**: Choose QUICK, STANDARD, or COMPREHENSIVE
   - **SKIP_QUALITY_GATE**: Leave unchecked (recommended)
   - **CUSTOM_THRESHOLD**: Leave empty or set custom value (0-100)
4. Click **Build**

### Triggering via Git Push

```bash
# Make changes to your code
git add .
git commit -m "feat: Add new feature"
git push origin main

# Pipeline will automatically trigger
```

---

## Script Examples

### 1. Running WatsonX Code Review Manually

#### Quick Review
```bash
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "QUICK" \
  --commit "HEAD" \
  --output-file "review-report.json"
```

#### Standard Review
```bash
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "STANDARD" \
  --commit "HEAD" \
  --output-file "review-report.json"
```

#### Comprehensive Review
```bash
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "COMPREHENSIVE" \
  --commit "HEAD" \
  --output-file "review-report.json"
```

#### Review Specific Commit
```bash
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "STANDARD" \
  --commit "abc123def" \
  --output-file "review-abc123.json"
```

### 2. Running Quality Gate Evaluation

#### Basic Quality Gate Check
```bash
python3 scripts/quality_gate.py \
  --code-quality-score 75 \
  --security-score 85 \
  --maintainability-score 80 \
  --output-file "quality-gate-result.json"
```

#### With Custom Thresholds
```bash
python3 scripts/quality_gate.py \
  --code-quality-score 75 \
  --security-score 85 \
  --maintainability-score 80 \
  --code-threshold 70 \
  --security-threshold 80 \
  --maintainability-threshold 75 \
  --output-file "quality-gate-result.json"
```

#### Strict Quality Gate
```bash
python3 scripts/quality_gate.py \
  --code-quality-score 88 \
  --security-score 92 \
  --maintainability-score 87 \
  --code-threshold 85 \
  --security-threshold 90 \
  --maintainability-threshold 85 \
  --output-file "quality-gate-strict.json"
```

### 3. Generating Reports

```bash
python3 scripts/generate_report.py \
  --review-file review-report.json \
  --quality-gate-file quality-gate-result.json \
  --commit "abc123" \
  --author "John Doe" \
  --output-file "pipeline-report.html"
```

---

## Pipeline Parameters

### REVIEW_DEPTH Options

#### QUICK
- **Use Case**: Fast feedback for small changes
- **Duration**: ~30 seconds
- **Focus**: Critical security issues and bugs
- **Best For**: Hot fixes, minor updates

```groovy
// In Jenkinsfile or when triggering
parameters {
    choice(name: 'REVIEW_DEPTH', choices: ['QUICK'])
}
```

#### STANDARD (Recommended)
- **Use Case**: Regular development workflow
- **Duration**: ~1-2 minutes
- **Focus**: Security, code quality, best practices
- **Best For**: Feature development, regular commits

```groovy
parameters {
    choice(name: 'REVIEW_DEPTH', choices: ['STANDARD'])
}
```

#### COMPREHENSIVE
- **Use Case**: Pre-release, major changes
- **Duration**: ~3-5 minutes
- **Focus**: All aspects including performance and architecture
- **Best For**: Release candidates, major refactoring

```groovy
parameters {
    choice(name: 'REVIEW_DEPTH', choices: ['COMPREHENSIVE'])
}
```

### SKIP_QUALITY_GATE

```groovy
// Skip quality gate (NOT RECOMMENDED)
parameters {
    booleanParam(name: 'SKIP_QUALITY_GATE', defaultValue: false)
}
```

**When to use:**
- Emergency hot fixes (with proper approval)
- Experimental branches
- Documentation-only changes

**Warning**: Skipping quality gate bypasses all checks!

### CUSTOM_THRESHOLD

```groovy
// Override default thresholds
parameters {
    string(name: 'CUSTOM_THRESHOLD', defaultValue: '75')
}
```

**Examples:**
- `'80'` - Stricter requirements
- `'60'` - More lenient (for legacy code)
- `''` - Use default thresholds

---

## API Integration Examples

### 1. Calling WatsonX API Directly

```python
import requests
import json

def call_watsonx_api(api_key, project_id, prompt):
    url = "https://us-south.ml.cloud.ibm.com/ml/v1/text/generation?version=2023-05-29"
    
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model_id": "ibm/granite-13b-chat-v2",
        "input": prompt,
        "parameters": {
            "decoding_method": "greedy",
            "max_new_tokens": 2000,
            "temperature": 0.3
        },
        "project_id": project_id
    }
    
    response = requests.post(url, headers=headers, json=payload, timeout=60)
    response.raise_for_status()
    return response.json()

# Usage
api_key = "your-api-key"
project_id = "your-project-id"
prompt = "Review this code: def hello(): print('Hello')"

result = call_watsonx_api(api_key, project_id, prompt)
print(json.dumps(result, indent=2))
```

### 2. Parsing Review Results

```python
import json

# Load review report
with open('review-report.json', 'r') as f:
    report = json.load(f)

# Extract scores
scores = report['scores']
print(f"Code Quality: {scores['code_quality']}/100")
print(f"Security: {scores['security']}/100")
print(f"Maintainability: {scores['maintainability']}/100")
print(f"Overall: {scores['overall']}/100")

# List critical issues
critical_issues = [
    issue for issue in report['issues'] 
    if issue['severity'] == 'CRITICAL'
]

print(f"\nFound {len(critical_issues)} critical issues:")
for issue in critical_issues:
    print(f"  - {issue['file']}: {issue['message']}")
```

### 3. Custom Quality Gate Logic

```python
def custom_quality_gate(scores, thresholds):
    """
    Custom quality gate with weighted scoring
    """
    weights = {
        'security': 0.5,      # Security is most important
        'code_quality': 0.3,
        'maintainability': 0.2
    }
    
    weighted_score = sum(
        scores[metric] * weights[metric]
        for metric in weights
    )
    
    # Check individual thresholds
    failures = []
    for metric, threshold in thresholds.items():
        if scores[metric] < threshold:
            failures.append(f"{metric}: {scores[metric]} < {threshold}")
    
    # Overall weighted threshold
    if weighted_score < 75:
        failures.append(f"Weighted score: {weighted_score:.1f} < 75")
    
    return {
        'passed': len(failures) == 0,
        'weighted_score': weighted_score,
        'failures': failures
    }

# Usage
scores = {
    'code_quality': 72,
    'security': 85,
    'maintainability': 78
}

thresholds = {
    'code_quality': 70,
    'security': 80,
    'maintainability': 75
}

result = custom_quality_gate(scores, thresholds)
print(f"Quality Gate: {'PASSED' if result['passed'] else 'FAILED'}")
print(f"Weighted Score: {result['weighted_score']:.1f}")
```

---

## Common Scenarios

### Scenario 1: Feature Branch Review

```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes
# ... code changes ...

# Commit and push
git add .
git commit -m "feat: Implement new feature"
git push origin feature/new-feature

# Pipeline runs automatically
# Review results in Jenkins UI
```

### Scenario 2: Pre-Merge Quality Check

```bash
# Before merging to main
git checkout feature/my-feature

# Run local review
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "COMPREHENSIVE" \
  --commit "HEAD" \
  --output-file "pre-merge-review.json"

# Check results
cat pre-merge-review.json | jq '.scores'

# If passed, merge
git checkout main
git merge feature/my-feature
git push origin main
```

### Scenario 3: Emergency Hotfix

```bash
# Create hotfix branch
git checkout -b hotfix/critical-bug

# Fix the bug
# ... code changes ...

# Commit
git add .
git commit -m "fix: Critical security vulnerability"
git push origin hotfix/critical-bug

# Trigger Jenkins with QUICK review
# In Jenkins UI: Build with Parameters
# - REVIEW_DEPTH: QUICK
# - SKIP_QUALITY_GATE: false (still check security!)
```

### Scenario 4: Batch Review Multiple Commits

```bash
#!/bin/bash
# review_commits.sh

COMMITS=("abc123" "def456" "ghi789")

for commit in "${COMMITS[@]}"; do
    echo "Reviewing commit: $commit"
    
    python3 scripts/watsonx_code_review.py \
      --api-key "${WATSONX_API_KEY}" \
      --project-id "${WATSONX_PROJECT_ID}" \
      --api-url "https://us-south.ml.cloud.ibm.com" \
      --review-depth "STANDARD" \
      --commit "$commit" \
      --output-file "review-${commit}.json"
    
    echo "Review complete: review-${commit}.json"
    echo "---"
done

echo "All reviews complete!"
```

### Scenario 5: Comparing Reviews Over Time

```bash
#!/bin/bash
# compare_reviews.sh

# Review current commit
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "STANDARD" \
  --commit "HEAD" \
  --output-file "review-current.json"

# Review previous commit
python3 scripts/watsonx_code_review.py \
  --api-key "${WATSONX_API_KEY}" \
  --project-id "${WATSONX_PROJECT_ID}" \
  --api-url "https://us-south.ml.cloud.ibm.com" \
  --review-depth "STANDARD" \
  --commit "HEAD~1" \
  --output-file "review-previous.json"

# Compare scores
echo "Score Comparison:"
echo "Current:"
cat review-current.json | jq '.scores'
echo "Previous:"
cat review-previous.json | jq '.scores'
```

---

## Integration with CI/CD Tools

### GitHub Actions

```yaml
name: WatsonX Code Review

on: [push, pull_request]

jobs:
  code-review:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.9'
      
      - name: Install dependencies
        run: pip install -r requirements.txt
      
      - name: Run WatsonX Review
        env:
          WATSONX_API_KEY: ${{ secrets.WATSONX_API_KEY }}
          WATSONX_PROJECT_ID: ${{ secrets.WATSONX_PROJECT_ID }}
        run: |
          python3 scripts/watsonx_code_review.py \
            --api-key "$WATSONX_API_KEY" \
            --project-id "$WATSONX_PROJECT_ID" \
            --api-url "https://us-south.ml.cloud.ibm.com" \
            --review-depth "STANDARD" \
            --commit "${{ github.sha }}" \
            --output-file "review-report.json"
      
      - name: Upload Report
        uses: actions/upload-artifact@v3
        with:
          name: review-report
          path: review-report.json
```

### GitLab CI

```yaml
watsonx-review:
  stage: test
  image: python:3.9
  script:
    - pip install -r requirements.txt
    - python3 scripts/watsonx_code_review.py
        --api-key "$WATSONX_API_KEY"
        --project-id "$WATSONX_PROJECT_ID"
        --api-url "https://us-south.ml.cloud.ibm.com"
        --review-depth "STANDARD"
        --commit "$CI_COMMIT_SHA"
        --output-file "review-report.json"
  artifacts:
    paths:
      - review-report.json
    expire_in: 1 week
```

---

## Tips and Best Practices

1. **Start with STANDARD depth** - Good balance of speed and coverage
2. **Use QUICK for rapid iteration** - During active development
3. **Use COMPREHENSIVE before releases** - Catch all potential issues
4. **Never skip quality gate in production** - Only for emergencies
5. **Archive review reports** - Track quality trends over time
6. **Set realistic thresholds** - Based on your codebase maturity
7. **Review failed builds immediately** - Don't let issues accumulate
8. **Use custom thresholds sparingly** - Maintain consistency

---

**Last Updated**: 2026-01-22
**Version**: 1.0