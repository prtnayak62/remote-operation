# Jenkins Setup - Step by Step (Starting from Your Code)

You already have your Java code. Now let's set up Jenkins to automatically check it!

---

## ✅ What You Already Have (Step 1-2)

- ✅ Step 1: Your Java project code
- ✅ Step 2: Code is in Git repository

---

## 🚀 STEP 3: Add Pipeline Files to Your Java Project

### 3.1: Copy These Files to Your Java Project

Copy these files from this repository to your Java project:

```
your-java-project/
├── src/                          (your existing Java code)
├── pom.xml                       (your existing Maven file)
├── Jenkinsfile                   ← COPY THIS
├── requirements.txt              ← COPY THIS
├── scripts/                      ← COPY THIS FOLDER
│   ├── watsonx_code_review.py
│   ├── quality_gate.py
│   └── generate_report.py
└── config/                       ← COPY THIS FOLDER
    ├── watsonx-config.json
    └── quality-thresholds.json
```

### 3.2: Commit These Files to Git

```bash
# Go to your Java project directory
cd your-java-project

# Add all new files
git add Jenkinsfile
git add requirements.txt
git add scripts/
git add config/

# Commit
git commit -m "Added Jenkins pipeline with WatsonX integration"

# Push to Git
git push origin main
```

**✅ Done! Your code now has Jenkins pipeline files.**

---

## 🔧 STEP 4: Install Python on Jenkins Server

Jenkins needs Python to run the WatsonX scripts.

### 4.1: Login to Jenkins Server

```bash
# SSH to your Jenkins server (or use the server directly)
ssh jenkins-server
```

### 4.2: Install Python and Dependencies

```bash
# Install Python 3
sudo apt-get update
sudo apt-get install python3 python3-pip

# Verify Python is installed
python3 --version
# Should show: Python 3.8.x or higher

# Install required Python packages
pip3 install requests python-dateutil

# Verify installation
python3 -c "import requests; print('✓ Python packages installed')"
```

**✅ Done! Jenkins can now run Python scripts.**

---

## 🔑 STEP 5: Add WatsonX Credentials to Jenkins

Jenkins needs your WatsonX API credentials.

### 5.1: Get Your WatsonX Credentials

1. Go to [IBM Cloud](https://cloud.ibm.com/)
2. Login to your account
3. Go to **WatsonX.ai** service
4. Click on your project
5. Copy these two values:
   - **API Key** (looks like: `abc123xyz456...`)
   - **Project ID** (looks like: `12345678-abcd-1234-abcd-123456789abc`)

### 5.2: Add Credentials in Jenkins

**Step 5.2.1: Open Jenkins**
- Open your browser
- Go to: `http://your-jenkins-url:8080`
- Login with your Jenkins username and password

**Step 5.2.2: Go to Credentials**
- Click **Manage Jenkins** (left sidebar)
- Click **Manage Credentials**
- Click **(global)** domain
- Click **Add Credentials**

**Step 5.2.3: Add WatsonX API Key**
- Kind: **Secret text**
- Scope: **Global**
- Secret: Paste your WatsonX API Key (the long string)
- ID: Type exactly: `watsonx-api-key`
- Description: Type: `WatsonX API Key`
- Click **OK**

**Step 5.2.4: Add WatsonX Project ID**
- Click **Add Credentials** again
- Kind: **Secret text**
- Scope: **Global**
- Secret: Paste your WatsonX Project ID
- ID: Type exactly: `watsonx-project-id`
- Description: Type: `WatsonX Project ID`
- Click **OK**

**✅ Done! Jenkins now has WatsonX credentials.**

---

## 📋 STEP 6: Create Jenkins Pipeline Job

Now create the Jenkins job that will run your pipeline.

### 6.1: Create New Job

1. Go to Jenkins Dashboard
2. Click **New Item** (top left)
3. Enter name: `my-java-project` (or your project name)
4. Select: **Pipeline**
5. Click **OK**

### 6.2: Configure General Settings

In the job configuration page:

**Description:**
```
Automated code review with WatsonX AI for Java project
```

**Check these boxes:**
- ✅ **GitHub project** (if using GitHub)
  - Project url: `https://github.com/your-username/your-java-project`

### 6.3: Configure Build Triggers

Scroll down to **Build Triggers** section:

**Check this box:**
- ✅ **Poll SCM**
  - Schedule: `H/5 * * * *`
  - (This checks Git every 5 minutes for new commits)

**OR if you have GitHub webhook:**
- ✅ **GitHub hook trigger for GITScm polling**

### 6.4: Configure Pipeline

Scroll down to **Pipeline** section:

**Definition:** Select `Pipeline script from SCM`

**SCM:** Select `Git`

**Repository URL:** 
```
https://github.com/your-username/your-java-project
```
(Replace with your actual Git repository URL)

**Credentials:** 
- If public repository: Select `- none -`
- If private repository: Add your Git credentials

**Branches to build:**
```
*/main
```
(Or `*/master` if your default branch is master)

**Script Path:**
```
Jenkinsfile
```

### 6.5: Save Configuration

- Click **Save** button at the bottom

**✅ Done! Jenkins job is created.**

---

## 🎯 STEP 7: Test Your Pipeline

Now let's test if everything works!

### 7.1: Trigger First Build Manually

1. Go to your Jenkins job page
2. Click **Build Now** (left sidebar)
3. Watch the build progress

### 7.2: Watch the Console Output

1. Click on the build number (e.g., #1)
2. Click **Console Output**
3. You will see:

```
Started by user admin
Checking out code...
✓ Code checked out

Running watsonx.ai Code Review...
📁 Analyzing changed files...
   Found 3 changed files
🔍 Analyzing code with watsonx.ai...
✅ Review report generated

📈 Review Scores:
  - Code Quality: 75/100
  - Security: 85/100
  - Maintainability: 80/100
  - Overall: 80/100

🚦 Evaluating Quality Gate...
✅ Quality Gate PASSED

🔨 Building application...
[INFO] Building jar: target/myapp.jar
[INFO] BUILD SUCCESS

🧪 Running tests...
Tests run: 10, Failures: 0, Errors: 0

✅ Pipeline completed successfully!
```

### 7.3: Check the Report

1. Go back to the build page
2. Click **Pipeline Report** link
3. See detailed code review results

**✅ Done! Your first build is complete.**

---

## 🔄 STEP 8: Automatic Builds (From Now On)

Now every time you commit Java code, Jenkins will automatically run!

### 8.1: Make a Code Change

Edit your Java file:
```java
// src/main/java/com/example/App.java
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World - Updated!");
    }
}
```

### 8.2: Commit and Push

```bash
git add .
git commit -m "Updated App.java"
git push origin main
```

### 8.3: Jenkins Automatically Runs

Within 5 minutes:
1. Jenkins detects your commit
2. Automatically starts a new build
3. Runs WatsonX code review
4. Checks quality gate
5. Builds your Java code
6. Runs tests
7. Shows results

**You don't need to do anything! It's automatic!** 🎉

---

## 📊 Understanding the Results

### When Build SUCCEEDS ✅

```
Console Output:
✅ Quality Gate PASSED
✅ BUILD SUCCESS

What this means:
- Your code quality is good (above threshold)
- Your code is secure (above threshold)
- Your code is maintainable (above threshold)
- Java code compiled successfully
- All tests passed
```

### When Build FAILS ❌

```
Console Output:
❌ Quality Gate FAILED
  - Code Quality: 65/100 (threshold: 70)
  - Security: 75/100 (threshold: 80)
❌ BUILD STOPPED

What this means:
- Your code quality is below minimum required
- You must fix the issues
- Build did not continue
- No JAR file was created
```

**What to do:**
1. Look at the issues in the report
2. Fix your Java code
3. Commit again
4. Jenkins will run again automatically

---

## 🎛️ Adjusting Quality Thresholds

If builds are failing too often, you can adjust thresholds.

### Option 1: Edit Jenkinsfile (Permanent Change)

Edit `Jenkinsfile` in your project:

```groovy
environment {
    CODE_QUALITY_THRESHOLD = '70'      // Change this
    SECURITY_THRESHOLD = '80'          // Change this
    MAINTAINABILITY_THRESHOLD = '75'   // Change this
}
```

**To make easier (more lenient):**
```groovy
CODE_QUALITY_THRESHOLD = '60'      // Lower = easier to pass
SECURITY_THRESHOLD = '70'
MAINTAINABILITY_THRESHOLD = '65'
```

**To make stricter (harder to pass):**
```groovy
CODE_QUALITY_THRESHOLD = '85'      // Higher = harder to pass
SECURITY_THRESHOLD = '90'
MAINTAINABILITY_THRESHOLD = '85'
```

Commit the change:
```bash
git add Jenkinsfile
git commit -m "Adjusted quality thresholds"
git push origin main
```

### Option 2: Use Build Parameter (One-time Change)

1. Go to Jenkins job
2. Click **Build with Parameters**
3. Enter **CUSTOM_THRESHOLD**: `65` (or any value)
4. Click **Build**

This only affects this one build.

---

## 🔍 Troubleshooting

### Problem: "Python not found"

**Solution:**
```bash
# On Jenkins server
sudo apt-get install python3 python3-pip
```

### Problem: "WatsonX API authentication failed"

**Solution:**
1. Check your API key is correct
2. Go to Jenkins → Manage Credentials
3. Edit `watsonx-api-key` credential
4. Make sure the secret is correct (no extra spaces)

### Problem: "Quality gate always fails"

**Solution:**
1. Check what scores you're getting (in console output)
2. Lower the thresholds in Jenkinsfile
3. Or improve your Java code quality

### Problem: "Build not triggering automatically"

**Solution:**
1. Check Jenkins job configuration
2. Make sure "Poll SCM" is enabled with schedule `H/5 * * * *`
3. Or set up GitHub webhook for instant triggers

---

## ✅ Summary Checklist

- [ ] Step 3: Added pipeline files to Java project ✓
- [ ] Step 4: Installed Python on Jenkins server ✓
- [ ] Step 5: Added WatsonX credentials to Jenkins ✓
- [ ] Step 6: Created Jenkins pipeline job ✓
- [ ] Step 7: Tested first build successfully ✓
- [ ] Step 8: Automatic builds working ✓

**Once all checked, you're done! Jenkins will now automatically review your Java code with WatsonX AI every time you commit!** 🎉

---

## 📞 Need Help?

- **Console shows errors?** → Check the error message and see Troubleshooting section
- **Want to change thresholds?** → See "Adjusting Quality Thresholds" section
- **Want more details?** → See SIMPLE_GUIDE.md for complete explanation

**Your Jenkins + WatsonX pipeline is ready!** 🚀