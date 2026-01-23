#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Quality Gate Evaluation Script
Evaluates code review scores against defined thresholds
"""

import argparse
import json
import sys
import os
from typing import Dict, List, Any
from datetime import datetime

# Fix Windows console encoding for emojis
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')


class QualityGate:
    """Evaluates quality metrics against thresholds"""
    
    def __init__(self, thresholds: Dict[str, int]):
        self.thresholds = thresholds
        self.failed_criteria = []
        self.warning_criteria = []
        self.passed_criteria = []
    
    def evaluate(self, scores: Dict[str, int]) -> Dict[str, Any]:
        """Evaluate scores against thresholds"""
        
        results = {
            "timestamp": datetime.utcnow().isoformat(),
            "status": "PASSED",
            "message": "",
            "scores": scores,
            "thresholds": self.thresholds,
            "failed_criteria": [],
            "warning_criteria": [],
            "passed_criteria": [],
            "details": {}
        }
        
        # Evaluate each metric
        for metric, score in scores.items():
            threshold = self.thresholds.get(metric, 0)
            
            if score < threshold:
                # Failed
                diff = threshold - score
                criterion = f"{metric}: {score}/100 (threshold: {threshold}/100, deficit: {diff})"
                results["failed_criteria"].append(criterion)
                results["details"][metric] = {
                    "status": "FAILED",
                    "score": score,
                    "threshold": threshold,
                    "deficit": diff
                }
            elif score < threshold + 10:
                # Warning (within 10 points of threshold)
                margin = score - threshold
                criterion = f"{metric}: {score}/100 (threshold: {threshold}/100, margin: {margin})"
                results["warning_criteria"].append(criterion)
                results["details"][metric] = {
                    "status": "WARNING",
                    "score": score,
                    "threshold": threshold,
                    "margin": margin
                }
            else:
                # Passed
                margin = score - threshold
                criterion = f"{metric}: {score}/100 (threshold: {threshold}/100, margin: {margin})"
                results["passed_criteria"].append(criterion)
                results["details"][metric] = {
                    "status": "PASSED",
                    "score": score,
                    "threshold": threshold,
                    "margin": margin
                }
        
        # Determine overall status
        if results["failed_criteria"]:
            results["status"] = "FAILED"
            results["message"] = f"Quality Gate FAILED: {len(results['failed_criteria'])} criteria not met"
        elif results["warning_criteria"]:
            results["status"] = "WARNING"
            results["message"] = f"Quality Gate PASSED with warnings: {len(results['warning_criteria'])} criteria close to threshold"
        else:
            results["status"] = "PASSED"
            results["message"] = "Quality Gate PASSED: All criteria met"
        
        return results
    
    def print_summary(self, results: Dict[str, Any]):
        """Print evaluation summary"""
        
        print("\n" + "="*70)
        print("🚦 QUALITY GATE EVALUATION")
        print("="*70)
        
        # Print status
        status_emoji = {
            "PASSED": "✅",
            "WARNING": "⚠️",
            "FAILED": "❌"
        }
        print(f"\nStatus: {status_emoji.get(results['status'], '❓')} {results['status']}")
        print(f"Message: {results['message']}")
        
        # Print details
        print("\n" + "-"*70)
        print("METRIC DETAILS")
        print("-"*70)
        
        for metric, detail in results["details"].items():
            status_symbol = {
                "PASSED": "✅",
                "WARNING": "⚠️",
                "FAILED": "❌"
            }
            
            metric_name = metric.replace('_', ' ').title()
            print(f"\n{status_symbol.get(detail['status'], '❓')} {metric_name}")
            print(f"   Score:     {detail['score']}/100")
            print(f"   Threshold: {detail['threshold']}/100")
            
            if detail['status'] == 'FAILED':
                print(f"   Deficit:   {detail['deficit']} points")
            else:
                print(f"   Margin:    {detail['margin']} points")
        
        # Print criteria lists
        if results["failed_criteria"]:
            print("\n" + "-"*70)
            print("❌ FAILED CRITERIA")
            print("-"*70)
            for criterion in results["failed_criteria"]:
                print(f"  • {criterion}")
        
        if results["warning_criteria"]:
            print("\n" + "-"*70)
            print("⚠️  WARNING CRITERIA")
            print("-"*70)
            for criterion in results["warning_criteria"]:
                print(f"  • {criterion}")
        
        if results["passed_criteria"]:
            print("\n" + "-"*70)
            print("✅ PASSED CRITERIA")
            print("-"*70)
            for criterion in results["passed_criteria"]:
                print(f"  • {criterion}")
        
        print("\n" + "="*70)


def main():
    parser = argparse.ArgumentParser(description="Quality Gate Evaluation")
    parser.add_argument("--code-quality-score", type=int, required=True, 
                       help="Code quality score (0-100)")
    parser.add_argument("--security-score", type=int, required=True,
                       help="Security score (0-100)")
    parser.add_argument("--maintainability-score", type=int, required=True,
                       help="Maintainability score (0-100)")
    parser.add_argument("--code-threshold", type=int, default=70,
                       help="Code quality threshold (default: 70)")
    parser.add_argument("--security-threshold", type=int, default=80,
                       help="Security threshold (default: 80)")
    parser.add_argument("--maintainability-threshold", type=int, default=75,
                       help="Maintainability threshold (default: 75)")
    parser.add_argument("--output-file", default="quality-gate-result.json",
                       help="Output file path")
    
    args = parser.parse_args()
    
    # Prepare scores and thresholds
    scores = {
        "code_quality": args.code_quality_score,
        "security": args.security_score,
        "maintainability": args.maintainability_score
    }
    
    thresholds = {
        "code_quality": args.code_threshold,
        "security": args.security_threshold,
        "maintainability": args.maintainability_threshold
    }
    
    # Evaluate quality gate
    gate = QualityGate(thresholds)
    results = gate.evaluate(scores)
    
    # Print summary
    gate.print_summary(results)
    
    # Save results
    with open(args.output_file, 'w') as f:
        json.dump(results, f, indent=2)
    
    print(f"\n💾 Results saved to: {args.output_file}\n")
    
    # Return exit code based on status
    if results["status"] == "FAILED":
        return 1
    elif results["status"] == "WARNING":
        return 0  # Warning is not a failure
    else:
        return 0


if __name__ == "__main__":
    sys.exit(main())

# Made with Bob
