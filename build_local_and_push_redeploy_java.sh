#!/bin/bash
# Java Spring Boot Maven build and push script

# Default values
ENV="dev"
IMAGE_TAG="latest"
SKIP_TESTS=false
DEPLOY_FLAG=false
BUILD_PUSH_FLAG=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        --deploy)
            DEPLOY_FLAG=true
            shift
            ;;
        --buildpush)
            BUILD_PUSH_FLAG=true
            shift
            ;;
        --skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        --help)
            echo "Usage: $0 [environment] [options]"
            echo "Options:"
            echo "  --buildpush     Build with Maven and push the image"
            echo "  --deploy        Build, push, and deploy the image"
            echo "  --skip-tests    Skip Maven tests during build"
            echo "  --help          Show this help message"
            exit 0
            ;;
        dev|staging|prod)
            ENV="$1"
            shift
            ;;
        *)
            echo "Warning: Unknown argument '$1'"
            shift
            ;;
    esac
done

# Read service configuration
if [ ! -f "config/dev/service-config.json" ]; then
    echo "❌ Error: service-config.json not found in config/dev/"
    echo "Make sure you're running this script from the service directory"
    exit 1
fi

# Extract service details from config
SERVICE_NAME=$(jq -r '.ServiceName' config/dev/service-config.json)
DOMAIN_NAME=$(jq -r '.DomainName' config/dev/service-config.json)
PRODUCT_NAME=$(jq -r '.ProductName' config/dev/service-config.json)
FULL_SERVICE_NAME=$(jq -r '.FullServiceName' config/dev/service-config.json)
SERVICE_TYPE=$(jq -r '.ServiceType' config/dev/service-config.json)

if [ "$SERVICE_NAME" = "null" ] || [ -z "$SERVICE_NAME" ]; then
    echo "❌ Error: ServiceName not found in service-config.json"
    exit 1
fi

if [ "$DOMAIN_NAME" = "null" ] || [ -z "$DOMAIN_NAME" ]; then
    echo "❌ Error: DomainName not found in service-config.json"
    exit 1
fi

echo "Java Spring Boot Service: $SERVICE_NAME ($SERVICE_TYPE)"
echo "Domain: $DOMAIN_NAME"
if [ "$PRODUCT_NAME" != "null" ] && [ -n "$PRODUCT_NAME" ]; then
    echo "Product: $PRODUCT_NAME"
fi
echo "Full Service Name: $FULL_SERVICE_NAME"

# Interactive region selection
echo "Select AWS Region:"
echo "=================="
echo "1. us-east-2"
echo "2. ap-south-1 (Mumbai)"
echo "3. Custom region"
echo ""
read -p "Enter your choice (1-3): " REGION_CHOICE

case "$REGION_CHOICE" in
    "1")
        REGION="us-east-2"
        ;;
    "2")
        REGION="ap-south-1"
        ;;
    "3")
        read -p "Enter custom region (e.g., eu-west-1): " REGION
        ;;
    *)
        echo "Invalid choice. Using default: ap-south-1"
        REGION="ap-south-1"
        ;;
esac

echo "Selected Region: $REGION"
echo ""

# Debug information
echo "Environment: $ENV"
echo "Deploy flag: $DEPLOY_FLAG"
echo "Skip tests: $SKIP_TESTS"

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven (mvn) is not installed or not in PATH"
    exit 1
fi

# Check if pom.xml exists
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found. This doesn't appear to be a Maven project."
    exit 1
fi

# Maven build options
MAVEN_OPTS=""
if [ "$SKIP_TESTS" = true ]; then
    MAVEN_OPTS="-DskipTests"
    echo "Skipping tests during Maven build"
fi

# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# ECR repository URI - using domain/product structure
if [ "$PRODUCT_NAME" != "null" ] && [ -n "$PRODUCT_NAME" ]; then
    ECR_REPO="${AWS_ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${ENV}/${DOMAIN_NAME}/${PRODUCT_NAME}/${SERVICE_NAME}"
else
    ECR_REPO="${AWS_ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${ENV}/${DOMAIN_NAME}/${SERVICE_NAME}"
fi

# Clean and build with Maven
echo "Cleaning and building with Maven..."

# Install local JAR if this is trip-mngr-service
if [ "$SERVICE_NAME" = "trip-mngr-service" ]; then
    echo "Installing local trip-rule-engine-lib JAR..."
    mvn install:install-file -Dfile=./trip-rule-engine-lib-0.0.1.jar -DgroupId=com.ibm.autoconnect.rule -DartifactId=trip-rule-engine-lib -Dversion=0.0.1 -Dpackaging=jar
    mvn install:install-file -Dfile=./timer-lib-1.0.0.jar -DgroupId=com.autoconnect.ibm -DartifactId=timer-lib -Dversion=1.0.0 -Dpackaging=jar
fi

mvn clean package ${MAVEN_OPTS}

if [ $? -ne 0 ]; then
    echo "❌ Maven build failed!"
    exit 1
fi

echo "✅ Maven build completed successfully"

# Login to ECR
echo "Logging in to Amazon ECR..."
aws ecr get-login-password --region ${REGION} | podman login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com

# Build the Docker image
echo "Building Docker image..."
podman build -t ${ECR_REPO}:${IMAGE_TAG} .

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed!"
    exit 1
fi

# Push the image to ECR
echo "Pushing image to ECR..."
podman push ${ECR_REPO}:${IMAGE_TAG}

if [ $? -ne 0 ]; then
    echo "❌ Docker push failed!"
    exit 1
fi

echo "✅ Image pushed successfully to ${ECR_REPO}:${IMAGE_TAG}"

# Optionally update the ECS service to force new deployment
if [ "$DEPLOY_FLAG" = true ]; then
  echo "Forcing new deployment of ECS service..."
  
  # Determine cluster name based on domain/product structure
  if [ "$PRODUCT_NAME" != "null" ] && [ -n "$PRODUCT_NAME" ]; then
      CLUSTER_NAME="${ENV}-${DOMAIN_NAME}-${PRODUCT_NAME}"
  else
      CLUSTER_NAME="${ENV}-${DOMAIN_NAME}"
  fi
  CLUSTER_NAME="${ENV}-${DOMAIN_NAME}"
  
  SERVICE_NAME_FULL=${ENV}-"${FULL_SERVICE_NAME}"
  
  echo "Cluster: $CLUSTER_NAME"
  echo "Service: $SERVICE_NAME_FULL"
  
  aws ecs update-service --cluster ${CLUSTER_NAME} --service ${SERVICE_NAME_FULL} --force-new-deployment --region ${REGION} --output text --query 'service.serviceName'
  echo "✅ Force new deployment initiated"
  
  # Show deployment status after update
  echo "Updated deployments:"
  aws ecs describe-services --cluster ${CLUSTER_NAME} --services ${SERVICE_NAME_FULL} --region ${REGION} --query "services[0].deployments[*].{Status:status,TaskDefinition:taskDefinition,CreatedAt:createdAt}" --output table
  
  # Wait for deployment to complete
  echo "Waiting for deployment to stabilize (this may take a few minutes)..."
  aws ecs wait services-stable --cluster ${CLUSTER_NAME} --services ${SERVICE_NAME_FULL} --region ${REGION}
  
  # Check deployment status
  STATUS=$(aws ecs describe-services --cluster ${CLUSTER_NAME} --services ${SERVICE_NAME_FULL} --region ${REGION} --query "services[0].deployments[0].status" --output text)
  if [ "$STATUS" == "PRIMARY" ]; then
    echo "✅ Deployment completed successfully!"
    
    # Get running tasks
    TASKS=$(aws ecs list-tasks --cluster ${CLUSTER_NAME} --service-name ${SERVICE_NAME_FULL} --region ${REGION} --desired-status RUNNING --query "taskArns" --output text)
    if [ -n "$TASKS" ]; then
      echo "Running tasks:"
      for task in $TASKS; do
        echo "- $task"
      done

      # Print command to check logs
      echo ""
      echo "To check container logs, use:"
      if [ "$PRODUCT_NAME" != "null" ] && [ -n "$PRODUCT_NAME" ]; then
          LOG_GROUP="/${ENV}/ecs/${DOMAIN_NAME}/${PRODUCT_NAME}/${SERVICE_NAME}"
      else
          LOG_GROUP="/${ENV}/ecs/${DOMAIN_NAME}/${SERVICE_NAME}"
      fi
      echo "aws logs get-log-events --log-group-name ${LOG_GROUP} --log-stream-name ecs/${SERVICE_NAME}/latest --region ${REGION}"

    else
      echo "⚠️ No running tasks found!"
    fi
  else
    echo "⚠️ Deployment may have issues. Check the AWS console for details."
  fi
fi

echo ""
echo "Build Summary:"
echo "=============="
echo "Service: $SERVICE_NAME"
echo "Environment: $ENV"
echo "Region: $REGION"
echo "ECR Repository: $ECR_REPO"
echo "Image Tag: $IMAGE_TAG"