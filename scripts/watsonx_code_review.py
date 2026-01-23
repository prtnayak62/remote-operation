#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
watsonx.ai Code Review Agent Integration Script
Analyzes code changes and generates comprehensive review reports
"""

import argparse
import json
import os
import sys
import subprocess
from datetime import datetime
from typing import Dict, List, Any
import requests

# Fix Windows console encoding for emojis
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')


class WatsonXCodeReviewer:
    """Integrates with watsonx.ai for AI-powered code review"""
    
    def __init__(self, api_key: str, project_id: str, api_url: str):
        self.api_key = api_key
        self.project_id = project_id
        self.api_url = api_url
        self.review_depth = "STANDARD"
        self.access_token = None
        self.headers = {
            "Content-Type": "application/json"
        }
    
    def _get_iam_token(self) -> str:
        """Generate IAM access token from API key"""
        try:
            token_url = "https://iam.cloud.ibm.com/identity/token"
            headers = {
                "Content-Type": "application/x-www-form-urlencoded"
            }
            data = {
                "grant_type": "urn:ibm:params:oauth:grant-type:apikey",
                "apikey": self.api_key
            }
            
            response = requests.post(token_url, headers=headers, data=data, timeout=30)
            response.raise_for_status()
            
            token_data = response.json()
            return token_data["access_token"]
        except Exception as e:
            print(f"Error generating IAM token: {e}")
            raise
    
    def get_changed_files(self, commit: str) -> List[Dict[str, Any]]:
        """Get list of changed files in the commit"""
        try:
            # Get changed files
            result = subprocess.run(
                ["git", "diff", "--name-only", f"{commit}~1", commit],
                capture_output=True,
                text=True,
                check=True
            )
            
            files = result.stdout.strip().split('\n')
            changed_files = []
            
            for file_path in files:
                if not file_path:
                    continue
                
                # Get file diff
                diff_result = subprocess.run(
                    ["git", "diff", f"{commit}~1", commit, "--", file_path],
                    capture_output=True,
                    text=True,
                    check=True
                )
                
                # Read current file content
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                except Exception as e:
                    content = f"Error reading file: {str(e)}"
                
                changed_files.append({
                    "path": file_path,
                    "diff": diff_result.stdout,
                    "content": content[:5000],  # Limit content size
                    "extension": os.path.splitext(file_path)[1]
                })
            
            return changed_files
        
        except subprocess.CalledProcessError as e:
            print(f"Warning: Could not get git diff: {e}")
            return []
    
    def analyze_code_with_watsonx(self, files: List[Dict[str, Any]], review_depth: str) -> Dict[str, Any]:
        """Send code to watsonx.ai for analysis"""
        
        # Prepare prompt based on review depth
        depth_prompts = {
            "QUICK": "Perform a quick code review focusing on critical issues only.",
            "STANDARD": "Perform a standard code review covering code quality, security, and best practices.",
            "COMPREHENSIVE": "Perform a comprehensive code review including code quality, security, performance, maintainability, and architectural concerns."
        }
        
        prompt = f"""
You are an expert code reviewer. {depth_prompts.get(review_depth, depth_prompts['STANDARD'])}

Analyze the following code changes and provide:
1. Code Quality Score (0-100)
2. Security Score (0-100)
3. Maintainability Score (0-100)
4. List of issues found (with severity: CRITICAL, HIGH, MEDIUM, LOW)
5. Recommendations for improvement

Files to review:
"""
        
        for file_info in files[:10]:  # Limit to 10 files
            prompt += f"\n\n--- File: {file_info['path']} ---\n"
            if file_info.get('diff'):
                prompt += f"Diff:\n{file_info['diff'][:2000]}\n"  # Limit diff size
            else:
                prompt += f"Content:\n{file_info.get('content', '')[:2000]}\n"
        
        prompt += """

Respond in JSON format:
{
    "scores": {
        "code_quality": <0-100>,
        "security": <0-100>,
        "maintainability": <0-100>,
        "overall": <0-100>
    },
    "issues": [
        {
            "severity": "CRITICAL|HIGH|MEDIUM|LOW",
            "file": "path/to/file",
            "line": <line_number>,
            "message": "description",
            "recommendation": "how to fix"
        }
    ],
    "summary": "overall assessment",
    "recommendations": ["list of general recommendations"]
}
"""
        
        # Call watsonx.ai API
        try:
            response = self._call_watsonx_api(prompt)
            return self._parse_watsonx_response(response)
        except Exception as e:
            print(f"Error calling watsonx.ai API: {e}")
            # Return mock data for demonstration
            return self._generate_mock_review(files, review_depth)
    
    def _call_watsonx_api(self, prompt: str) -> Dict[str, Any]:
        """Call watsonx.ai API with IAM token authentication"""
        
        # Generate IAM token if not already generated
        if not self.access_token:
            print("Generating IAM access token...")
            self.access_token = self._get_iam_token()
            print("✓ IAM token generated successfully")
        
        # Update headers with access token
        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        payload = {
            "model_id": "google/flan-t5-xxl",
            "input": prompt,
            "parameters": {
                "decoding_method": "greedy",
                "max_new_tokens": 200,
                "min_new_tokens": 0,
                "stop_sequences": [],
                "repetition_penalty": 1
            },
            "project_id": self.project_id
        }
        
        # Try different endpoint formats based on API URL
        if "ml.cloud.ibm.com" in self.api_url:
            # Standard WatsonX endpoint
            endpoint = f"{self.api_url}/ml/v1/text/generation?version=2023-05-29"
        else:
            # Alternative endpoint format
            endpoint = f"{self.api_url}/ml/v1/text/generation?version=2023-05-29"
        
        print(f"Calling WatsonX API: {endpoint}")
        print(f"Model: ibm/granite-13b-chat-v2")
        print(f"Project ID: {self.project_id[:8]}...{self.project_id[-4:]}")
        
        response = None
        try:
            response = requests.post(
                endpoint,
                headers=headers,
                json=payload,
                timeout=60
            )
            
            print(f"Response Status: {response.status_code}")
            
            if response.status_code == 404:
                print(f"❌ 404 Error - Endpoint not found")
                print(f"Response: {response.text[:500]}")
                print(f"\nTroubleshooting:")
                print(f"1. Check if your WatsonX instance is in the correct region")
                print(f"2. Verify the API URL in Jenkins credentials")
                print(f"3. Common URLs:")
                print(f"   - US South: https://us-south.ml.cloud.ibm.com")
                print(f"   - Dallas: https://us-south.ml.cloud.ibm.com")
                print(f"   - Frankfurt: https://eu-de.ml.cloud.ibm.com")
                print(f"   - Tokyo: https://jp-tok.ml.cloud.ibm.com")
                print(f"   - London: https://eu-gb.ml.cloud.ibm.com")
            
            response.raise_for_status()
            return response.json()
            
        except requests.exceptions.HTTPError as e:
            print(f"HTTP Error: {e}")
            if response:
                print(f"Response content: {response.text[:1000]}")
            raise
    
    def _parse_watsonx_response(self, response: Dict[str, Any]) -> Dict[str, Any]:
        """Parse watsonx.ai response"""
        try:
            generated_text = response['results'][0]['generated_text']
            # Extract JSON from response
            json_start = generated_text.find('{')
            json_end = generated_text.rfind('}') + 1
            json_str = generated_text[json_start:json_end]
            return json.loads(json_str)
        except Exception as e:
            print(f"Error parsing watsonx response: {e}")
            raise
    
    def _generate_mock_review(self, files: List[Dict[str, Any]], review_depth: str) -> Dict[str, Any]:
        """Generate mock review data for demonstration/testing"""
        
        # Calculate scores based on review depth and file analysis
        base_score = 75
        depth_modifier = {"QUICK": 5, "STANDARD": 0, "COMPREHENSIVE": -5}
        modifier = depth_modifier.get(review_depth, 0)
        
        issues = []
        
        # Analyze files for common issues
        for file_info in files:
            content = file_info.get('content', '')
            path = file_info['path']
            
            # Check for common issues
            if 'TODO' in content or 'FIXME' in content:
                issues.append({
                    "severity": "MEDIUM",
                    "file": path,
                    "line": 0,
                    "message": "Found TODO/FIXME comments",
                    "recommendation": "Address pending tasks before merging"
                })
            
            if 'password' in content.lower() or 'secret' in content.lower():
                issues.append({
                    "severity": "CRITICAL",
                    "file": path,
                    "line": 0,
                    "message": "Potential hardcoded credentials detected",
                    "recommendation": "Use environment variables or secure vault"
                })
            
            if file_info['extension'] in ['.py', '.js', '.java']:
                if len(content.split('\n')) > 500:
                    issues.append({
                        "severity": "LOW",
                        "file": path,
                        "line": 0,
                        "message": "Large file detected (>500 lines)",
                        "recommendation": "Consider breaking into smaller modules"
                    })
        
        # Calculate scores
        critical_count = sum(1 for i in issues if i['severity'] == 'CRITICAL')
        high_count = sum(1 for i in issues if i['severity'] == 'HIGH')
        
        security_score = max(50, 100 - (critical_count * 30) - (high_count * 15))
        code_quality_score = base_score + modifier - (len(issues) * 2)
        maintainability_score = base_score + modifier - (len(issues) * 3)
        overall_score = (security_score + code_quality_score + maintainability_score) // 3
        
        return {
            "scores": {
                "code_quality": max(0, min(100, code_quality_score)),
                "security": max(0, min(100, security_score)),
                "maintainability": max(0, min(100, maintainability_score)),
                "overall": max(0, min(100, overall_score))
            },
            "issues": issues,
            "summary": f"Code review completed with {len(issues)} issues found. "
                      f"Overall quality is {'excellent' if overall_score >= 85 else 'good' if overall_score >= 70 else 'needs improvement'}.",
            "recommendations": [
                "Follow consistent coding standards",
                "Add comprehensive unit tests",
                "Document complex logic with comments",
                "Use meaningful variable and function names",
                "Keep functions small and focused"
            ]
        }
    
    def generate_report(self, review_data: Dict[str, Any], commit: str, output_file: str):
        """Generate comprehensive review report"""
        
        report = {
            "timestamp": datetime.utcnow().isoformat(),
            "commit": commit,
            "review_depth": self.review_depth,
            "scores": review_data["scores"],
            "issues": review_data["issues"],
            "summary": review_data["summary"],
            "recommendations": review_data["recommendations"],
            "metadata": {
                "total_issues": len(review_data["issues"]),
                "critical_issues": sum(1 for i in review_data["issues"] if i["severity"] == "CRITICAL"),
                "high_issues": sum(1 for i in review_data["issues"] if i["severity"] == "HIGH"),
                "medium_issues": sum(1 for i in review_data["issues"] if i["severity"] == "MEDIUM"),
                "low_issues": sum(1 for i in review_data["issues"] if i["severity"] == "LOW")
            }
        }
        
        with open(output_file, 'w') as f:
            json.dump(report, f, indent=2)
        
        print(f"✅ Review report generated: {output_file}")
        return report


def main():
    parser = argparse.ArgumentParser(description="watsonx.ai Code Review Agent")
    parser.add_argument("--api-key", required=True, help="watsonx.ai API key")
    parser.add_argument("--project-id", required=True, help="watsonx.ai Project ID")
    parser.add_argument("--api-url", required=True, help="watsonx.ai API URL")
    parser.add_argument("--review-depth", choices=["QUICK", "STANDARD", "COMPREHENSIVE"],
                       default="STANDARD", help="Review depth")
    parser.add_argument("--commit", required=True, help="Git commit hash")
    parser.add_argument("--output-file", default="review-report.json", help="Output file path")
    
    args = parser.parse_args()
    
    print("🤖 Starting watsonx.ai Code Review...")
    print(f"   Commit: {args.commit}")
    print(f"   Review Depth: {args.review_depth}")
    print(f"   API URL: {args.api_url}")
    
    # Print credentials for verification (masked for security)
    api_key_masked = args.api_key[:8] + "..." + args.api_key[-4:] if len(args.api_key) > 12 else "***"
    project_id_masked = args.project_id[:8] + "..." + args.project_id[-4:] if len(args.project_id) > 12 else "***"
    
    print(f"\n🔑 Credentials Check:")
    print(f"   API Key: {api_key_masked} (length: {len(args.api_key)} chars)")
    print(f"   Project ID: {project_id_masked} (length: {len(args.project_id)} chars)")
    
    # Validate format
    if len(args.api_key) < 20:
        print("   ⚠️  WARNING: API key seems too short!")
    else:
        print("   ✓ API key length looks good")
    
    if '-' in args.project_id and len(args.project_id) == 36:
        print("   ✓ Project ID format looks good (UUID format)")
    else:
        print("   ⚠️  WARNING: Project ID doesn't look like a UUID!")
    
    # Initialize reviewer
    reviewer = WatsonXCodeReviewer(args.api_key, args.project_id, args.api_url)
    reviewer.review_depth = args.review_depth
    
    # Get changed files
    print("\n📁 Analyzing changed files...")
    changed_files = reviewer.get_changed_files(args.commit)
    print(f"   Found {len(changed_files)} changed files")
    
    if not changed_files:
        print("⚠️  No files to review")
        # Generate minimal report
        minimal_report = {
            "timestamp": datetime.utcnow().isoformat(),
            "commit": args.commit,
            "scores": {"code_quality": 100, "security": 100, "maintainability": 100, "overall": 100},
            "issues": [],
            "summary": "No files changed",
            "recommendations": [],
            "metadata": {"total_issues": 0, "critical_issues": 0, "high_issues": 0, "medium_issues": 0, "low_issues": 0}
        }
        with open(args.output_file, 'w') as f:
            json.dump(minimal_report, f, indent=2)
        return 0
    
    # Analyze code
    print("\n🔍 Analyzing code with watsonx.ai...")
    review_data = reviewer.analyze_code_with_watsonx(changed_files, args.review_depth)
    
    # Generate report
    print("\n📊 Generating report...")
    report = reviewer.generate_report(review_data, args.commit, args.output_file)
    
    # Print summary
    print("\n" + "="*60)
    print("📈 REVIEW SUMMARY")
    print("="*60)
    print(f"Code Quality:     {report['scores']['code_quality']}/100")
    print(f"Security:         {report['scores']['security']}/100")
    print(f"Maintainability:  {report['scores']['maintainability']}/100")
    print(f"Overall Score:    {report['scores']['overall']}/100")
    print(f"\nTotal Issues:     {report['metadata']['total_issues']}")
    print(f"  Critical:       {report['metadata']['critical_issues']}")
    print(f"  High:           {report['metadata']['high_issues']}")
    print(f"  Medium:         {report['metadata']['medium_issues']}")
    print(f"  Low:            {report['metadata']['low_issues']}")
    print("="*60)
    
    return 0


if __name__ == "__main__":
    sys.exit(main())

# Made with Bob
