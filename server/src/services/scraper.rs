use crate::domain::article::Article;
use async_trait::async_trait;
use reqwest::Client;

#[async_trait]
pub trait Scraper {
    async fn scrape_articles(&self, http_client: &Client) -> anyhow::Result<Vec<Article>>;
}
