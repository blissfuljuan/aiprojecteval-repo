# Project Context

## Project

AI-Powered Software Project Evaluation and Documentation Compliance Analysis System

## Architecture

Modular Monolith (microservice-ready)

## Tech Stack

Spring Boot
React
PostgreSQL
OpenAI API
Ollama

## Rules

- No Lombok
- Constructor injection
- Modular boundaries enforced
- Service-to-service module communication only
- ApiResponse wrapper
- Global exception handling
- JWT authentication

## Module Order

1. common
2. identity
3. project
4. submission
5. document
6. ai
7. evaluation
8. repositoryanalysis
9. deploymentvalidation
10. report
