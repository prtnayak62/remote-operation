#!/bin/bash
# Java Spring Boot Maven build and push script

ENV=${1:-"dev"}
IMAGE_TAG="latest"

# Read service configuration
if [ ! -f "config/dev/service-config.json" ]; then
    echo "❌ Error: service-config.json not found in config/dev/"
    echo "Make sure you're running this script from the service directory"
    exit 1
fi

# Extract service details from config
SERVICE_NAME=$(jq -r '.ServiceName' config/dev/service-config.json)
SERVICE_TYPE=$(jq -r '.ServiceType' config/dev/service-config.json)
echo $SERVICE_NAME;
if [ "$SERVICE_NAME" = "null" ] || [ -z "$SERVICE_NAME" ]; then
    echo "❌ Error: ServiceName not found in service-config.json"
    exit 1
fi

echo "Java Spring Boot Service: $SERVICE_NAME ($SERVICE_TYPE)"

# Interactive region selection
echo "Select AWS Region:"
echo "=================="
echo "1. us-east-2 (N. Virginia)"
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
        echo "Invalid choice. Using default: us-east-2"
        REGION="us-east-2"
        ;;
esac

echo "Selected Region: $REGION"
echo ""

# Usage information
if [ "$1" == "--help" ]; then
  echo "Usage: $0 [environment] [options]"
  echo "Options:"
  echo "  --buildpush     Build with Maven and push the image"
  echo "  --deploy        Build, push, and deploy the image"
  echo "  --skip-tests    Skip Maven tests during build"
  echo "  --help          Show this help message"
  exit 0
fi

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
if [ "$2" == "--skip-tests" ] || [ "$3" == "--skip-tests" ]; then
    MAVEN_OPTS="-DskipTests"
    echo "Skipping tests during Maven build"
fi

# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# ECR repository URI
ECR_REPO="${AWS_ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${ENV}/${SERVICE_NAME}"

# Clean and build with Maven
echo "Cleaning and building with Maven..."

# mvn install:install-file -Dfile=./trip-rules-engine-0.0.2.jar -DgroupId=com.ibm.trip.rules.engine -DartifactId=trip-rules-engine -Dversion=0.0.2 -Dpackaging=jar
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
exit 0
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
if [ "$2" == "--deploy" ] || [ "$3" == "--deploy" ]; then
  echo "Forcing new deployment of ECS service..."
  aws ecs update-service --cluster ${ENV}-microservices --service ${ENV}-${SERVICE_NAME} --force-new-deployment --region ${REGION} --output text --query 'service.serviceName'
  echo "Deployment initiated. Check the AWS console for status."
  
  # Wait for deployment to complete
  echo "Waiting for deployment to stabilize (this may take a few minutes)..."
  aws ecs wait services-stable --cluster ${ENV}-microservices --services ${ENV}-${SERVICE_NAME} --region ${REGION}
  
  # Check deployment status
  STATUS=$(aws ecs describe-services --cluster ${ENV}-microservices --services ${ENV}-${SERVICE_NAME} --region ${REGION} --query "services[0].deployments[0].status" --output text)
  if [ "$STATUS" == "PRIMARY" ]; then
    echo "✅ Deployment completed successfully!"
    
    # Get running tasks
    TASKS=$(aws ecs list-tasks --cluster ${ENV}-microservices --service-name ${ENV}-${SERVICE_NAME} --region ${REGION} --desired-status RUNNING --query "taskArns" --output text)
    if [ -n "$TASKS" ]; then
      echo "Running tasks:"
      for task in $TASKS; do
        echo "- $task"
      done

      # Print command to check logs
      echo ""
      echo "To check container logs, use:"
      echo "aws logs get-log-events --log-group-name /${ENV}/ecs/${SERVICE_NAME} --log-stream-name ecs/${SERVICE_NAME}/latest --region ${REGION}"

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