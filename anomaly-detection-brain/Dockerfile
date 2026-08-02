# Stage 1: Build dependencies
FROM python:3.13-slim AS builder

WORKDIR /build
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
# Install wheels to a user directory to copy easily
RUN pip install --no-cache-dir --user -r requirements.txt

# Stage 2: Clean runtime environment
FROM python:3.13-slim

WORKDIR /app
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Copy only the installed Python packages from the builder stage
COPY --from=builder /root/.local /root/.local
COPY . .

# Update path so the container can find uvicorn
ENV PATH=/root/.local/bin:$PATH

EXPOSE 8000
CMD ["uvicorn", "brain:app", "--host", "0.0.0.0", "--port", "8000"]
