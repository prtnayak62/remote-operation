#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Pipeline Report Generator
Creates comprehensive HTML reports from review and quality gate results
"""

import argparse
import json
import sys
import os
from datetime import datetime
from typing import Dict, Any

# Fix Windows console encoding for emojis
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')


class ReportGenerator:
    """Generates HTML reports from pipeline results"""
    
    def __init__(self):
        self.template = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pipeline Report - {commit}</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            line-height: 1.6;
            color: #333;
            background: #f5f5f5;
            padding: 20px;
        }}
        
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }}
        
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }}
        
        .header h1 {{
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        .header .subtitle {{
            font-size: 1.1em;
            opacity: 0.9;
        }}
        
        .content {{
            padding: 30px;
        }}
        
        .section {{
            margin-bottom: 40px;
        }}
        
        .section-title {{
            font-size: 1.8em;
            color: #667eea;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 3px solid #667eea;
        }}
        
        .info-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        
        .info-card {{
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }}
        
        .info-card .label {{
            font-size: 0.9em;
            color: #666;
            margin-bottom: 5px;
        }}
        
        .info-card .value {{
            font-size: 1.2em;
            font-weight: bold;
            color: #333;
        }}
        
        .score-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        
        .score-card {{
            background: white;
            padding: 25px;
            border-radius: 8px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            border-top: 4px solid #667eea;
        }}
        
        .score-card .score-label {{
            font-size: 0.9em;
            color: #666;
            margin-bottom: 10px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }}
        
        .score-card .score-value {{
            font-size: 3em;
            font-weight: bold;
            margin-bottom: 5px;
        }}
        
        .score-card .score-max {{
            font-size: 1.2em;
            color: #999;
        }}
        
        .score-excellent {{ color: #28a745; border-top-color: #28a745; }}
        .score-good {{ color: #17a2b8; border-top-color: #17a2b8; }}
        .score-warning {{ color: #ffc107; border-top-color: #ffc107; }}
        .score-poor {{ color: #dc3545; border-top-color: #dc3545; }}
        
        .status-badge {{
            display: inline-block;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 1.1em;
            margin: 10px 0;
        }}
        
        .status-passed {{
            background: #d4edda;
            color: #155724;
            border: 2px solid #28a745;
        }}
        
        .status-warning {{
            background: #fff3cd;
            color: #856404;
            border: 2px solid #ffc107;
        }}
        
        .status-failed {{
            background: #f8d7da;
            color: #721c24;
            border: 2px solid #dc3545;
        }}
        
        .issues-list {{
            list-style: none;
        }}
        
        .issue-item {{
            background: white;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 6px;
            border-left: 4px solid #ccc;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }}
        
        .issue-critical {{ border-left-color: #dc3545; }}
        .issue-high {{ border-left-color: #fd7e14; }}
        .issue-medium {{ border-left-color: #ffc107; }}
        .issue-low {{ border-left-color: #17a2b8; }}
        
        .issue-severity {{
            display: inline-block;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.85em;
            font-weight: bold;
            margin-right: 10px;
        }}
        
        .severity-critical {{ background: #dc3545; color: white; }}
        .severity-high {{ background: #fd7e14; color: white; }}
        .severity-medium {{ background: #ffc107; color: #333; }}
        .severity-low {{ background: #17a2b8; color: white; }}
        
        .issue-file {{
            color: #667eea;
            font-family: monospace;
            font-size: 0.9em;
        }}
        
        .issue-message {{
            margin: 10px 0;
            color: #333;
        }}
        
        .issue-recommendation {{
            background: #f8f9fa;
            padding: 10px;
            border-radius: 4px;
            margin-top: 10px;
            font-size: 0.9em;
            color: #666;
        }}
        
        .recommendations {{
            background: #e7f3ff;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }}
        
        .recommendations ul {{
            margin-left: 20px;
        }}
        
        .recommendations li {{
            margin: 10px 0;
            color: #333;
        }}
        
        .footer {{
            background: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #666;
            font-size: 0.9em;
        }}
        
        .progress-bar {{
            width: 100%;
            height: 30px;
            background: #e9ecef;
            border-radius: 15px;
            overflow: hidden;
            margin: 10px 0;
        }}
        
        .progress-fill {{
            height: 100%;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            transition: width 0.3s ease;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 Pipeline Report</h1>
            <div class="subtitle">watsonx.ai Code Review & Quality Gate Analysis</div>
        </div>
        
        <div class="content">
            <!-- Build Information -->
            <div class="section">
                <h2 class="section-title">📋 Build Information</h2>
                <div class="info-grid">
                    <div class="info-card">
                        <div class="label">Commit</div>
                        <div class="value">{commit}</div>
                    </div>
                    <div class="info-card">
                        <div class="label">Author</div>
                        <div class="value">{author}</div>
                    </div>
                    <div class="info-card">
                        <div class="label">Timestamp</div>
                        <div class="value">{timestamp}</div>
                    </div>
                    <div class="info-card">
                        <div class="label">Review Depth</div>
                        <div class="value">{review_depth}</div>
                    </div>
                </div>
            </div>
            
            <!-- Quality Scores -->
            <div class="section">
                <h2 class="section-title">📊 Quality Scores</h2>
                <div class="score-grid">
                    {score_cards}
                </div>
            </div>
            
            <!-- Quality Gate Status -->
            <div class="section">
                <h2 class="section-title">🚦 Quality Gate Status</h2>
                <div style="text-align: center;">
                    <div class="status-badge status-{gate_status_class}">{gate_status_icon} {gate_status}</div>
                    <p style="margin: 20px 0; font-size: 1.1em;">{gate_message}</p>
                </div>
                {gate_details}
            </div>
            
            <!-- Issues Found -->
            <div class="section">
                <h2 class="section-title">🔍 Issues Found</h2>
                {issues_content}
            </div>
            
            <!-- Recommendations -->
            <div class="section">
                <h2 class="section-title">💡 Recommendations</h2>
                <div class="recommendations">
                    <ul>
                        {recommendations}
                    </ul>
                </div>
            </div>
            
            <!-- Summary -->
            <div class="section">
                <h2 class="section-title">📝 Summary</h2>
                <p style="font-size: 1.1em; line-height: 1.8;">{summary}</p>
            </div>
        </div>
        
        <div class="footer">
            Generated by watsonx.ai Pipeline Integration | {generation_time}
        </div>
    </div>
</body>
</html>
"""
    
    def get_score_class(self, score: int) -> str:
        """Get CSS class based on score"""
        if score >= 85:
            return "score-excellent"
        elif score >= 70:
            return "score-good"
        elif score >= 50:
            return "score-warning"
        else:
            return "score-poor"
    
    def generate_score_card(self, label: str, score: int) -> str:
        """Generate HTML for a score card"""
        score_class = self.get_score_class(score)
        return f"""
                    <div class="score-card">
                        <div class="score-label">{label}</div>
                        <div class="score-value {score_class}">{score}</div>
                        <div class="score-max">/100</div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: {score}%">{score}%</div>
                        </div>
                    </div>
"""
    
    def generate_issue_html(self, issue: Dict[str, Any]) -> str:
        """Generate HTML for an issue"""
        severity = issue.get('severity', 'MEDIUM').upper()
        severity_class = f"severity-{severity.lower()}"
        issue_class = f"issue-{severity.lower()}"
        
        html = f"""
                <li class="issue-item {issue_class}">
                    <div>
                        <span class="issue-severity {severity_class}">{severity}</span>
                        <span class="issue-file">{issue.get('file', 'N/A')}</span>
                    </div>
                    <div class="issue-message">{issue.get('message', 'No description')}</div>
"""
        
        if issue.get('recommendation'):
            html += f"""
                    <div class="issue-recommendation">
                        <strong>💡 Recommendation:</strong> {issue['recommendation']}
                    </div>
"""
        
        html += "                </li>\n"
        return html
    
    def generate_report(self, review_data: Dict[str, Any], gate_data: Dict[str, Any], 
                       commit: str, author: str, output_file: str):
        """Generate comprehensive HTML report"""
        
        # Generate score cards
        scores = review_data.get('scores', {})
        score_cards = ""
        score_cards += self.generate_score_card("Code Quality", scores.get('code_quality', 0))
        score_cards += self.generate_score_card("Security", scores.get('security', 0))
        score_cards += self.generate_score_card("Maintainability", scores.get('maintainability', 0))
        score_cards += self.generate_score_card("Overall", scores.get('overall', 0))
        
        # Generate issues list
        issues = review_data.get('issues', [])
        if issues:
            issues_content = f"""
                <p style="margin-bottom: 20px;">Found <strong>{len(issues)}</strong> issues requiring attention:</p>
                <ul class="issues-list">
"""
            for issue in issues:
                issues_content += self.generate_issue_html(issue)
            issues_content += "                </ul>"
        else:
            issues_content = '<p style="text-align: center; padding: 40px; color: #28a745; font-size: 1.2em;">✅ No issues found! Excellent work!</p>'
        
        # Generate recommendations
        recommendations_list = review_data.get('recommendations', [])
        recommendations = "\n".join([f"<li>{rec}</li>" for rec in recommendations_list])
        
        # Quality gate details
        gate_status = gate_data.get('status', 'UNKNOWN')
        gate_status_class = gate_status.lower()
        gate_status_icons = {
            'PASSED': '✅',
            'WARNING': '⚠️',
            'FAILED': '❌'
        }
        gate_status_icon = gate_status_icons.get(gate_status, '❓')
        
        gate_details = ""
        if gate_data.get('details'):
            gate_details = '<div style="margin-top: 30px;">'
            for metric, detail in gate_data['details'].items():
                metric_name = metric.replace('_', ' ').title()
                status_icon = gate_status_icons.get(detail['status'], '❓')
                gate_details += f"""
                <div class="info-card" style="margin-bottom: 15px;">
                    <div class="label">{status_icon} {metric_name}</div>
                    <div class="value">Score: {detail['score']}/100 | Threshold: {detail['threshold']}/100</div>
                </div>
"""
            gate_details += '</div>'
        
        # Fill template
        html = self.template.format(
            commit=commit,
            author=author,
            timestamp=review_data.get('timestamp', 'N/A'),
            review_depth=review_data.get('review_depth', 'STANDARD'),
            score_cards=score_cards,
            gate_status=gate_status,
            gate_status_class=gate_status_class,
            gate_status_icon=gate_status_icon,
            gate_message=gate_data.get('message', 'No message'),
            gate_details=gate_details,
            issues_content=issues_content,
            recommendations=recommendations,
            summary=review_data.get('summary', 'No summary available'),
            generation_time=datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S UTC')
        )
        
        # Write to file
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(html)
        
        print(f"✅ Report generated: {output_file}")


def main():
    parser = argparse.ArgumentParser(description="Generate Pipeline Report")
    parser.add_argument("--review-file", required=True, help="Review report JSON file")
    parser.add_argument("--quality-gate-file", required=True, help="Quality gate result JSON file")
    parser.add_argument("--commit", required=True, help="Git commit hash")
    parser.add_argument("--author", required=True, help="Commit author")
    parser.add_argument("--output-file", default="pipeline-report.html", help="Output HTML file")
    
    args = parser.parse_args()
    
    # Load data
    try:
        with open(args.review_file, 'r') as f:
            review_data = json.load(f)
    except Exception as e:
        print(f"Error loading review file: {e}")
        return 1
    
    try:
        with open(args.quality_gate_file, 'r') as f:
            gate_data = json.load(f)
    except Exception as e:
        print(f"Error loading quality gate file: {e}")
        return 1
    
    # Generate report
    generator = ReportGenerator()
    generator.generate_report(review_data, gate_data, args.commit, args.author, args.output_file)
    
    return 0


if __name__ == "__main__":
    sys.exit(main())

# Made with Bob
