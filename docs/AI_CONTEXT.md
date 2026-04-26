# Project Context

## Project

AI-Powered Software Project Evaluation and Documentation Compliance Analysis System

## Architecture

Modular Monolith (microservice-ready)

## Tech Stack

- Spring Boot - 4.0.6
- React
- PostgreSQL
- OpenAI API or Ollama

## Rules

- No Lombok
- Constructor injection
- Modular boundaries enforced
- Modules cannot directly access another module’s repository
- Service-to-service module communication only
- Use environment variables
- Use ApiResponse wrapper
- Use GlobalExceptionHandler

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
