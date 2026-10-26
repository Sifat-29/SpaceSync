# SpaceSync

**SpaceSync** is a coworking-space booking application built with Spring Boot and MySQL. It supports user and vendor registration, admin approval of vendors and spaces, and space bookings.

This repository also includes a **local Kubernetes deployment using kind**, with Docker images for the Spring Boot application and MySQL.

## Tech stack

- **Backend:** Java 21, Spring Boot 4.0.3, Spring MVC, Spring Data JPA / Hibernate
- **Views:** Thymeleaf
- **Database:** MySQL 8.4
- **Build:** Maven
- **Containers and orchestration:** Docker, Kubernetes (kind), kubectl

## Features

- User and vendor registration and login
- Admin review and approval of vendor accounts
- Coworking-space listings and approval
- Space booking workflow
- MySQL-backed data persistence

## Local Kubernetes architecture

```text
Browser (http://localhost:8081)
    |
    | kubectl port-forward
    v
SpaceSync Service (ClusterIP, port 8081)
    |
    v
SpaceSync Pod (Spring Boot, spacesync:1.0)
    |
    | JDBC to mysql:3306
    v
MySQL Service (ClusterIP, port 3306)
    |
    v
MySQL Pod (mysql:8.4)
    |
    v
PersistentVolumeClaim (mysql-data, 1 GiB)
```

Database passwords are supplied through a Kubernetes Secret named `mysql-secret` rather than committed to this repository.

## Run with Docker and kind (Windows PowerShell)

### Prerequisites

- Docker Desktop running with Linux containers
- [kind](https://kind.sigs.k8s.io/docs/user/quick-start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- Git

Run the following commands from **PowerShell**. Docker Desktop must be running.

### 1. Clone the repository

```powershell
git clone https://github.com/Sifat-29/SpaceSync.git
cd SpaceSync
```

### 2. Create a local Kubernetes cluster

```powershell
kind create cluster --name spacesync --wait 120s
kubectl get nodes --context kind-spacesync
```

The cluster should show a node with status `Ready`. If the named cluster already exists, reuse it instead of creating it again.

### 3. Build the application image and load it into kind

```powershell
docker build -t spacesync:1.0 .
kind load docker-image spacesync:1.0 --name spacesync
```

The multi-stage `Dockerfile` builds the Spring Boot JAR using Maven and then runs it with a Java 21 runtime image. Loading the image makes it available to the kind node without pushing it to a container registry.

### 4. Create database credentials locally

```powershell
$rootPwd = [guid]::NewGuid().ToString("N")
$appPwd = [guid]::NewGuid().ToString("N")

kubectl create secret generic mysql-secret `
  --from-literal=MYSQL_ROOT_PASSWORD="$rootPwd" `
  --from-literal=MYSQL_PASSWORD="$appPwd"
```

Create this Secret **only once per cluster**. Do not commit actual credentials, exported Secrets, or database backups to Git. Kubernetes Secrets are not a substitute for full production secret management.

### 5. Deploy MySQL

```powershell
kubectl apply -f mysql.yaml
kubectl wait --for=condition=ready pod -l app=mysql --timeout=240s
kubectl get pvc
```

`mysql.yaml` configures one MySQL Pod, an internal Service named `mysql`, and a 1 GiB PersistentVolumeClaim. The PVC should show `Bound`.

### 6. Deploy SpaceSync

```powershell
kubectl apply -f app.yaml
kubectl rollout status deployment/spacesync --timeout=180s
kubectl get pods
```

The application uses the internal JDBC hostname `mysql:3306` and reads the database password from `mysql-secret`. The manifests use `imagePullPolicy: IfNotPresent` for the locally loaded application image.

### 7. Open the application

```powershell
kubectl port-forward service/spacesync 8081:8081
```

Keep this terminal open and visit **http://localhost:8081/login**. The home URL (`/`) may redirect to `/login` when not authenticated; an HTTP `302` redirect is normal.

### 8. Verify the deployment

In a separate PowerShell terminal:

```powershell
kubectl get deployments,pods,services,pvc
kubectl logs deployment/spacesync --tail=50
kubectl logs deployment/mysql --tail=50
```

To check the tables directly, open a shell in the MySQL container:

```powershell
kubectl exec -it deployment/mysql -- sh
```

Then, inside the container:

```sh
MYSQL_PWD="$MYSQL_PASSWORD" mysql -u "$MYSQL_USER" "$MYSQL_DATABASE"
```

At the `mysql>` prompt:

```sql
SHOW TABLES;
```

Use `exit` to leave MySQL, then `exit` again to leave the container.

## First-time admin setup (local development only)

A new Kubernetes MySQL database has **no pre-created admin account**. To test vendor approval locally, first provision an admin in the `system_admin` table.

1. Open the MySQL client using the commands in step 8.
2. Execute the following SQL **after replacing the sample password with a unique, local-only password**:

```sql
INSERT INTO system_admin (username, role_level, password)
VALUES ('localadmin', 'ADMIN', SHA2('REPLACE_WITH_A_UNIQUE_LOCAL_PASSWORD', 256));
```

3. Log in at **http://localhost:8081/login** using `localadmin` and the password you chose.
4. Go to **http://localhost:8081/admin/verify** to review pending vendor applications and coworking spaces.
5. Approve a vendor, log in with that vendor account, and test the booking flow with a user account.

**Security note:** The current application uses unsalted SHA-256 for password comparison. The `SHA2(..., 256)` statement matches that existing behavior; it is **not a recommended password-storage design**. Use an adaptive password hash such as Argon2id or bcrypt, enforce admin authorization on sensitive routes, and replace manual admin provisioning before a production deployment. Never publish actual admin credentials.

## Kubernetes configuration files

| File | Purpose |
| --- | --- |
| `Dockerfile` | Builds and packages the Spring Boot application in a multi-stage Docker build. |
| `.dockerignore` | Excludes unnecessary local files from the Docker build context. |
| `mysql.yaml` | Defines the MySQL Deployment, ClusterIP Service, and persistent storage claim. |
| `app.yaml` | Defines the SpaceSync Deployment, its environment variables, and ClusterIP Service. |

## Important limitations

- This deployment is designed for **local development and demonstration**, not production hosting.
- The Kubernetes app manifest temporarily uses `SPRING_JPA_HIBERNATE_DDL_AUTO=update` to create tables in a fresh database. Production should use versioned schema migrations.
- The Docker image build uses `mvn -DskipTests package`; tests are not executed during that build.
- The MySQL PVC keeps data across Pod replacements, but **deleting the kind cluster can remove the local data**. It is not a backup.
- `kubectl port-forward` exposes the app only while the command is running; no Ingress or public load balancer is configured.

## Troubleshooting

```powershell
kubectl get pods
kubectl describe pod -l app=spacesync
kubectl logs deployment/spacesync --tail=100
kubectl describe pod -l app=mysql
kubectl logs deployment/mysql --tail=100
```

If a new application image is built using the same `spacesync:1.0` tag, load it into kind again and restart the app Deployment to replace the running Pod:

```powershell
kind load docker-image spacesync:1.0 --name spacesync
kubectl rollout restart deployment/spacesync
kubectl rollout status deployment/spacesync
```

## Clean up the local cluster

```powershell
kind delete cluster --name spacesync
```

**Warning:** Deleting the cluster removes this local environment and may delete its database data.
