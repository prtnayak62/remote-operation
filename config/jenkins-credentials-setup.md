# Jenkins Credentials Setup Guide

This guide walks you through setting up the required credentials in Jenkins for the watsonx.ai integration.

## Required Credentials

You need to configure two credentials in Jenkins:

1. **watsonx-api-key**: Your IBM watsonx.ai API key
2. **watsonx-project-id**: Your IBM watsonx.ai Project ID

## Step-by-Step Setup

### 1. Access Jenkins Credentials Manager

1. Log in to your Jenkins instance
2. Navigate to: **Jenkins → Manage Jenkins → Credentials**
3. Click on **(global)** domain
4. Click **Add Credentials**

### 2. Add watsonx.ai API Key

1. **Kind**: Select `Secret text`
2. **Scope**: Select `Global (Jenkins, nodes, items, all child items, etc)`
3. **Secret**: Paste your watsonx.ai API key
4. **ID**: Enter `watsonx-api-key` (must match exactly)
5. **Description**: Enter `watsonx.ai API Key for Code Review`
6. Click **OK**

### 3. Add watsonx.ai Project ID

1. Click **Add Credentials** again
2. **Kind**: Select `Secret text`
3. **Scope**: Select `Global (Jenkins, nodes, items, all child items, etc)`
4. **Secret**: Paste your watsonx.ai Project ID
5. **ID**: Enter `watsonx-project-id` (must match exactly)
6. **Description**: Enter `watsonx.ai Project ID for Code Review`
7. Click **OK**

## How to Get Your watsonx.ai Credentials

### Getting Your API Key

1. Log in to [IBM Cloud](https://cloud.ibm.com/)
2. Navigate to **Manage → Access (IAM)**
3. Click **API keys** in the left sidebar
4. Click **Create an IBM Cloud API key**
5. Enter a name (e.g., "Jenkins Pipeline")
6. Click **Create**
7. **Important**: Copy and save the API key immediately (you won't be able to see it again)

### Getting Your Project ID

1. Log in to [watsonx.ai](https://dataplatform.cloud.ibm.com/wx/home)
2. Navigate to your project
3. Click on the **Manage** tab
4. Find **Project ID** in the General section
5. Copy the Project ID

## Verification

After adding credentials, verify they are set up correctly:

1. Go to **Jenkins → Credentials → System → Global credentials**
2. You should see two entries:
   - `watsonx-api-key` (Secret text)
   - `watsonx-project-id` (Secret text)

## Security Best Practices

### ✅ DO:
- Use Jenkins credentials binding
- Rotate API keys regularly (every 90 days)
- Limit credential access to specific jobs if possible
- Use separate API keys for different environments
- Monitor API key usage in IBM Cloud

### ❌ DON'T:
- Hardcode credentials in Jenkinsfile
- Commit credentials to version control
- Share API keys between teams
- Use production credentials in test environments
- Log or print credential values

## Troubleshooting

### Issue: "Credentials not found"

**Solution**: 
- Verify credential IDs match exactly: `watsonx-api-key` and `watsonx-project-id`
- Check credentials are in the Global domain
- Ensure credentials are of type "Secret text"

### Issue: "Authentication failed"

**Solution**:
- Verify API key is valid and not expired
- Check API key has necessary permissions
- Ensure Project ID is correct
- Test credentials manually using curl:

```bash
curl -X POST "https://iam.cloud.ibm.com/identity/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=YOUR_API_KEY"
```

### Issue: "Project not found"

**Solution**:
- Verify Project ID is correct
- Ensure API key has access to the project
- Check project exists in watsonx.ai

## Alternative: Using Jenkins Configuration as Code (JCasC)

If you use Jenkins Configuration as Code, you can define credentials in YAML:

```yaml
credentials:
  system:
    domainCredentials:
      - credentials:
          - string:
              id: "watsonx-api-key"
              secret: "${WATSONX_API_KEY}"
              scope: GLOBAL
              description: "watsonx.ai API Key"
          - string:
              id: "watsonx-project-id"
              secret: "${WATSONX_PROJECT_ID}"
              scope: GLOBAL
              description: "watsonx.ai Project ID"
```

**Note**: Store actual values in environment variables or external secret management.

## Using External Secret Management

For enhanced security, integrate with external secret managers:

### HashiCorp Vault

```groovy
withCredentials([
    string(credentialsId: 'vault-token', variable: 'VAULT_TOKEN')
]) {
    env.WATSONX_API_KEY = sh(
        script: "vault kv get -field=api_key secret/watsonx",
        returnStdout: true
    ).trim()
}
```

### AWS Secrets Manager

```groovy
withAWS(credentials: 'aws-credentials') {
    env.WATSONX_API_KEY = sh(
        script: "aws secretsmanager get-secret-value --secret-id watsonx-api-key --query SecretString --output text",
        returnStdout: true
    ).trim()
}
```

### Azure Key Vault

```groovy
withCredentials([azureServicePrincipal('azure-sp')]) {
    env.WATSONX_API_KEY = sh(
        script: "az keyvault secret show --name watsonx-api-key --vault-name my-vault --query value -o tsv",
        returnStdout: true
    ).trim()
}
```

## Credential Rotation

Set up a reminder to rotate credentials regularly:

1. Create new API key in IBM Cloud
2. Update Jenkins credential with new key
3. Test pipeline with new credential
4. Delete old API key from IBM Cloud
5. Document rotation in change log

## Support

For issues with:
- **Jenkins credentials**: Contact your Jenkins administrator
- **IBM Cloud API keys**: Check [IBM Cloud Documentation](https://cloud.ibm.com/docs/account?topic=account-userapikey)
- **watsonx.ai access**: Check [watsonx.ai Documentation](https://www.ibm.com/docs/en/watsonx-as-a-service)

---

**Last Updated**: 2026-01-22