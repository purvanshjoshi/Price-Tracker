# Changelog

All notable changes to **Price Scout** are documented here.


### Bug Fixes

- Generate missing chrome extension icon png files for manifest loading by @purvanshjoshi

- Use absolute paths in native messaging host config for better reliability by @purvanshjoshi

- Relocate host config to pricetracker root for space-less registry path by @purvanshjoshi

- Optimize workflow triggers and permissions for better visibility by @purvanshjoshi

- Skip PR-only jobs on direct pushes to prevent failures by @purvanshjoshi

- Amazon scraper - resolve 503 bot detection and broken selectors (#61) by @purvanshjoshi

- Update labeler config to v6 format by @purvanshjoshi

- Resolve workflow failures in labeler and security scan by @purvanshjoshi

- Correct SelectorConfigTest field from 'container' to 'title' by @purvanshjoshi

- Remaining log -> LOG reference in MigrationRunner.java by @purvanshjoshi

- Checkstyle violations (constant naming, star imports, whitespace) by @purvanshjoshi

- Use create-pull-request instead of direct push to master by @purvanshjoshi

- Use PR-based approach for changelog updates by @purvanshjoshi

- Use gh CLI for changelog PR with PAT fallback by @purvanshjoshi

- Use gh CLI for changelog PR creation with PAT fallback by @purvanshjoshi

- Set git identity before committing changelog by @purvanshjoshi

- Set git identity before committing changelog by @purvanshjoshi

- Remove [skip ci] from changelog commit to trigger PR checks by @purvanshjoshi

- Trigger CI on master for changelog workflow by @purvanshjoshi

- Unblock CI pipeline after skip-ci commit by @purvanshjoshi

- Check only open PRs for changelog by @purvanshjoshi

- Check only open PRs for changelog updates by @purvanshjoshi


### Build System

- Initialize Java Maven project and package structure by @purvanshjoshi

- Bump kentaro-m/auto-assign-action from 2.0.0 to 2.0.2 by @dependabot[bot]

- Bump org.junit.jupiter:junit-jupiter-api in /backend by @dependabot[bot]

- Bump actions/checkout from 4 to 6 by @dependabot[bot]

- Bump codelytv/pr-size-labeler from 1.10.0 to 1.10.3 by @dependabot[bot]

- Bump org.xerial:sqlite-jdbc in /backend by @dependabot[bot]

- Bump org.jsoup:jsoup from 1.17.2 to 1.22.1 in /backend by @dependabot[bot]

- Bump org.json:json from 20240303 to 20251224 in /backend by @dependabot[bot]

- Bump actions/stale from 9 to 10 by @dependabot[bot]

- Bump github/codeql-action from 3 to 4 by @dependabot[bot]

- Bump org.apache.maven.plugins:maven-assembly-plugin by @dependabot[bot]

- Bump actions/first-interaction from 1 to 3 by @dependabot[bot]

- Bump org.slf4j:slf4j-simple in /backend by @dependabot[bot]

- Bump org.xerial:sqlite-jdbc in /backend by @dependabot[bot]

- Bump actions/labeler from 5 to 6 by @dependabot[bot]

- Bump com.google.code.gson:gson in /backend by @dependabot[bot]

- Bump org.json:json from 20231013 to 20251224 in /backend by @dependabot[bot]

- Bump org.jsoup:jsoup from 1.17.2 to 1.22.2 in /backend by @dependabot[bot]

- Bump amannn/action-semantic-pull-request by @dependabot[bot]

- Bump codelytv/pr-size-labeler from 1.10.3 to 1.10.4 by @dependabot[bot]

- Bump actions/setup-java from 4 to 5 by @dependabot[bot]

- Bump org.seleniumhq.selenium:selenium-java in /backend by @dependabot[bot]

- Bump org.junit.jupiter:junit-jupiter-api in /backend by @dependabot[bot]

- Bump io.github.bonigarcia:webdrivermanager in /backend by @dependabot[bot]

- Bump softprops/action-gh-release from 2 to 3 (#55) by @dependabot[bot]

- Bump actions/dependency-review-action from 4 to 5 (#56) by @dependabot[bot]

- Bump org.junit.jupiter:junit-jupiter-api in /backend (#58) by @dependabot[bot]

- Bump org.json:json from 20251224 to 20260522 in /backend (#59) by @dependabot[bot]

- Bump org.xerial:sqlite-jdbc in /backend (#62) by @dependabot[bot]

- Bump org.seleniumhq.selenium:selenium-java in /backend by @dependabot[bot]

- Bump actions/checkout from 6 to 7 by @dependabot[bot]

- Bump org.json:json from 20260522 to 20260719 in /backend by @dependabot[bot]

- Bump org.json:json from 20260522 to 20260719 in /backend by @purvanshjoshi

- Bump org.junit.jupiter:junit-jupiter-api in /backend by @dependabot[bot]

- Bump org.junit.jupiter:junit-jupiter-api from 6.1.0 to 6.1.2 in /backend by @purvanshjoshi

- Bump org.seleniumhq.selenium:selenium-java in /backend by @dependabot[bot]

- Bump org.seleniumhq.selenium:selenium-java from 4.45.0 to 4.46.0 in /backend by @purvanshjoshi

- Add detailed CODEOWNERS file by @purvanshjoshi

- Add detailed CODEOWNERS file by @purvanshjoshi

- Add .dockerignore for backend by @purvanshjoshi

- Add .dockerignore for backend by @purvanshjoshi

- Add Makefile with common dev commands by @purvanshjoshi

- Add Makefile with common dev commands by @purvanshjoshi

- Add docker-compose for local development by @purvanshjoshi

- Add docker-compose for local development by @purvanshjoshi

- Add pre-commit hook configuration by @purvanshjoshi

- Add pre-commit hook configuration by @purvanshjoshi

- Add ESLint config for Chrome extension by @purvanshjoshi

- Add ESLint config for Chrome extension by @purvanshjoshi

- Bump org.apache.maven.plugins:maven-surefire-plugin by @dependabot[bot]

- Bump maven-surefire-plugin from 3.5.3 to 3.5.6 in /backend by @purvanshjoshi

- Bump ch.qos.logback:logback-classic in /backend by @dependabot[bot]

- Bump logback-classic from 1.5.18 to 1.5.38 in /backend by @purvanshjoshi

- Bump org.xerial:sqlite-jdbc in /backend by @dependabot[bot]

- Bump sqlite-jdbc from 3.53.2.0 to 3.53.2.1 in /backend by @purvanshjoshi


### Chores

- Remove temporary read_docx.ps1 script by @purvanshjoshi

- Standardize directory names to lowercase for cleaner structure by @purvanshjoshi

- Rename Extension directory to lowercase by @purvanshjoshi

- Implement professional GitHub workflows, templates, and community guidelines by @purvanshjoshi

- Implement advanced PR automation (linting, size labeling, security scanning, checkstyle) by @purvanshjoshi

- Consolidate PR checks into high-visibility Avengers Guard workflow by @purvanshjoshi

- Implement industry-grade infrastructure (CodeQL, Dependabot, Labeler, Release Automation) by @purvanshjoshi

- Resolve merge conflicts and merge master into 34 by @purvanshjoshi

- Repository cleanup and modernization rfc by @purvanshjoshi

- Comprehensive repository cleanup and backlog grooming by @purvanshjoshi

- Update changelog by @github-actions[bot]

- Update changelog by @purvanshjoshi

- Update changelog by @github-actions[bot]

- Update changelog by @purvanshjoshi


### Continuous Integration

- Add continue-on-error to greetings workflow by @purvanshjoshi


### Documentation

- Organize proposal documents into docs/project_proposal by @purvanshjoshi

- Update architecture to Chrome Extension Native Messaging (No API) by @purvanshjoshi

- Add local setup guide for native messaging and compilation by @purvanshjoshi

- Update paths for extension and host-config to lowercase by @purvanshjoshi

- Restructure project into backend and proposal folders for maximum cleanness by @purvanshjoshi

- Overhaul README.md with professional design and technical deep dives by @purvanshjoshi

- Fix logo URL branch name to match repository setup by @purvanshjoshi

- Overhaul README About and Overview sections for a premium professional look by @purvanshjoshi

- Finalize README overhaul with premium tech stack and team section by @purvanshjoshi

- Update README logo reference to official logo.png by @purvanshjoshi

- Update official contact email across community guidelines by @purvanshjoshi

- Update project status to Active and add QA section by @purvanshjoshi

- Add MIT license file by @purvanshjoshi

- Add MIT license file by @purvanshjoshi

- Add OpenAPI 3.0.3 specification for REST API by @purvanshjoshi

- Add OpenAPI 3.0.3 specification for REST API by @purvanshjoshi


### Features

- Add Product model and JDBC DatabaseManager by @purvanshjoshi

- Add Amazon and Flipkart Jsoup scrapers implementing Callable by @purvanshjoshi

- Add Multithreading EngineManager with Callable and Future by @purvanshjoshi

- Implement Chrome Native Messaging bridge with MessageParser and Main loop by @purvanshjoshi

- Complete Chrome Extension frontend UI and Native Messaging logic by @purvanshjoshi

- Complete Windows Native Messaging host configuration files by @purvanshjoshi

- Switch to SQLite for zero-config database setup by @purvanshjoshi

- Add sort, filter, and Best Deal UI by @D2758695161

- Add sort, filter, and Best Deal UI by @D2758695161

- Add sort, filter, and Best Deal UI by @D2758695161

- Externalize fragile selectors to selectors.json (#1) by @purvanshjoshi

- Stabilize selenium scrapers with stealth factory and diagnostics by @purvanshjoshi

- Add /api/health endpoint and Docker HEALTHCHECK by @purvanshjoshi

- Add /api/health endpoint and Docker HEALTHCHECK by @purvanshjoshi

- Add centralized error handling to API server by @purvanshjoshi

- Add centralized error handling to API server by @purvanshjoshi

- Replace System.out with structured logging (SLF4J + Logback) by @purvanshjoshi

- Replace System.out with structured logging (SLF4J + Logback) by @purvanshjoshi

- Add database migration framework by @purvanshjoshi

- Add database migration framework by @purvanshjoshi

- Implement intelligence service with price prediction API by @purvanshjoshi

- Implement intelligence service with price prediction API by @purvanshjoshi

- Add auto-changelog generation with git-cliff by @purvanshjoshi

- Auto-generate CHANGELOG.md after every PR merge by @purvanshjoshi


### Tests

- Add unit tests for Product and SelectorConfig by @purvanshjoshi

- Add unit tests for Product and SelectorConfig by @purvanshjoshi

