FROM lukemathwalker/cargo-chef:latest as chef
WORKDIR /app
RUN apt update && apt install lld clang -y

# Compute a lock file
FROM chef as planner
COPY . .
RUN cargo chef prepare --recipe-path recipe.json

# Build dependencies
FROM chef as builder
COPY --from=planner /app/recipe.json recipe.json
RUN cargo chef cook --release --recipe-path recipe.json

# Build project
COPY . .
ENV SQLX_OFFLINE true
RUN cargo build --release --bin peek-server

# Final image
FROM debian:bookworm-slim AS runtime
WORKDIR /app
RUN apt-get update -y \
    && apt-get install -y --no-install-recommends openssl ca-certificates \
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/release/peek-server peek-server
COPY configuration configuration
COPY static static
COPY scripts/init_db.sh init_db.sh

EXPOSE 8080

ENV APP_ENVIRONMENT prod
ENTRYPOINT ["./peek-server"]