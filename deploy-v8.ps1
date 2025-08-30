param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("dev", "staging", "prod")]
    [string]$Environment,

    [Parameter(Mandatory=$true)]
    [string]$Region

)

$ServiceName = Split-Path -Leaf (Get-Location)
$ConfigFile = "config\$Environment\service-config.json"

Write-Host "Configuration-Driven Deployment (v8)" -ForegroundColor Green
Write-Host "Service: $ServiceName" -ForegroundColor Yellow
Write-Host "Environment: $Environment" -ForegroundColor Yellow
# Set AWS region
$env:AWS_DEFAULT_REGION = $Region
Write-Host "AWS Region set to: $Region" -ForegroundColor Cyan

# Load service configuration
if (-not (Test-Path $ConfigFile)) {
    Write-Error "Configuration file not found: $ConfigFile"
    Write-Host "Run create-service-v8.ps1 first to generate configurations" -ForegroundColor Red
    exit 1
}

$config = Get-Content $ConfigFile | ConvertFrom-Json
Write-Host "Loaded configuration from: $ConfigFile" -ForegroundColor Cyan

# Display configuration summary
Write-Host "`nConfiguration Summary:" -ForegroundColor Yellow
Write-Host "  Service Type: $($config.ServiceType)" -ForegroundColor White
Write-Host "  Service Port: $($config.ServicePort)" -ForegroundColor White
Write-Host "  Task CPU: $($config.TaskCpu)" -ForegroundColor White
Write-Host "  Task Memory: $($config.TaskMemory)" -ForegroundColor White
Write-Host "  Desired Count: $($config.DesiredCount)" -ForegroundColor White
Write-Host "  Min/Max Capacity: $($config.MinCapacity)/$($config.MaxCapacity)" -ForegroundColor White

# ECR Repository Management
Write-Host "`nChecking ECR repository..." -ForegroundColor Cyan
$ecrUri = $(aws ecr describe-repositories --repository-names "$Environment/$ServiceName" --query 'repositories[0].repositoryUri' --output text 2>$null)

if ($LASTEXITCODE -ne 0 -or $ecrUri -eq "None" -or [string]::IsNullOrEmpty($ecrUri)) {
    Write-Host "ECR repository not found. Creating..." -ForegroundColor Yellow
    
    aws cloudformation deploy --template-file "templates/ecr-repository.yaml" --stack-name "$Environment-$ServiceName-ecr-repository" --parameter-overrides Environment=$Environment ServiceName=$ServiceName --capabilities CAPABILITY_IAM
    
    if ($LASTEXITCODE -eq 0) {
        $ecrUri = $(aws ecr describe-repositories --repository-names "$Environment/$ServiceName" --query 'repositories[0].repositoryUri' --output text)
        Write-Host "ECR repository created: $ecrUri" -ForegroundColor Green
        
        # Show build instructions
        Write-Host "`nBuild and push your container image:" -ForegroundColor Yellow
        # $region = ($ecrUri -split '\.')[3]
        Write-Host "1. docker build -t $ServiceName ." -ForegroundColor Gray
        Write-Host "2. aws ecr get-login-password --region $region | docker login --username AWS --password-stdin $ecrUri" -ForegroundColor Gray
        Write-Host "3. docker tag $ServiceName`:latest $ecrUri`:latest" -ForegroundColor Gray
        Write-Host "4. docker push $ecrUri`:latest" -ForegroundColor Gray
        Write-Host ""
        Read-Host "Press Enter after pushing the image"
    } else {
        Write-Error "Failed to create ECR repository"
        exit 1
    }
}

$ContainerImage = "$ecrUri`:latest"

# Deployment steps based on service type
$deploymentSteps = @(
    @{Name="Service Policy"; Template="templates/service-policy-v7.yaml"},
    @{Name="Security Group"; Template="templates/security-group-v7.yaml"}
)

# Add service-type specific components
if ($config.ServiceTypeFeatures.loadBalancer) {
    $deploymentSteps += @{Name="NLB Resources"; Template="templates/nlb-resources.yaml"}
}

# Add main ECS service
$deploymentSteps += @(
    @{Name="ECS Service"; Template="templates/ecs-service-merged-v7.yaml"},
    @{Name="Monitoring"; Template="templates/monitoring-v7.yaml"}
)

# Deploy each component
foreach ($step in $deploymentSteps) {
    Write-Host "`nDeploying: $($step.Name)" -ForegroundColor Yellow
    
    $stackName = "$Environment-$ServiceName-$($step.Name.Replace(' ', '-').ToLower())"
    
    if (-not (Test-Path $step.Template)) {
        Write-Warning "Template not found: $($step.Template)"
        continue
    }
    
    try {
        switch ($step.Name) {
            "Service Policy" {
                aws cloudformation deploy --template-file $($step.Template) --stack-name $stackName --parameter-overrides Environment=$Environment Region=$Region ServiceName=$ServiceName ServiceType=$($config.ServiceType) NeedsMSK=$($config.ResourceAccess.NeedsMSK.ToString().ToLower()) NeedsSQS=$($config.ResourceAccess.NeedsSQS.ToString().ToLower()) NeedsSNS=$($config.ResourceAccess.NeedsSNS.ToString().ToLower()) NeedsDatabase=$($config.ResourceAccess.NeedsDatabase.ToString().ToLower()) NeedsRedis=$($config.ResourceAccess.NeedsRedis.ToString().ToLower()) NeedsDynamoDB=$($config.ResourceAccess.NeedsDynamoDB.ToString().ToLower()) --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
            }
            "Security Group" {
                aws cloudformation deploy --template-file $($step.Template) --stack-name $stackName --parameter-overrides Environment=$Environment ServiceName=$ServiceName ServicePort=$($config.ServicePort) --capabilities CAPABILITY_IAM
            }
            "NLB Resources" {
                aws cloudformation deploy --template-file $($step.Template) --stack-name $stackName --parameter-overrides Environment=$Environment ServiceName=$ServiceName ServicePort=$($config.ServicePort) HealthCheckPath=$($config.HealthCheckPath) --capabilities CAPABILITY_IAM
            }
            "ECS Service" {
                aws cloudformation deploy --template-file $($step.Template) --stack-name $stackName --parameter-overrides Environment=$Environment ServiceName=$ServiceName ServiceType=$($config.ServiceType) ContainerImage=$ContainerImage ContainerPort=$($config.ServicePort) HealthCheckPath=$($config.HealthCheckPath) HealthCheckProtocol=$($config.HealthCheckProtocol) DesiredCount=$($config.DesiredCount) MinCapacity=$($config.MinCapacity) MaxCapacity=$($config.MaxCapacity) TaskCpu=$($config.TaskCpu) TaskMemory=$($config.TaskMemory) NeedsMSK=$($config.ResourceAccess.NeedsMSK.ToString().ToLower()) NeedsSQS=$($config.ResourceAccess.NeedsSQS.ToString().ToLower()) NeedsSNS=$($config.ResourceAccess.NeedsSNS.ToString().ToLower()) NeedsDatabase=$($config.ResourceAccess.NeedsDatabase.ToString().ToLower()) NeedsRedis=$($config.ResourceAccess.NeedsRedis.ToString().ToLower()) NeedsDynamoDB=$($config.ResourceAccess.NeedsDynamoDB.ToString().ToLower()) CouchbaseSecretArn=$($config.CouchbaseSecretArn.ToString()) CouchbaseBucket=$($config.CouchbaseBucket.ToString()) KafkaIamUrlArn=$($config.KafkaIamUrlArn.ToString()) --capabilities CAPABILITY_IAM
            }
            "Monitoring" {
                aws cloudformation deploy --template-file $($step.Template) --stack-name $stackName --parameter-overrides Environment=$Environment ServiceName=$ServiceName ServiceType=$($config.ServiceType) --capabilities CAPABILITY_IAM
            }
        }
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  $($step.Name) deployed successfully" -ForegroundColor Green
        } else {
            Write-Host "  $($step.Name) deployment failed" -ForegroundColor Red
            exit 1
        }
    }
    catch {
        Write-Host "  Error deploying $($step.Name): $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`nService $ServiceName deployed successfully to $Environment!" -ForegroundColor Green
Write-Host "Configuration-driven deployment completed using: $ConfigFile" -ForegroundColor Yellow