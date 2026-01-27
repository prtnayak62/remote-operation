#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Pipeline Report Generator - Jenkins Compatible
Creates HTML reports with simple visual elements that work in Jenkins
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
    """Generates HTML reports with simple charts from pipeline results"""
    
    def __init__(self):
        self.template = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pipeline Report - {commit}</title>
    <style>
        body {{
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            background: #f5f5f5;
            padding: 20px;
            margin: 0;
        }}
        
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }}
        
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
            border-radius: 8px 8px 0 0;
        }}
        
        .header h1 {{
            font-size: 2em;
            margin: 0 0 10px 0;
        }}
        
        .content {{
            padding: 30px;
        }}
        
        .section {{
            margin-bottom: 40px;
        }}
        
        .section-title {{
            font-size: 1.5em;
            color: #667eea;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 3px solid #667eea;
        }}
        
        .info-grid {{
            display: table;
            width: 100%;
            margin-bottom: 20px;
        }}
        
        .info-row {{
            display: table-row;
        }}
        
        .info-cell {{
            display: table-cell;
            padding: 15px;
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            vertical-align: top;
        }}
        
        .info-label {{
            font-weight: bold;
            color: #666;
            margin-bottom: 5px;
        }}
        
        .info-value {{
            font-size: 1.1em;
            color: #333;
        }}
        
        .score-table {{
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
        }}
        
        .score-table th {{
            background: #667eea;
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: bold;
        }}
        
        .score-table td {{
            padding: 15px;
            border: 1px solid #dee2e6;
        }}
        
        .score-table tr:nth-child(even) {{
            background: #f8f9fa;
        }}
        
        .score-bar {{
            width: 100%;
            height: 30px;
            background: #e9ecef;
            border-radius: 15px;
            overflow: hidden;
            position: relative;
        }}
        
        .score-bar-fill {{
            height: 100%;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            font-size: 0.9em;
        }}
        
        .score-excellent {{ background: linear-gradient(90deg, #28a745 0%, #20c997 100%) !important; }}
        .score-good {{ background: linear-gradient(90deg, #17a2b8 0%, #20c997 100%) !important; }}
        .score-warning {{ background: linear-gradient(90deg, #ffc107 0%, #fd7e14 100%) !important; }}
        .score-poor {{ background: linear-gradient(90deg, #dc3545 0%, #c82333 100%) !important; }}
        
        .chart-grid {{
            display: table;
            width: 100%;
            margin: 20px 0;
        }}
        
        .chart-cell {{
            display: table-cell;
            width: 50%;
            padding: 20px;
            vertical-align: top;
        }}
        
        .chart-box {{
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border: 2px solid #dee2e6;
        }}
        
        .chart-title {{
            text-align: center;
            font-size: 1.2em;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 20px;
        }}
        
        .bar-chart-simple {{
            display: table;
            width: 100%;
            border-collapse: collapse;
        }}
        
        .bar-row {{
            display: table-row;
        }}
        
        .bar-label-cell {{
            display: table-cell;
            padding: 10px;
            font-weight: bold;
            width: 30%;
            vertical-align: middle;
        }}
        
        .bar-cell {{
            display: table-cell;
            padding: 10px;
            width: 70%;
            vertical-align: middle;
        }}
        
        .comparison-table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        
        .comparison-table th {{
            background: #667eea;
            color: white;
            padding: 12px;
            text-align: left;
        }}
        
        .comparison-table td {{
            padding: 12px;
            border: 1px solid #dee2e6;
        }}
        
        .status-badge {{
            display: inline-block;
            padding: 10px 20px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 1.2em;
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
        
        .issue-list {{
            list-style: none;
            padding: 0;
        }}
        
        .issue-item {{
            background: white;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 6px;
            border-left: 4px solid #ccc;
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
        
        .recommendations {{
            background: #e7f3ff;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }}
        
        .footer {{
            background: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #666;
            border-radius: 0 0 8px 8px;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Pipeline Report</h1>
            <div>watsonx.ai Code Review & Quality Gate Analysis</div>
        </div>
        
        <div class="content">
            <!-- Build Information -->
            <div class="section">
                <h2 class="section-title">Build Information</h2>
                <div class="info-grid">
                    <div class="info-row">
                        <div class="info-cell">
                            <div class="info-label">Commit</div>
                            <div class="info-value">{commit}</div>
                        </div>
                        <div class="info-cell">
                            <div class="info-label">Author</div>
                            <div class="info-value">{author}</div>
                        </div>
                        <div class="info-cell">
                            <div class="info-label">Timestamp</div>
                            <div class="info-value">{timestamp}</div>
                        </div>
                        <div class="info-cell">
                            <div class="info-label">Review Depth</div>
                            <div class="info-value">{review_depth}</div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Quality Scores -->
            <div class="section">
                <h2 class="section-title">Quality Scores</h2>
                <table class="score-table">
                    <tr>
                        <th>Metric</th>
                        <th>Score</th>
                        <th>Visual</th>
                    </tr>
{score_rows}
                </table>
            </div>
            
            <!-- Charts -->
            <div class="section">
                <h2 class="section-title">Score Visualization</h2>
                <div class="chart-grid">
                    <div class="chart-cell">
                        <div class="chart-box">
                            <div class="chart-title">Score Distribution</div>
                            <div class="bar-chart-simple">
{bar_chart}
                            </div>
                        </div>
                    </div>
                    <div class="chart-cell">
                        <div class="chart-box">
                            <div class="chart-title">Quality vs Thresholds</div>
                            <table class="comparison-table">
                                <tr>
                                    <th>Metric</th>
                                    <th>Score</th>
                                    <th>Threshold</th>
                                    <th>Status</th>
                                </tr>
{comparison_rows}
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Quality Gate Status -->
            <div class="section">
                <h2 class="section-title">Quality Gate Status</h2>
                <div style="text-align: center;">
                    <div class="status-badge status-{gate_status_class}">{gate_status_icon} {gate_status}</div>
                    <p style="margin: 20px 0; font-size: 1.1em;">{gate_message}</p>
                </div>
            </div>
            
            <!-- Issues Found -->
            <div class="section">
                <h2 class="section-title">Issues Found</h2>
{issues_content}
            </div>
            
            <!-- Recommendations -->
            <div class="section">
                <h2 class="section-title">Recommendations</h2>
                <div class="recommendations">
                    <ul>
{recommendations}
                    </ul>
                </div>
            </div>
            
            <!-- Summary -->
            <div class="section">
                <h2 class="section-title">Summary</h2>
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
        """Determine CSS class based on score"""
        if score >= 85:
            return "score-excellent"
        elif score >= 70:
            return "score-good"
        elif score >= 50:
            return "score-warning"
        else:
            return "score-poor"
    
    def generate_score_rows(self, scores: Dict[str, int]) -> str:
        """Generate table rows for scores"""
        metrics = [
            ('Code Quality', scores.get('code_quality', 0)),
            ('Security', scores.get('security', 0)),
            ('Maintainability', scores.get('maintainability', 0)),
            ('Overall', scores.get('overall', 0))
        ]
        
        rows = ""
        for label, score in metrics:
            score_class = self.get_score_class(score)
            rows += f"""
                    <tr>
                        <td><strong>{label}</strong></td>
                        <td><strong>{score}/100</strong></td>
                        <td>
                            <div class="score-bar">
                                <div class="score-bar-fill {score_class}" style="width: {score}%;">
                                    {score}%
                                </div>
                            </div>
                        </td>
                    </tr>
"""
        return rows
    
    def generate_bar_chart(self, scores: Dict[str, int]) -> str:
        """Generate simple bar chart using table"""
        metrics = [
            ('Code Quality', scores.get('code_quality', 0)),
            ('Security', scores.get('security', 0)),
            ('Maintainability', scores.get('maintainability', 0)),
            ('Overall', scores.get('overall', 0))
        ]
        
        bars = ""
        for label, score in metrics:
            score_class = self.get_score_class(score)
            bars += f"""
                                <div class="bar-row">
                                    <div class="bar-label-cell">{label}</div>
                                    <div class="bar-cell">
                                        <div class="score-bar">
                                            <div class="score-bar-fill {score_class}" style="width: {score}%;">
                                                {score}
                                            </div>
                                        </div>
                                    </div>
                                </div>
"""
        return bars
    
    def generate_comparison_rows(self, scores: Dict[str, int], thresholds: Dict[str, int], details: Dict[str, Any]) -> str:
        """Generate comparison table rows"""
        metrics = [
            ('Code Quality', 'code_quality'),
            ('Security', 'security'),
            ('Maintainability', 'maintainability')
        ]
        
        rows = ""
        for label, key in metrics:
            score = scores.get(key, 0)
            threshold = thresholds.get(key, 0)
            detail = details.get(key, {}) if details else {}
            status = detail.get('status', 'UNKNOWN')
            
            if status == 'PASSED':
                status_icon = '✅'
                status_color = '#28a745'
            elif status == 'WARNING':
                status_icon = '⚠️'
                status_color = '#ffc107'
            else:
                status_icon = '❌'
                status_color = '#dc3545'
            
            rows += f"""
                                <tr>
                                    <td><strong>{label}</strong></td>
                                    <td><strong>{score}/100</strong></td>
                                    <td>{threshold}/100</td>
                                    <td style="color: {status_color}; font-weight: bold;">{status_icon} {status}</td>
                                </tr>
"""
        return rows
    
    def generate_issue_html(self, issue: Dict[str, Any]) -> str:
        """Generate HTML for an issue"""
        severity = issue.get('severity', 'MEDIUM').upper()
        severity_class = f"severity-{severity.lower()}"
        issue_class = f"issue-{severity.lower()}"
        
        html = f"""
                <li class="issue-item {issue_class}">
                    <div>
                        <span class="issue-severity {severity_class}">{severity}</span>
                        <span style="color: #667eea; font-family: monospace;">{issue.get('file', 'N/A')}</span>
                    </div>
                    <div style="margin: 10px 0;">{issue.get('message', 'No description')}</div>
"""
        
        if issue.get('recommendation'):
            html += f"""
                    <div style="background: #f8f9fa; padding: 10px; border-radius: 4px; margin-top: 10px;">
                        <strong>Recommendation:</strong> {issue['recommendation']}
                    </div>
"""
        
        html += "                </li>\n"
        return html
    
    def generate_report(self, review_data: Dict[str, Any], gate_data: Dict[str, Any], 
                       commit: str, author: str, output_file: str):
        """Generate comprehensive HTML report"""
        
        # Get scores
        scores = review_data.get('scores', {})
        thresholds = gate_data.get('thresholds', {})
        details = gate_data.get('details', {})
        
        # Generate score rows
        score_rows = self.generate_score_rows(scores)
        
        # Generate bar chart
        bar_chart = self.generate_bar_chart(scores)
        
        # Generate comparison rows
        comparison_rows = self.generate_comparison_rows(scores, thresholds, details)
        
        # Generate issues list
        issues = review_data.get('issues', [])
        if issues:
            issues_content = f"""
                <p style="margin-bottom: 20px;">Found <strong>{len(issues)}</strong> issues requiring attention:</p>
                <ul class="issue-list">
"""
            for issue in issues:
                issues_content += self.generate_issue_html(issue)
            issues_content += "                </ul>"
        else:
            issues_content = '<p style="text-align: center; padding: 40px; color: #28a745; font-size: 1.2em;">No issues found! Excellent work!</p>'
        
        # Generate recommendations
        recommendations_list = review_data.get('recommendations', [])
        recommendations = "\n".join([f"                        <li>{rec}</li>" for rec in recommendations_list])
        
        # Quality gate details
        gate_status = gate_data.get('status', 'UNKNOWN')
        gate_status_class = gate_status.lower()
        gate_status_icons = {
            'PASSED': '✅',
            'WARNING': '⚠️',
            'FAILED': '❌'
        }
        gate_status_icon = gate_status_icons.get(gate_status, '❓')
        
        # Fill template
        html = self.template.format(
            commit=commit,
            author=author if author and author != "ECHO is off." else "Unknown",
            timestamp=review_data.get('timestamp', 'N/A'),
            review_depth=review_data.get('review_depth', 'STANDARD'),
            score_rows=score_rows,
            bar_chart=bar_chart,
            comparison_rows=comparison_rows,
            gate_status=gate_status,
            gate_status_class=gate_status_class,
            gate_status_icon=gate_status_icon,
            gate_message=gate_data.get('message', 'No message'),
            issues_content=issues_content,
            recommendations=recommendations,
            summary=review_data.get('summary', 'No summary available'),
            generation_time=datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S UTC')
        )
        
        # Write to file
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(html)
        
        print(f"✅ Report generated: {output_file}")
        print(f"📊 Scores: Code Quality={scores.get('code_quality', 0)}, Security={scores.get('security', 0)}, Maintainability={scores.get('maintainability', 0)}")
        print(f"🚦 Quality Gate: {gate_status}")


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
        with open(args.review_file, 'r', encoding='utf-8') as f:
            review_data = json.load(f)
        print(f"✅ Loaded review data from {args.review_file}")
    except Exception as e:
        print(f"❌ Error loading review file: {e}")
        return 1
    
    try:
        with open(args.quality_gate_file, 'r', encoding='utf-8') as f:
            gate_data = json.load(f)
        print(f"✅ Loaded quality gate data from {args.quality_gate_file}")
    except Exception as e:
        print(f"❌ Error loading quality gate file: {e}")
        return 1
    
    # Generate report
    generator = ReportGenerator()
    try:
        generator.generate_report(
            review_data=review_data,
            gate_data=gate_data,
            commit=args.commit,
            author=args.author,
            output_file=args.output_file
        )
        return 0
    except Exception as e:
        print(f"❌ Error generating report: {e}")
        import traceback
        traceback.print_exc()
        return 1


if __name__ == "__main__":
    sys.exit(main())

# Made with Bob
