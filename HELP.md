# How to build thw Cart Service Docker image

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

- [Docker](https://www.docker.com/get-started)

## Getting Started

Follow these instructions to build and run the Docker image for this project.

### 1. Clone the Repository

First, clone the repository to your local machine:

```bash
git clone https://github.com/avorakh/nodejs-aws-cart-api.git
cd nodejs-aws-cart-api
```

### 2. Build the Docker Image

Build the Docker image using the provided `Dockerfile`:

```bash
docker build -t cart-svc .
```

### 3. Run the Docker Container

Run the Docker container using the image you just built:

```bash
docker run -p 4000:4000  --name cart-svc cart-svc
```

- `-p 4000:4000` maps port 4000 on your host to port 4000 in the container.
- `--name cart-svc` gives your container a name.

### 4. Verify the Container is Running

You can verify that the container is running by the request.

```bash
curl 'http://localhost:4000/ping'
```

### 5. Stopping the Container

To stop the running container, use the following command:

```bash
docker stop cart-svc
```