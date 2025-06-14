use peek_server::app::App;
use peek_server::jobs::cleanup_job::cleanup_old_articles;
use peek_server::telemetry::LogLevel;
use peek_server::{configuration, telemetry};
use std::fmt::{Debug, Display};
use tokio::task::JoinError;

#[tokio::main]
async fn main() -> Result<(), std::io::Error> {
    let settings = configuration::read_configuration().expect("Failed to read app settings");

    telemetry::init_tracing(String::from("peek-server"), LogLevel::Info, std::io::stdout);

    let worker_settings = settings.clone();
    tokio::spawn(async { cleanup_old_articles(worker_settings).await });

    let app = App::build(settings).await?;
    let app_worker = tokio::spawn(app.run_until_stopped());

    tokio::select! {
        outcome = app_worker => report_exit("APP", outcome),
    }

    Ok(())
}

fn report_exit(task_name: &str, outcome: Result<Result<(), impl Debug + Display>, JoinError>) {
    match outcome {
        Ok(Ok(())) => tracing::info!("{} exited", task_name),
        Ok(Err(e)) => {
            tracing::error!(
                error.cause_chain = ?e,
                error.message = %e,
                "{} failed",
                task_name,
            )
        }
        Err(e) => {
            tracing::error!(
                error.cause_chain = ?e,
                error.message = %e,
                "{} failed to complete",
                task_name,
            )
        }
    }
}
