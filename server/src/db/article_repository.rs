use chrono::Utc;
use reqwest::Client;

use crate::domain::article::Article;
use crate::domain::news_source::NewsSource;
use crate::services::cache_fetcher::CacheFetcher;
use crate::services::scraper::Scraper;

pub async fn get_articles_for_today<T, U>(
    source: NewsSource,
    scraper: &T,
    db_fetcher: &U,
) -> anyhow::Result<Vec<Article>>
where
    T: Scraper,
    U: CacheFetcher,
{
    let today = Utc::now();
    let key = source.key;

    // Try to get articles from database first
    match db_fetcher.get_articles(source, today).await {
        Ok(cached_articles) if !cached_articles.is_empty() => {
            tracing::info!(
                "Found {} cached articles for {} from today",
                cached_articles.len(),
                key,
            );
            return Ok(cached_articles);
        }
        Ok(_) => {
            tracing::info!(
                "No cached articles found for {} from today, proceeding with scraping",
                key
            );
        }
        Err(e) => {
            tracing::warn!(
                "Failed to fetch cached articles for {}: {}. Proceeding with scraping",
                key,
                e
            );
        }
    }

    // If no cached articles or database failed, scrape new ones
    tracing::info!("Scraping latest articles for {}", key);

    let scraped_articles = scraper.scrape_articles(&Client::new()).await?;

    // Save scraped articles to database if any were found
    if !scraped_articles.is_empty() {
        match db_fetcher.save_articles(&scraped_articles).await {
            Ok(_) => {
                tracing::info!(
                    "Saved {} new articles to database for {}",
                    scraped_articles.len(),
                    key
                );
            }
            Err(e) => {
                tracing::warn!("Failed to save articles to database for {}: {}", key, e);
            }
        }
    }

    Ok(scraped_articles)
}
