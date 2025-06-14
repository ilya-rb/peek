mod scraper;

use crate::configuration::Settings;
use crate::db::article_repository::get_articles_for_today;
use crate::db::postgres_fetcher::PostgresFetcher;
use crate::domain::article::Article;
use crate::domain::news_source::NewsSource;
use crate::services::irish_times::scraper::IrishTimesScraper;
use anyhow::Result;
use reqwest::Client;
use sqlx::PgPool;

pub async fn get_news(
    news_source: NewsSource,
    _http_client: &Client,
    db: &PgPool,
    settings: &Settings,
) -> Result<Vec<Article>> {
    let scraper = IrishTimesScraper::new(news_source, settings.services.irish_times.url.clone());
    let db_fetcher = PostgresFetcher::new(db.clone());
    get_articles_for_today(news_source, &scraper, &db_fetcher).await
}
