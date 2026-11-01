# File Processing Server — Master Project Context

## 1. Project Purpose

Build a backend application using Java and Spring Boot that processes uploaded files and gradually evolves into a reliable, asynchronous processing system.

This is a learning project, not merely an API implementation exercise.

The goal is to understand backend engineering concepts through implementation, testing, and solving real problems.

The project starts with image processing. The architecture and lessons should prepare it for more demanding workloads, including video processing.

## 2. Learning Philosophy

Follow this cycle:

1. Build the simplest working solution.
2. Test it and understand its behavior.
3. Identify a real limitation.
4. Understand why that limitation exists.
5. Introduce the smallest suitable improvement.
6. Test the improved design.
7. Save a Git checkpoint.

Do not introduce technologies merely because they are popular or used by large companies.

Introduce them when the current design gives us a reason to use them.

## 3. Teaching Preferences

When helping me implement the project:

* Explain concepts in beginner-friendly language.
* Avoid unnecessary jargon.
* Use small examples and diagrams when useful.
* Explain why a change is needed before implementing it.
* Prefer step-by-step implementation over large code dumps.
* Help me understand the code rather than simply copying it.
* Give hints before complete solutions when appropriate.
* Ask one question at a time during conceptual assessments.
* Avoid repeating explanations already covered.
* Keep routine responses concise.
* Ask for code or terminal output only when it is needed to diagnose a problem.
* Give exact commands and specify the directory when relevant.
* Do not assume my implementation matches the planned design; verify when necessary.
* Preserve existing working code unless a change is required.

## 4. Technology Direction

Current project direction:

* Java
* Spring Boot
* Gradle
* Java 21
* Git for version control
* GitHub for remote repository hosting

Planned technologies, introduced only when appropriate:

* Java concurrency utilities and thread pools
* A relational database using Spring Data JPA
* RabbitMQ
* WebSocket
* Redis
* Testing and performance tools
* Monitoring and observability tools
* Docker and deployment improvements
* Multi-instance and distributed processing concepts

Do not add every planned technology at the beginning.

## 5. Current Project Status

### V0.1 — Basic Image Upload

Status: Completed and committed.

Endpoint:
`POST /api/update-image`

Original flow:

Upload image → Save to disk → Return saved file

The application receives an image using Spring's `MultipartFile` and saves it to a local directory.

Git checkpoint:

* Commit: `e147f48`
* Commit message: `V0.1: Basic image upload`

### V0.2 — Synchronous Image Processing

Status: Completed and committed.

Current functionality:

1. Receive an image.
2. Create the upload directory if required.
3. Read the original image.
4. Create a 300 × 320 image canvas.
5. Draw the input image onto the canvas.
6. Add a white background and the text `neti-neti`.
7. Save the transformed image.
8. Return the processed image in the HTTP response.

The current processing uses Java image APIs, including `BufferedImage`, `ImageIO`, and `Graphics2D`.

The output is written as JPEG.

The important architectural property is that processing occurs synchronously within the HTTP request. The client waits for processing to finish.

Git checkpoint:

* A V0.2 commit and Git tag have been created by the project owner.
* Check the repository history if the exact commit hash is needed.

### Current Learning Position

The project owner understands the basic concepts of:

* HTTP requests and responses
* File uploads and disk storage
* Synchronous versus asynchronous processing
* Jobs and job IDs
* Job status
* In-memory queues
* Background workers
* Worker pools
* Polling
* Database persistence versus message queues
* The purpose of heartbeats
* Why duplicate job processing is undesirable

These concepts have been discussed conceptually. Their complete implementation should be verified during the relevant phases.

## 6. Core Architectural Principles

### Principle A — Separate requests from long-running work

The upload request should eventually create a job and return a job ID without waiting for processing to finish.

### Principle B — Separate job state from job execution

A job represents the work to be done. A worker executes that work. A queue helps deliver waiting work to workers.

### Principle C — Separate persistence from messaging

A database primarily stores durable application data and job state.

A message queue distributes work between producers and consumers.

They can work together, but they are not interchangeable.

### Principle D — Prefer simple solutions first

Start with an in-memory Java queue. Later, introduce a database and a message broker when their benefits become necessary.

### Principle E — Make failures part of the learning process

Explore what happens when:

* The application crashes.
* A worker gets stuck.
* Processing fails.
* Two workers attempt to process the same job.
* A job is submitted more than once.
* The queue becomes overloaded.
* A worker finishes but fails before recording completion.

Do not attempt to solve all these problems in the first version.

**## 7. Version Roadmap

The roadmap is provisional. Adjust the order or scope when implementation reveals a better learning path.

### V0.1 — Basic Upload [COMPLETED]

* Accept an image.
* Save it to disk.
* Return the saved file.

### V0.2 — Synchronous Processing [COMPLETED]

* Transform the uploaded image.
* Save the processed result.
* Return the result.
* Understand the limitation of long-running HTTP requests.

### V1.0 — Asynchronous Processing [COMPLTED]

* Introduce a Job model.
* Generate a unique job ID.
* Add job statuses: QUEUED, PROCESSING, COMPLETED, FAILED.
* Introduce a thread-safe in-memory queue.
* Introduce controlled background workers.
* Configure two workers initially.
* Return HTTP 202 Accepted and a job ID.
* Implement a job-status endpoint.
* Implement a result-download endpoint.
* Handle processing failures.
* Test multiple simultaneous jobs.
* Understand the limitations of in-memory state.

Do not add a database, RabbitMQ, Redis, or WebSocket in this phase.

### V2.0 — Persistent Job State [COMPLETED]

* Introduce a relational database.
* Learn JPA and repository operations.
* Persist job metadata and status.
* Store input and output file paths as metadata.
* Recover interrupted jobs after application restart.
* Understand the difference between persistent job records and an in-memory work queue.

### V3.0 — Concurrency and Atomic Job Claiming [NEXT]

* Reproduce the duplicate-processing problem.
* Understand race conditions.
* Learn transactions and database consistency.
* Implement safe job claiming.
* Explore appropriate locking or atomic-update techniques.

### V4.0 — Worker Pool and Capacity Management

* Improve worker-pool configuration.
* Control concurrency and queue capacity.
* Understand CPU-bound versus I/O-bound work.
* Test what happens when jobs arrive faster than workers can process them.
* Learn basic backpressure and resource limits.

### V5.0 — External Messaging with RabbitMQ

* Introduce a message broker.
* Learn producers, consumers, acknowledgements, and durable messaging.
* Connect job creation to message delivery.
* Understand the relationship between database records and queued messages.
* Explore delivery failures and duplicate messages.

### V6.0 — Retries and Failure Recovery

* Track processing attempts.
* Add controlled retries.
* Set retry limits.
* Distinguish temporary and permanent failures.
* Learn dead-letter handling and idempotency.

### V7.0 — Progress Updates

* Add progress information to jobs.
* Continue supporting HTTP polling.
* Introduce WebSocket updates if useful.
* Keep persisted job state as the source of truth.

### V8.0 — Scheduling and Cleanup

* Learn Spring scheduling and cron expressions.
* Clean up temporary and expired files.
* Handle old job records and retention policies.

### V9.0 — Notifications and Webhooks

* Notify clients when processing finishes.
* Explore server-to-server webhooks.
* Consider delivery failures and repeated notifications.

### V10.0 — Caching

* Identify a genuine caching need.
* Learn cache hits, misses, TTL, and invalidation.
* Introduce Redis if justified.

### V11.0 — Security

* Add authentication and authorization.
* Restrict access to users' jobs and files.
* Validate uploaded files and limit upload sizes.
* Protect against unsafe filenames and path traversal.
* Consider rate limiting and resource abuse.

### V12.0 — Monitoring and Observability

* Improve structured logging.
* Measure processing duration and failure rates.
* Track queue depth and worker utilization.
* Monitor application health and resource consumption.

### V13.0 — Performance Testing

* Load-test upload and status endpoints.
* Measure throughput and latency.
* Identify actual bottlenecks.
* Optimize based on measurements rather than guesses.

### V14.0 — Multi-Instance Scaling

* Run multiple API instances.
* Separate API and worker responsibilities where useful.
* Use shared file/object storage when necessary.
* Introduce load balancing and distributed processing concepts.

### V15.0 — Advanced Distributed-System Concepts [OPTIONAL]

* Heartbeats and leases
* Stale-job detection
* Idempotency
* At-least-once delivery
* Duplicate-message handling
* Distributed coordination
* Eventual consistency
* Crash recovery and fault tolerance**

This advanced phase is optional and should be scoped according to learning goals and available time.

## 8. What Is Intentionally Deferred?

Until a phase explicitly requires them, avoid introducing:

* RabbitMQ or Kafka
* A relational database before the persistence phase
* Redis
* WebSocket
* Distributed locks
* Kubernetes
* Microservices
* Premature cloud scaling
* Complex authentication
* Unnecessary abstractions

## 9. Git and Checkpoint Strategy

* Keep meaningful commits.
* Use Git tags for completed milestones.
* Do not create a new branch merely because a new version begins.
* Keep runtime-generated files out of Git.
* Do not commit uploaded test images or generated outputs.
* Never commit secrets or credentials.
* Update the relevant requirements document when scope or architectural decisions change.

## 10. Completion Criteria for Each Version

A version is not complete merely because the application starts.

Before declaring a version complete:

1. Implement its requirements.
2. Test its expected behavior.
3. Test relevant failure cases.
4. Explain the main concepts in your own words.
5. Update the requirements or context documentation if necessary.
6. Commit the working code.
7. Tag the completed milestone when appropriate.

## 11. Instructions for Future AI Sessions

When continuing this project:

1. Read this file first.
2. Read the requirements file for the active version.
3. Confirm the current implementation and Git state if necessary.
4. Identify the last completed task.
5. Continue from that point without repeating completed work.
6. Do not assume a planned feature is already implemented.
7. Explain the next task and its purpose before changing code.
8. Keep changes limited to the active version.
9. Update this document when a meaningful architectural decision changes.
10. If a requirement is unclear, ask before making a significant design decision.

The guiding principle remains:

**Build → Observe → Understand → Improve → Test → Save a checkpoint.**
