use crate::configuration::Settings;
use chrono::Utc;
use sqlx::postgres::PgPoolOptions;
use tracing;

#[tracing::instrument(name = "Running cleanup job", skip(settings))]
pub async fn cleanup_old_articles(settings: Settings) -> Result<(), anyhow::Error> {
    let db = PgPoolOptions::new().connect_lazy_with(settings.database.connect_options());
    let today = Utc::now().date_naive();

    tracing::info!("Starting cleanup of articles older than {}", today);

    let result = sqlx::query!("DELETE FROM articles WHERE date::date < $1", today)
        .execute(&db)
        .await?;

    tracing::info!(
        "Cleanup completed. Deleted {} articles older than {}",
        result.rows_affected(),
        today
    );

    Ok(())
}
