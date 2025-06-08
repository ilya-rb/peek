FROM lukemathwalker/cargo-chef:latest-rust-1.80.1 as chef
WORKDIR /app
RUN apt update && apt install lld clang -y

# Compute a lock file
FROM chef as planner
COPY ./server .
RUN cargo chef prepare --recipe-path recipe.json

# Build dependencies
FROM chef as builder
COPY --from=planner /app/recipe.json recipe.json
RUN cargo chef cook --release --recipe-path recipe.json

# Build project
COPY ./server .
ENV SQLX_OFFLINE true
RUN cargo build --release --bin [peek-server]

# Final image
FROM debian:bookworm-slim AS runtime
WORKDIR /app
COPY --from=builder /app/target/release/peek-server peek-server
COPY ./server/configuration configuration

EXPOSE 8080

ENV APP_ENVIRONMENT prod
ENTRYPOINT ["./peek-server"]