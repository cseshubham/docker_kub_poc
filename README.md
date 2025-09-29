# docker_kub_poc
---

# **POC: Spring Boot Microservices with Docker & Kubernetes**

## **Overview**

This POC demonstrates a simple **microservices architecture** with three Spring Boot services:

| Service        | Port | Description                                    |
| -------------- | ---- | ---------------------------------------------- |
| `product-ms`   | 8080 | Product Service                                |
| `order-ms`     | 8081 | Order Service (depends on Product & Inventory) |
| `inventory-ms` | 8082 | Inventory Service                              |

**Tech Stack**:

* Java 17, Spring Boot 3.5.6
* H2 Database (in-memory)
* Docker & Docker Compose
* Kubernetes (via Docker Desktop or any K8s cluster)

All images are **hosted on Docker Hub** under the `cseshubham` account.

---

## **Prerequisites**

* Docker (v20+)
* Docker Compose (v2+)
* Kubernetes cluster (Docker Desktop K8s or Minikube)
* `kubectl` CLI

---

## **1 Running with Docker Compose**

1. **Create a `docker-compose.yml`** with the following content:

```yaml
version: '3.8'

services:
  product-ms:
    image: cseshubham/product-ms:latest
    container_name: product-ms
    ports:
      - "8080:8080"
    networks:
      - microservice-net

  order-ms:
    image: cseshubham/order-ms:latest
    container_name: order-ms
    ports:
      - "8081:8081"
    networks:
      - microservice-net
    environment:
      INVENTORY_SERVICE_URL: http://inventory-ms:8082/inventory
      PRODUCT_SERVICE_URL: http://product-ms:8080/product

  inventory-ms:
    image: cseshubham/inventory-ms:latest
    container_name: inventory-ms
    ports:
      - "8082:8082"
    networks:
      - microservice-net

networks:
  microservice-net:
    driver: bridge
```

2. **Run services in detached mode**:

```bash
docker compose up -d
```

3. **Check running containers**:

```bash
docker ps
```

4. **View logs of a service**:

```bash
docker compose logs -f order-ms
```

5. **Stop and remove all containers**:

```bash
docker compose down
```

---

## **2 Running with Docker CLI (Optional)**

```bash
docker run -d -p 8080:8080 --name product-ms cseshubham/product-ms:latest
docker run -d -p 8082:8082 --name inventory-ms cseshubham/inventory-ms:latest
docker run -d -p 8081:8081 --name order-ms \
  -e INVENTORY_SERVICE_URL=http://inventory-ms:8082/inventory \
  -e PRODUCT_SERVICE_URL=http://product-ms:8080/product \
  cseshubham/order-ms:latest
```

* Stop containers:

```bash
docker stop product-ms order-ms inventory-ms
docker rm product-ms order-ms inventory-ms
```

---

## **3 Running on Kubernetes**

1. **Create Deployment and Service YAML files** for each microservice (example for `product-ms`):

```yaml
# product-ms-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-ms
spec:
  replicas: 1
  selector:
    matchLabels:
      app: product-ms
  template:
    metadata:
      labels:
        app: product-ms
    spec:
      containers:
        - name: product-ms
          image: cseshubham/product-ms:latest
          ports:
            - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: product-ms
spec:
  type: NodePort
  selector:
    app: product-ms
  ports:
    - port: 8080
      targetPort: 8080
      nodePort: 30080
```

2. **Deploy all microservices**:

```bash
kubectl apply -f product-ms-deployment.yaml
kubectl apply -f product-ms-service.yaml
kubectl apply -f order-ms-deployment.yaml
kubectl apply -f order-ms-service.yaml
kubectl apply -f inventory-ms-deployment.yaml
kubectl apply -f inventory-ms-service.yaml
```

3. **Check pods and services**:

```bash
kubectl get pods
kubectl get service
```

4. **Delete deployments/services if needed**:

```bash
kubectl delete -f product-ms-deployment.yaml
kubectl delete -f product-ms-service.yaml
```

---

## **4 Docker Hub Images**

| Service      | Docker Hub Image                 |
| ------------ | -------------------------------- |
| product-ms   | `cseshubham/product-ms:latest`   |
| order-ms     | `cseshubham/order-ms:latest`     |
| inventory-ms | `cseshubham/inventory-ms:latest` |

> Since images are on Docker Hub, **no Dockerfiles are required locally**.

---

## **5 Notes**

* Ports mapped for local testing:

  * `product-ms` → 8080
  * `order-ms` → 8081
  * `inventory-ms` → 8082

* `order-ms` depends on `product-ms` and `inventory-ms`. Ensure all services are running.

* For Kubernetes, `NodePort` allows access via `localhost:<nodePort>` if using Docker Desktop.

## **6 Complete Code You can download from git repo**

---
