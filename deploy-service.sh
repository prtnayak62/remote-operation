#!/bin/bash
export MSYS2_ARG_CONV_EXCL="*"

# Function to upload configs to EFS
uploadConfigsToEFS() {
    local efsId="$1"
    local env="$2"
    local region="$3"
    
    echo -e "\033[36mSkipping EFS config upload (Windows limitation)\033[0m"
    echo -e "\033[33mEFS is mounted but empty - application will use defaults\033[0m"
    echo -e "\033[37m  Alternative: Bundle configs in container image\033[0m"
    echo -e "\033[37m  Add to Dockerfile: COPY config-samples/ /opt/app/config/\033[0m"
    echo -e "\033[32m  EFS infrastructure ready for future config management\033[0m"
}

# Function to display usage
usage() {
    echo "Usage: $0 -e <environment> -r <region>"
    echo "  -e, --environment    Environment (dev, staging, prod)"
    echo "  -r, --region         AWS Region"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--environment)
            Environment="$2"
            shift 2
            ;;
        -r|--region)
            Region="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Unknown option: $1"
            usage
            ;;
    esac
done

# Validate required parameters
if [[ -z "$Environment" || -z "$Region" ]]; then
    echo "Error: Both environment and region are required"
    usage
fi

# Validate environment
if [[ ! "$Environment" =~ ^(dev|staging|prod)$ ]]; then
    echo "Error: Environment must be one of: dev, staging, prod"
    exit 1
fi

ServiceDir=$(pwd)
ServiceFolderName=$(basename "$ServiceDir")
ConfigFile="config/$Environment/service-config.json"

echo -e "\033[32mDomain-Driven Deployment (version: v1)\033[0m"
echo -e "\033[33mService Folder: $ServiceFolderName\033[0m"
echo -e "\033[33mEnvironment: $Environment\033[0m"
# Set AWS region
export AWS_DEFAULT_REGION="$Region"
echo -e "\033[36mAWS Region set to: $Region\033[0m"

# Load service configuration
if [[ ! -f "$ConfigFile" ]]; then
    echo -e "\033[31mError: Configuration file not found: $ConfigFile\033[0m" >&2
    echo -e "\033[31mRun create-service.sh first to generate configurations\033[0m"
    exit 1
fi

config=$(cat "$ConfigFile")
DomainName=$(echo "$config" | jq -r '.DomainName')
ProductName=$(echo "$config" | jq -r '.ProductName // empty')
ServiceName=$(echo "$config" | jq -r '.ServiceName')
FullServiceName=$(echo "$config" | jq -r '.FullServiceName')

echo -e "\033[33mActual Service Name: $ServiceName\033[0m"

# Display configuration summary
echo -e "\n\033[33mConfiguration Summary:\033[0m"
echo -e "\033[37m  Domain: $DomainName\033[0m"
if [[ -n "$ProductName" && "$ProductName" != "null" ]]; then
    echo -e "\033[37m  Product: $ProductName\033[0m"
fi
ServiceType=$(echo "$config" | jq -r '.ServiceType')
ServicePort=$(echo "$config" | jq -r '.ServicePort')
TaskCpu=$(echo "$config" | jq -r '.TaskCpu')
TaskMemory=$(echo "$config" | jq -r '.TaskMemory')
DesiredCount=$(echo "$config" | jq -r '.DesiredCount')
MinCapacity=$(echo "$config" | jq -r '.MinCapacity')
MaxCapacity=$(echo "$config" | jq -r '.MaxCapacity')

echo -e "\033[37m  Service Type: $ServiceType\033[0m"
echo -e "\033[37m  Service Port: $ServicePort\033[0m"
echo -e "\033[37m  Task CPU: $TaskCpu\033[0m"
echo -e "\033[37m  Task Memory: $TaskMemory\033[0m"
echo -e "\033[37m  Desired Count: $DesiredCount\033[0m"
echo -e "\033[37m  Min/Max Capacity: $MinCapacity/$MaxCapacity\033[0m"

# Extract all configuration values once at the start
echo -e "\n\033[33mResource Configuration:\033[0m"

# Extract resource access values
NeedsMSK=$(echo "$config" | jq -r '.ResourceAccess.NeedsMSK // false' | tr '[:upper:]' '[:lower:]')
NeedsSQS=$(echo "$config" | jq -r '.ResourceAccess.NeedsSQS // false' | tr '[:upper:]' '[:lower:]')
NeedsSNS=$(echo "$config" | jq -r '.ResourceAccess.NeedsSNS // false' | tr '[:upper:]' '[:lower:]')
NeedsDatabase=$(echo "$config" | jq -r '.ResourceAccess.NeedsDatabase // false' | tr '[:upper:]' '[:lower:]')
NeedsRedis=$(echo "$config" | jq -r '.ResourceAccess.NeedsRedis // false' | tr '[:upper:]' '[:lower:]')
NeedsDynamoDB=$(echo "$config" | jq -r '.ResourceAccess.NeedsDynamoDB // false' | tr '[:upper:]' '[:lower:]')
NeedsS3=$(echo "$config" | jq -r '.ResourceAccess.NeedsS3 // false' | tr '[:upper:]' '[:lower:]')
NeedsCouchbase=$(echo "$config" | jq -r '.ResourceAccess.NeedsCouchbase // false' | tr '[:upper:]' '[:lower:]')
NeedsEFS=$(echo "$config" | jq -r '.ResourceAccess.NeedsEFS // false' | tr '[:upper:]' '[:lower:]')
HealthCheckPath=$(echo "$config" | jq -r '.HealthCheckPath')
HealthCheckProtocol=$(echo "$config" | jq -r '.HealthCheckProtocol')

# Extract resource levels
MSKProducerLevel=$(echo "$config" | jq -r '.ResourceLevels.MSKProducerLevel // "foundation"')
MSKConsumerLevel=$(echo "$config" | jq -r '.ResourceLevels.MSKConsumerLevel // "foundation"')

# Extract MSK topic names
ProducerTopicName1=$(echo "$config" | jq -r '.MSKConfig.ProducerTopicName1 // "sample-topic"')
ConsumerTopicName1=$(echo "$config" | jq -r '.MSKConfig.ConsumerTopicName1 // "sample-topic"')
DatabaseLevel=$(echo "$config" | jq -r '.ResourceLevels.DatabaseLevel // "foundation"')
RedisLevel=$(echo "$config" | jq -r '.ResourceLevels.RedisLevel // "foundation"')
S3Level=$(echo "$config" | jq -r '.ResourceLevels.S3Level // "foundation"')
SQSLevel=$(echo "$config" | jq -r '.ResourceLevels.SQSLevel // "foundation"')
SNSLevel=$(echo "$config" | jq -r '.ResourceLevels.SNSLevel // "foundation"')
DynamoDBLevel=$(echo "$config" | jq -r '.ResourceLevels.DynamoDBLevel // "foundation"')
DAXLevel=$(echo "$config" | jq -r '.ResourceLevels.DynamoDBLevel // "foundation"')  # DAX uses same level as DynamoDB
ECSClusterLevel=$(echo "$config" | jq -r '.ResourceLevels.ECSClusterLevel // "domain"')
ServiceConnectLevel=$(echo "$config" | jq -r '.ResourceLevels.ServiceConnectLevel // "domain"')
ECSLogGroupLevel=$(echo "$config" | jq -r '.ResourceLevels.ECSLogGroupLevel // "domain"')

# Extract Service Connect configuration
FoundationServiceConnectNamespaceSSMPath=$(echo "$config" | jq -r '.FoundationServiceConnectNamespaceSSMPath // ""')
FoundationServiceConnectNamespaceArn=""

# Resolve foundation namespace ARN if SSM path is provided
if [[ -n "$FoundationServiceConnectNamespaceSSMPath" && "$FoundationServiceConnectNamespaceSSMPath" != "null" ]]; then
    echo -e "\033[37m  Using Foundation Service Connect Namespace\033[0m"
    FoundationServiceConnectNamespaceArn=$(aws ssm get-parameter --name "$FoundationServiceConnectNamespaceSSMPath" --query 'Parameter.Value' --output text 2>/dev/null)
    if [[ $? -eq 0 && -n "$FoundationServiceConnectNamespaceArn" ]]; then
        echo -e "\033[37m  Foundation Namespace ARN: $FoundationServiceConnectNamespaceArn\033[0m"
    else
        echo -e "\033[33m  Warning: Could not resolve foundation namespace from SSM: $FoundationServiceConnectNamespaceSSMPath\033[0m"
    fi
fi

# Extract Couchbase configuration if needed
if [[ "$NeedsCouchbase" == "true" ]]; then
    CouchbaseBucket=$(echo "$config" | jq -r '.CouchbaseConfig.Bucket // "newgen"')
    CouchbaseScope=$(echo "$config" | jq -r '.CouchbaseConfig.Scope // "dev"')
    CouchbaseCollection=$(echo "$config" | jq -r '.CouchbaseConfig.Collection // ""')
    CouchbaseSecretName=$(echo "$config" | jq -r '.AWSResources.CouchbaseCredentials // "dev/couchbase/autoconnect/newgen/credentials"')
    CouchbaseEndpointParam=$(echo "$config" | jq -r '.AWSResources.CouchbaseEndpoint // "/dev/couchbase/autoconnect/newgen/endpoint"')
    JWTSecretParam=$(echo "$config" | jq -r '.AWSResources.JWTSecret // "/dev/jwt/secret"')
    
    echo -e "\033[37m  Couchbase Enabled: Yes\033[0m"
    echo -e "\033[37m  Couchbase Bucket: $CouchbaseBucket\033[0m"
    echo -e "\033[37m  Couchbase Collection: $CouchbaseCollection\033[0m"
else
    CouchbaseBucket="newgen"
    CouchbaseScope="dev"
    CouchbaseCollection=""
    CouchbaseSecretName="dev/couchbase/autoconnect/newgen/credentials"
    CouchbaseEndpointParam="/dev/couchbase/autoconnect/newgen/endpoint"
    JWTSecretParam="/dev/jwt/secret"
fi

echo -e "\033[37m  Health Check: $HealthCheckProtocol $HealthCheckPath\033[0m"
if [[ "$NeedsMSK" == "true" ]]; then
    echo -e "\033[37m  MSK Producer Level: $MSKProducerLevel\033[0m"
    echo -e "\033[37m  MSK Consumer Level: $MSKConsumerLevel\033[0m"
    echo -e "\033[37m  Producer Topic: $ProducerTopicName1\033[0m"
    echo -e "\033[37m  Consumer Topic: $ConsumerTopicName1\033[0m"
fi

# ECR Repository Management
echo -e "\n\033[36mChecking ECR repository...\033[0m"
if [[ -n "$ProductName" && "$ProductName" != "null" ]]; then
    ecrRepoName="$Environment/$DomainName/$ProductName/$ServiceName"
else
    ecrRepoName="$Environment/$DomainName/$ServiceName"
fi

ecrUri=$(aws ecr describe-repositories --repository-names "$ecrRepoName" --query 'repositories[0].repositoryUri' --output text 2>/dev/null)
exit_code=$?

if [[ $exit_code -ne 0 || "$ecrUri" == "None" || -z "$ecrUri" ]]; then
    echo -e "\033[33mECR repository not found. Creating...\033[0m"
    
    aws cloudformation deploy --template-file "templates/ecr-repository.yaml" --stack-name "$Environment-$FullServiceName-ecr-repository" --parameter-overrides Environment="$Environment" ServiceName="$ServiceName" DomainName="$DomainName" ProductName="$ProductName" --capabilities CAPABILITY_IAM
    
    if [[ $? -eq 0 ]]; then
        ecrUri=$(aws ecr describe-repositories --repository-names "$ecrRepoName" --query 'repositories[0].repositoryUri' --output text)
        echo -e "\033[32mECR repository created: $ecrUri\033[0m"
        
        # Show build instructions
        echo -e "\n\033[33mBuild and push your container image:\033[0m"
        echo -e "\033[90m1. docker build -t $ServiceName .\033[0m"
        echo -e "\033[90m2. aws ecr get-login-password --region $Region | docker login --username AWS --password-stdin $ecrUri\033[0m"
        echo -e "\033[90m3. docker tag $ServiceName:latest $ecrUri:latest\033[0m"
        echo -e "\033[90m4. docker push $ecrUri:latest\033[0m"
        echo ""
        read -p "Press Enter after pushing the image"
    else
        echo -e "\033[31mError: Failed to create ECR repository\033[0m" >&2
        exit 1
    fi
fi

ContainerImage="$ecrUri:latest"

# Deployment steps based on service type
deploymentSteps=(
    "Service Policy:templates/service-policy.yaml"
    "Security Group:templates/security-group.yaml"
)

# Add EFS if needed
if [[ "$NeedsEFS" == "true" ]]; then
    deploymentSteps+=("EFS Config:templates/efs-config.yaml")
    deploymentSteps+=("Upload EFS Configs:upload-configs")
    echo -e "\033[37m  EFS Configuration: Enabled\033[0m"
fi

# Add service-type specific components
loadBalancer=$(echo "$config" | jq -r '.ServiceTypeFeatures.loadBalancer // false')
if [[ "$loadBalancer" == "true" ]]; then
    deploymentSteps+=("NLB Resources:templates/nlb-resources.yaml")
fi

# Add main ECS service
deploymentSteps+=(
    "ECS Service:templates/ecs-service-merged.yaml"
)

# Add monitoring if enabled
CreateMonitoring=$(echo "$config" | jq -r '.CreateMonitoring // true' | tr '[:upper:]' '[:lower:]')
if [[ "$CreateMonitoring" == "true" ]]; then
    deploymentSteps+=("Monitoring:templates/monitoring.yaml")
else
    echo -e "\033[33mSkipping monitoring deployment (CreateMonitoring=false)\033[0m"
fi

# Deploy each component
for step in "${deploymentSteps[@]}"; do
    stepName="${step%%:*}"
    template="${step##*:}"
    
    echo -e "\n\033[33mDeploying: $stepName\033[0m"
    
    stackName="$Environment-$FullServiceName-$(echo "$stepName" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')"
    
    # Handle special cases (non-template deployments) FIRST
    if [[ "$stepName" == "Upload EFS Configs" ]]; then
        echo -e "\033[36mUploading configuration files to EFS...\033[0m"
        # Get EFS ID from CloudFormation output
        EfsId=$(aws cloudformation describe-stacks --stack-name "$Environment-$FullServiceName-efs-config" --query 'Stacks[0].Outputs[?OutputKey==`EFSFileSystemId`].OutputValue' --output text)
        if [[ -n "$EfsId" && "$EfsId" != "None" ]]; then
            uploadConfigsToEFS "$EfsId" "$Environment" "$Region"
            echo -e "\033[32m  $stepName completed successfully\033[0m"
        else
            echo -e "\033[31mError: Could not get EFS ID from CloudFormation\033[0m"
            exit 1
        fi
        continue
    fi
    
    # Check for template file (only for CloudFormation deployments)
    if [[ ! -f "$template" ]]; then
        echo -e "\033[33mWarning: Template not found: $template\033[0m"
        continue
    fi
    
    # Configuration values already extracted at the beginning of the script
    
    case "$stepName" in
        "Service Policy")
            aws cloudformation deploy --template-file "$template" --stack-name "$stackName" --parameter-overrides Environment="$Environment" Region="$Region" ServiceName="$ServiceName" FullServiceName="$FullServiceName" DomainName="$DomainName" ProductName="$ProductName" ServiceType="$ServiceType" NeedsMSK="$NeedsMSK" NeedsSQS="$NeedsSQS" NeedsSNS="$NeedsSNS" NeedsDatabase="$NeedsDatabase" NeedsRedis="$NeedsRedis" NeedsDynamoDB="$NeedsDynamoDB" NeedsS3="$NeedsS3" MSKProducerLevel="$MSKProducerLevel" MSKConsumerLevel="$MSKConsumerLevel" DatabaseLevel="$DatabaseLevel" RedisLevel="$RedisLevel" S3Level="$S3Level" SQSLevel="$SQSLevel" SNSLevel="$SNSLevel" DynamoDBLevel="$DynamoDBLevel" --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
            ;;
        "Security Group")
            aws cloudformation deploy --template-file "$template" --stack-name "$stackName" --parameter-overrides Environment="$Environment" ServiceName="$ServiceName" DomainName="$DomainName" ProductName="$ProductName" ServicePort="$ServicePort" DatabaseLevel="$DatabaseLevel" RedisLevel="$RedisLevel" DAXLevel="$DAXLevel" --capabilities CAPABILITY_IAM
            ;;
        "NLB Resources")
            MSYS_NO_PATHCONV=1 aws cloudformation deploy --template-file "$template" --stack-name "$stackName" --parameter-overrides Environment="$Environment" ServiceName="$ServiceName" FullServiceName="$FullServiceName" DomainName="$DomainName" ProductName="$ProductName" ServicePort="$ServicePort" HealthCheckPath="$HealthCheckPath" --capabilities CAPABILITY_IAM
            ;;
        "EFS Config")
            aws cloudformation deploy --template-file "$template" --stack-name "$stackName" --parameter-overrides Environment="$Environment" ServiceName="$ServiceName" DomainName="$DomainName" ProductName="$ProductName" --capabilities CAPABILITY_IAM
            ;;
        "ECS Service")
            MSYS_NO_PATHCONV=1 aws cloudformation deploy --template-file "$template" --stack-name "$stackName" --parameter-overrides Environment="$Environment" ServiceName="$ServiceName" DomainName="$DomainName" ProductName="$ProductName" ServiceType="$ServiceType" ContainerImage="$ContainerImage" ContainerPort="$ServicePort" HealthCheckPath="$HealthCheckPath" HealthCheckProtocol="$HealthCheckProtocol" DesiredCount="$DesiredCount" MinCapacity="$MinCapacity" MaxCapacity="$MaxCapacity" TaskCpu="$TaskCpu" TaskMemory="$TaskMemory" ECSClusterLevel="$ECSClusterLevel" DatabaseLevel="$DatabaseLevel" RedisLevel="$RedisLevel" MSKProducerLevel="$MSKProducerLevel" MSKConsumerLevel="$MSKConsumerLevel" ServiceConnectLevel="$ServiceConnectLevel" ECSLogGroupLevel="$ECSLogGroupLevel" NeedsMSK="$NeedsMSK" NeedsSQS="$NeedsSQS" NeedsSNS="$NeedsSNS" NeedsDatabase="$NeedsDatabase" NeedsRedis="$NeedsRedis" NeedsDynamoDB="$NeedsDynamoDB" NeedsCouchbase="$NeedsCouchbase" NeedsEFS="$NeedsEFS" CouchbaseBucket="$CouchbaseBucket" CouchbaseScope="$CouchbaseScope" CouchbaseCollection="$CouchbaseCollection" CouchbaseSecretName="$CouchbaseSecretName" CouchbaseEndpointParam="$CouchbaseEndpointParam" JWTSecretParam="$JWTSecretParam" ProducerTopicName1="$ProducerTopicName1" ConsumerTopicName1="$ConsumerTopicName1" FoundationServiceConnectNamespaceSSMPath="$FoundationServiceConnectNamespaceSSMPath" FoundationServiceConnectNamespaceArn="$FoundationServiceConnectNamespaceArn" --capabilities CAPABILITY_IAM
            ;;
        "Monitoring")
            aws cloudformation deploy --template-file "$template" --stack-name "$stackName" --parameter-overrides Environment="$Environment" ServiceName="$ServiceName" FullServiceName="$FullServiceName" DomainName="$DomainName" ProductName="$ProductName" ServiceType="$ServiceType" --capabilities CAPABILITY_IAM
            ;;
    esac
    
    if [[ $? -eq 0 ]]; then
        echo -e "\033[32m  $stepName deployed successfully\033[0m"
    else
        echo -e "\033[31m  $stepName deployment failed\033[0m"
        exit 1
    fi
done

echo -e "\n\033[32mService $FullServiceName deployed successfully to $Environment!\033[0m"
echo -e "\033[33mDomain: $DomainName\033[0m"
if [[ -n "$ProductName" && "$ProductName" != "null" ]]; then
    echo -e "\033[33mProduct: $ProductName\033[0m"
fi
echo -e "\033[33mConfiguration-driven deployment completed using: $ConfigFile\033[0m"