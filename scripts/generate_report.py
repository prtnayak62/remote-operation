#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Pipeline Report Generator with Pure CSS/SVG Charts
Creates comprehensive HTML reports with visual graphs that work in Jenkins
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
    """Generates HTML reports with CSS/SVG charts from pipeline results"""
    
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
            max-width: 1400px;
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
        
        .chart-container {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 30px;
            margin-bottom: 30px;
        }}
        
        .chart-box {{
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }}
        
        .chart-box h3 {{
            text-align: center;
            color: #667eea;
            margin-bottom: 20px;
        }}
        
        /* CSS Bar Chart */
        .bar-chart {{
            display: flex;
            justify-content: space-around;
            align-items: flex-end;
            height: 300px;
            padding: 20px;
            border-bottom: 2px solid #ddd;
            position: relative;
        }}
        
        .bar-chart::before {{
            content: '100';
            position: absolute;
            left: 0;
            top: 0;
            font-size: 0.8em;
            color: #999;
        }}
        
        .bar-chart::after {{
            content: '0';
            position: absolute;
            left: 0;
            bottom: 0;
            font-size: 0.8em;
            color: #999;
        }}
        
        .bar {{
            flex: 1;
            margin: 0 10px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: flex-end;
        }}
        
        .bar-fill {{
            width: 100%;
            background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
            border-radius: 4px 4px 0 0;
            transition: all 0.3s ease;
            position: relative;
            display: flex;
            align-items: flex-start;
            justify-content: center;
            padding-top: 10px;
        }}
        
        .bar-fill:hover {{
            opacity: 0.8;
        }}
        
        .bar-value {{
            color: white;
            font-weight: bold;
            font-size: 1.2em;
        }}
        
        .bar-label {{
            margin-top: 10px;
            font-size: 0.85em;
            color: #666;
            text-align: center;
            word-wrap: break-word;
        }}
        
        /* SVG Radar Chart */
        .radar-chart {{
            width: 100%;
            height: 300px;
            display: flex;
            justify-content: center;
            align-items: center;
        }}
        
        .radar-chart svg {{
            max-width: 100%;
            max-height: 100%;
        }}
        
        .radar-grid {{
            fill: none;
            stroke: #ddd;
            stroke-width: 1;
        }}
        
        .radar-axis {{
            stroke: #999;
            stroke-width: 1;
        }}
        
        .radar-label {{
            font-size: 12px;
            fill: #666;
        }}
        
        .radar-area-actual {{
            fill: rgba(102, 126, 234, 0.3);
            stroke: #667eea;
            stroke-width: 2;
        }}
        
        .radar-area-threshold {{
            fill: rgba(220, 53, 69, 0.2);
            stroke: #dc3545;
            stroke-width: 2;
            stroke-dasharray: 5,5;
        }}
        
        .radar-point {{
            fill: #667eea;
            stroke: white;
            stroke-width: 2;
        }}
        
        .radar-legend {{
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 15px;
            font-size: 0.9em;
        }}
        
        .legend-item {{
            display: flex;
            align-items: center;
            gap: 8px;
        }}
        
        .legend-color {{
            width: 20px;
            height: 12px;
            border-radius: 2px;
        }}
        
        .legend-actual {{
            background: #667eea;
        }}
        
        .legend-threshold {{
            background: #dc3545;
        }}
        
        .status-badge {{
            display: inline-block;
            padding: 12px 24px;
            border-radius: 25px;
            font-weight: bold;
            font-size: 1.3em;
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
        
        .metrics-comparison {{
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-top: 20px;
        }}
        
        .metric-row {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            margin: 5px 0;
            background: white;
            border-radius: 4px;
        }}
        
        .metric-name {{
            font-weight: bold;
            color: #667eea;
        }}
        
        .metric-values {{
            display: flex;
            gap: 20px;
        }}
        
        .metric-score {{
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
        }}
        
        .metric-threshold {{
            color: #666;
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
                <h2 class="section-title">Build Information</h2>
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
            
            <!-- Quality Scores with Charts -->
            <div class="section">
                <h2 class="section-title">Quality Scores</h2>
                
                <!-- Score Cards -->
                <div class="score-grid">
{score_cards}
                </div>
                
                <!-- Charts -->
                <div class="chart-container">
                    <div class="chart-box">
                        <h3>Score Distribution</h3>
                        <div class="bar-chart">
{bar_chart}
                        </div>
                    </div>
                    <div class="chart-box">
                        <h3>Quality vs Thresholds</h3>
                        <div class="radar-chart">
{radar_chart}
                        </div>
                        <div class="radar-legend">
                            <div class="legend-item">
                                <div class="legend-color legend-actual"></div>
                                <span>Actual Score</span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-color legend-threshold"></div>
                                <span>Threshold</span>
                            </div>
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
                
                <!-- Metrics Comparison -->
                <div class="metrics-comparison">
                    <h3 style="margin-bottom: 15px; color: #667eea;">Detailed Metrics</h3>
{gate_details}
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
    
    def generate_bar_chart(self, scores: Dict[str, int]) -> str:
        """Generate CSS bar chart"""
        labels = ['Code Quality', 'Security', 'Maintainability', 'Overall']
        values = [
            scores.get('code_quality', 0),
            scores.get('security', 0),
            scores.get('maintainability', 0),
            scores.get('overall', 0)
        ]
        
        bars = ""
        for label, value in zip(labels, values):
            height_percent = value * 3  # Scale to 300px max height
            bars += f"""
                            <div class="bar">
                                <div class="bar-fill" style="height: {height_percent}px;">
                                    <span class="bar-value">{value}</span>
                                </div>
                                <div class="bar-label">{label}</div>
                            </div>
"""
        return bars
    
    def generate_radar_chart(self, scores: Dict[str, int], thresholds: Dict[str, int]) -> str:
        """Generate SVG radar chart"""
        import math
        
        # Chart dimensions
        size = 300
        center = size / 2
        radius = size / 2 - 40
        
        # Metrics
        metrics = ['Code Quality', 'Security', 'Maintainability']
        actual_values = [
            scores.get('code_quality', 0),
            scores.get('security', 0),
            scores.get('maintainability', 0)
        ]
        threshold_values = [
            thresholds.get('code_quality', 70),
            thresholds.get('security', 80),
            thresholds.get('maintainability', 60)
        ]
        
        # Calculate points
        def get_point(value, index, num_points):
            angle = (2 * math.pi * index / num_points) - (math.pi / 2)
            r = (value / 100) * radius
            x = center + r * math.cos(angle)
            y = center + r * math.sin(angle)
            return x, y
        
        # Generate grid circles
        grid_circles = ""
        for i in range(1, 6):
            r = radius * i / 5
            grid_circles += f'<circle cx="{center}" cy="{center}" r="{r}" class="radar-grid"/>\n'
        
        # Generate axes and labels
        axes = ""
        labels = ""
        for i, metric in enumerate(metrics):
            x, y = get_point(100, i, len(metrics))
            axes += f'<line x1="{center}" y1="{center}" x2="{x}" y2="{y}" class="radar-axis"/>\n'
            
            # Label position (slightly outside)
            label_x, label_y = get_point(110, i, len(metrics))
            # Adjust text anchor based on position
            anchor = "middle"
            if label_x < center - 10:
                anchor = "end"
            elif label_x > center + 10:
                anchor = "start"
            
            labels += f'<text x="{label_x}" y="{label_y}" class="radar-label" text-anchor="{anchor}">{metric}</text>\n'
        
        # Generate actual score polygon
        actual_points = []
        for i, value in enumerate(actual_values):
            x, y = get_point(value, i, len(actual_values))
            actual_points.append(f"{x},{y}")
        actual_polygon = f'<polygon points="{" ".join(actual_points)}" class="radar-area-actual"/>\n'
        
        # Generate threshold polygon
        threshold_points = []
        for i, value in enumerate(threshold_values):
            x, y = get_point(value, i, len(threshold_values))
            threshold_points.append(f"{x},{y}")
        threshold_polygon = f'<polygon points="{" ".join(threshold_points)}" class="radar-area-threshold"/>\n'
        
        # Generate points
        points_svg = ""
        for i, value in enumerate(actual_values):
            x, y = get_point(value, i, len(actual_values))
            points_svg += f'<circle cx="{x}" cy="{y}" r="4" class="radar-point"/>\n'
        
        svg = f"""
                            <svg width="{size}" height="{size}" viewBox="0 0 {size} {size}">
                                {grid_circles}
                                {axes}
                                {threshold_polygon}
                                {actual_polygon}
                                {points_svg}
                                {labels}
                            </svg>
"""
        return svg
    
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
                        <strong>Recommendation:</strong> {issue['recommendation']}
                    </div>
"""
        
        html += "                </li>\n"
        return html
    
    def generate_report(self, review_data: Dict[str, Any], gate_data: Dict[str, Any], 
                       commit: str, author: str, output_file: str):
        """Generate comprehensive HTML report with CSS/SVG charts"""
        
        # Generate score cards - USE ACTUAL DATA FROM review_data
        scores = review_data.get('scores', {})
        score_cards = ""
        score_cards += self.generate_score_card("Code Quality", scores.get('code_quality', 0))
        score_cards += self.generate_score_card("Security", scores.get('security', 0))
        score_cards += self.generate_score_card("Maintainability", scores.get('maintainability', 0))
        score_cards += self.generate_score_card("Overall", scores.get('overall', 0))
        
        # Generate charts
        bar_chart = self.generate_bar_chart(scores)
        thresholds = gate_data.get('thresholds', {})
        radar_chart = self.generate_radar_chart(scores, thresholds)
        
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
        recommendations = "\n".join([f"                        <li>{rec}</li>" for rec in recommendations_list])
        
        # Quality gate details - USE ACTUAL DATA FROM gate_data
        gate_status = gate_data.get('status', 'UNKNOWN')
        gate_status_class = gate_status.lower()
        gate_status_icons = {
            'PASSED': '✅',
            'WARNING': '⚠️',
            'FAILED': '❌'
        }
        gate_status_icon = gate_status_icons.get(gate_status, '❓')
        
        # Generate detailed metrics comparison
        gate_details = ""
        if gate_data.get('details'):
            for metric, detail in gate_data['details'].items():
                metric_name = metric.replace('_', ' ').title()
                status = detail.get('status', 'UNKNOWN')
                score = detail.get('score', 0)
                threshold = detail.get('threshold', 0)
                
                # Determine status icon and color
                if status == 'PASSED':
                    status_icon = '✅'
                    score_class = 'score-excellent'
                elif status == 'WARNING':
                    status_icon = '⚠️'
                    score_class = 'score-warning'
                else:
                    status_icon = '❌'
                    score_class = 'score-poor'
                
                gate_details += f"""
                    <div class="metric-row">
                        <div class="metric-name">{status_icon} {metric_name}</div>
                        <div class="metric-values">
                            <div class="metric-score {score_class}">Score: {score}/100</div>
                            <div class="metric-threshold">Threshold: {threshold}/100</div>
                        </div>
                    </div>
"""
        
        # Fill template with ACTUAL data
        html = self.template.format(
            commit=commit,
            author=author if author and author != "ECHO is off." else "Unknown",
            timestamp=review_data.get('timestamp', 'N/A'),
            review_depth=review_data.get('review_depth', 'STANDARD'),
            score_cards=score_cards,
            bar_chart=bar_chart,
            radar_chart=radar_chart,
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
        print(f"📊 Scores: Code Quality={scores.get('code_quality', 0)}, Security={scores.get('security', 0)}, Maintainability={scores.get('maintainability', 0)}")
        print(f"🚦 Quality Gate: {gate_status}")


def main():
    parser = argparse.ArgumentParser(description="Generate Pipeline Report with CSS/SVG Charts")
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
