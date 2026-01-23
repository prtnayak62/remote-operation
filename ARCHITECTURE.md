# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Jenkins Pipeline                            │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         Pipeline Stages                              │
├─────────────────────────────────────────────────────────────────────┤
│  1. Checkout          │  Get code from repository                   │
│  2. Pre-Build         │  Analyze changed files                      │
│  3. Code Review       │  watsonx.ai analysis                        │
│  4. Quality Gate      │  Evaluate thresholds                        │
│  5. Build             │  Compile/package                            │
│  6. Test              │  Run test suite                             │
│  7. Report            │  Generate HTML report                       │
└─────────────────────────────────────────────────────────────────────┘
```

## Component Interaction Flow

```
┌──────────────┐
│   Developer  │
│   Commits    │
└──────┬───────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────┐
│                        Jenkins Pipeline                           │
│                                                                   │
│  ┌─────────────┐                                                 │
│  │  Checkout   │                                                 │
│  │   Stage     │                                                 │
│  └──────┬──────┘                                                 │
│         │                                                         │
│         ▼                                                         │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │         watsonx_code_review.py                          │   │
│  │  ┌──────────────────────────────────────────────────┐  │   │
│  │  │  1. Get changed files from Git                   │  │   │
│  │  │  2. Prepare code for analysis                    │  │   │
│  │  │  3. Call watsonx.ai API                          │  │   │
│  │  │  4. Parse AI response                            │  │   │
│  │  │  5. Calculate scores                             │  │   │
│  │  │  6. Generate review-report.json                  │  │   │
│  │  └──────────────────────────────────────────────────┘  │   │
│  └─────────────────┬───────────────────────────────────────┘   │
│                    │                                             │
│                    ▼                                             │
│         ┌──────────────────┐                                    │
│         │  watsonx.ai API  │                                    │
│         │  ┌────────────┐  │                                    │
│         │  │  Granite   │  │                                    │
│         │  │   Model    │  │                                    │
│         │  └────────────┘  │                                    │
│         └──────────────────┘                                    │
│                    │                                             │
│                    ▼                                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            quality_gate.py                              │   │
│  │  ┌──────────────────────────────────────────────────┐  │   │
│  │  │  1. Load review scores                           │  │   │
│  │  │  2. Compare against thresholds                   │  │   │
│  │  │  3. Evaluate each metric                         │  │   │
│  │  │  4. Determine PASS/WARNING/FAIL                  │  │   │
│  │  │  5. Generate quality-gate-result.json            │  │   │
│  │  └──────────────────────────────────────────────────┘  │   │
│  └─────────────────┬───────────────────────────────────────┘   │
│                    │                                             │
│                    ▼                                             │
│            ┌───────────────┐                                    │
│            │ Quality Gate  │                                    │
│            │   Decision    │                                    │
│            └───────┬───────┘                                    │
│                    │                                             │
│         ┌──────────┴──────────┐                                │
│         │                     │                                 │
│    ┌────▼────┐          ┌────▼────┐                           │
│    │  PASS   │          │  FAIL   │                           │
│    │ Continue│          │  Block  │                           │
│    └────┬────┘          └────┬────┘                           │
│         │                     │                                 │
│         ▼                     ▼                                 │
│  ┌─────────────┐      ┌─────────────┐                         │
│  │   Build     │      │   Report    │                         │
│  │   Stage     │      │   & Fail    │                         │
│  └──────┬──────┘      └─────────────┘                         │
│         │                                                       │
│         ▼                                                       │
│  ┌─────────────┐                                               │
│  │    Test     │                                               │
│  │   Stage     │                                               │
│  └──────┬──────┘                                               │
│         │                                                       │
│         ▼                                                       │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │           generate_report.py                            │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │  1. Load review and quality gate results         │  │  │
│  │  │  2. Generate HTML with scores and issues         │  │  │
│  │  │  3. Create visual charts and graphs              │  │  │
│  │  │  4. Output pipeline-report.html                  │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └─────────────────┬───────────────────────────────────────┘  │
│                    │                                            │
└────────────────────┼────────────────────────────────────────────┘
                     │
                     ▼
              ┌─────────────┐
              │   Report    │
              │  Published  │
              │ in Jenkins  │
              └─────────────┘
```

## Data Flow

```
Git Repository
      │
      ▼
┌─────────────┐
│ Changed     │
│ Files       │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│  watsonx_code_review.py     │
│  ┌───────────────────────┐  │
│  │ Code Analysis         │  │
│  │ - Parse files         │  │
│  │ - Extract diffs       │  │
│  │ - Prepare prompts     │  │
│  └───────────────────────┘  │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│    watsonx.ai API           │
│  ┌───────────────────────┐  │
│  │ AI Model Processing   │  │
│  │ - Analyze code        │  │
│  │ - Identify issues     │  │
│  │ - Calculate scores    │  │
│  └───────────────────────┘  │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  review-report.json         │
│  {                          │
│    "scores": {...},         │
│    "issues": [...],         │
│    "recommendations": [...]  │
│  }                          │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│    quality_gate.py          │
│  ┌───────────────────────┐  │
│  │ Threshold Evaluation  │  │
│  │ - Compare scores      │  │
│  │ - Check limits        │  │
│  │ - Determine status    │  │
│  └───────────────────────┘  │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│ quality-gate-result.json    │
│  {                          │
│    "status": "PASSED",      │
│    "details": {...}         │
│  }                          │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│   generate_report.py        │
│  ┌───────────────────────┐  │
│  │ HTML Generation       │  │
│  │ - Format data         │  │
│  │ - Create visuals      │  │
│  │ - Style report        │  │
│  └───────────────────────┘  │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  pipeline-report.html       │
│  (Published in Jenkins)     │
└─────────────────────────────┘
```

## Quality Gate Decision Logic

```
┌─────────────────────────────────────────────────────────────┐
│                    Quality Gate Evaluation                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  Load Scores    │
                    │  & Thresholds   │
                    └────────┬────────┘
                             │
                             ▼
              ┌──────────────────────────────┐
              │  For Each Metric:            │
              │  - Code Quality              │
              │  - Security                  │
              │  - Maintainability           │
              └──────────┬───────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │  Score >= Threshold? │
              └──────────┬───────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
    ┌────────┐    ┌──────────┐    ┌─────────┐
    │  YES   │    │ CLOSE    │    │   NO    │
    │        │    │ (within  │    │         │
    │        │    │ 10 pts)  │    │         │
    └───┬────┘    └────┬─────┘    └────┬────┘
        │              │               │
        ▼              ▼               ▼
    ┌────────┐    ┌──────────┐    ┌─────────┐
    │ PASSED │    │ WARNING  │    │ FAILED  │
    └───┬────┘    └────┬─────┘    └────┬────┘
        │              │               │
        └──────────────┼───────────────┘
                       │
                       ▼
              ┌─────────────────┐
              │  Any FAILED?    │
              └────────┬────────┘
                       │
              ┌────────┴────────┐
              │                 │
              ▼                 ▼
         ┌─────────┐       ┌─────────┐
         │   YES   │       │   NO    │
         └────┬────┘       └────┬────┘
              │                 │
              ▼                 ▼
    ┌──────────────────┐  ┌──────────────────┐
    │  Quality Gate    │  │  Any WARNING?    │
    │     FAILED       │  └────────┬─────────┘
    │  Block Build     │           │
    └──────────────────┘  ┌────────┴────────┐
                          │                 │
                          ▼                 ▼
                     ┌─────────┐       ┌─────────┐
                     │   YES   │       │   NO    │
                     └────┬────┘       └────┬────┘
                          │                 │
                          ▼                 ▼
                ┌──────────────────┐  ┌──────────────────┐
                │  Quality Gate    │  │  Quality Gate    │
                │    WARNING       │  │     PASSED       │
                │ Continue Build   │  │ Continue Build   │
                └──────────────────┘  └──────────────────┘
```

## Configuration Management

```
┌─────────────────────────────────────────────────────────┐
│                  Configuration Files                     │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  config/quality-thresholds.json                         │
│  ├── Threshold values                                   │
│  ├── Profiles (strict/standard/lenient)                 │
│  ├── Issue limits                                       │
│  └── Enforcement rules                                  │
│                                                          │
│  config/watsonx-config.json                             │
│  ├── API configuration                                  │
│  ├── Model settings                                     │
│  ├── Review settings                                    │
│  ├── Analysis rules                                     │
│  └── File filters                                       │
│                                                          │
│  Jenkins Credentials                                    │
│  ├── watsonx-api-key (Secret)                          │
│  └── watsonx-project-id (Secret)                       │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Security Layers                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Layer 1: Credential Management                         │
│  ├── Jenkins Credentials Store                          │
│  ├── Encrypted at rest                                  │
│  └── Access via credentials binding                     │
│                                                          │
│  Layer 2: API Communication                             │
│  ├── HTTPS/TLS encryption                               │
│  ├── Bearer token authentication                        │
│  └── Request/response validation                        │
│                                                          │
│  Layer 3: Code Analysis                                 │
│  ├── No code stored in watsonx.ai                       │
│  ├── Temporary processing only                          │
│  └── Results stored locally                             │
│                                                          │
│  Layer 4: Access Control                                │
│  ├── Jenkins role-based access                          │
│  ├── Audit logging                                      │
│  └── Build history retention                            │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Scalability Considerations

### Horizontal Scaling
- Multiple Jenkins agents can run pipelines in parallel
- Each agent has independent Python environment
- watsonx.ai API handles concurrent requests

### Performance Optimization
- File size limits prevent oversized analysis
- Caching of unchanged files (configurable)
- Rate limiting to prevent API throttling
- Incremental analysis (only changed files)

### Resource Requirements

| Component | CPU | Memory | Storage |
|-----------|-----|--------|---------|
| Jenkins Master | 2+ cores | 4GB+ | 50GB+ |
| Jenkins Agent | 1+ core | 2GB+ | 20GB+ |
| Python Scripts | Minimal | <500MB | <1GB |

## Integration Points

```
┌─────────────────────────────────────────────────────────┐
│                  External Integrations                   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Source Control                                         │
│  ├── Git (GitHub, GitLab, Bitbucket)                   │
│  └── Webhook triggers                                   │
│                                                          │
│  AI Platform                                            │
│  ├── IBM watsonx.ai                                     │
│  └── REST API                                           │
│                                                          │
│  Notification Systems (Optional)                        │
│  ├── Email (SMTP)                                       │
│  ├── Slack                                              │
│  └── Microsoft Teams                                    │
│                                                          │
│  Issue Tracking (Future)                                │
│  ├── Jira                                               │
│  └── GitHub Issues                                      │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Production Setup                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │           Jenkins Master                       │    │
│  │  - Pipeline orchestration                      │    │
│  │  - Credential management                       │    │
│  │  - Report publishing                           │    │
│  └────────────────┬───────────────────────────────┘    │
│                   │                                      │
│         ┌─────────┴─────────┬─────────────┐            │
│         │                   │             │             │
│         ▼                   ▼             ▼             │
│  ┌──────────┐        ┌──────────┐  ┌──────────┐       │
│  │  Agent 1 │        │  Agent 2 │  │  Agent N │       │
│  │  Python  │        │  Python  │  │  Python  │       │
│  │  Scripts │        │  Scripts │  │  Scripts │       │
│  └────┬─────┘        └────┬─────┘  └────┬─────┘       │
│       │                   │             │              │
│       └───────────────────┼─────────────┘              │
│                           │                             │
│                           ▼                             │
│                  ┌─────────────────┐                   │
│                  │  watsonx.ai API │                   │
│                  │  (IBM Cloud)    │                   │
│                  └─────────────────┘                   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

**Architecture Version**: 1.0  
**Last Updated**: 2026-01-22