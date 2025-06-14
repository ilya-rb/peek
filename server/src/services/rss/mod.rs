use crate::domain::article::Article;
use crate::domain::news_source::NewsSource;
use crate::domain::tag::Tags;
use crate::services::scraper::Scraper;
use anyhow::Result;
use async_trait::async_trait;
use feed_rs::parser;
use reqwest::Client;
use url::Url;

pub struct RssScraper {
    base_url: Url,
    news_source: NewsSource,
}

impl RssScraper {
    pub fn new(base_url: Url, news_source: NewsSource) -> Self {
        Self {
            base_url,
            news_source,
        }
    }
}

#[async_trait]
impl Scraper for RssScraper {
    async fn scrape_articles(&self, http_client: &Client) -> Result<Vec<Article>> {
        self.scrape_latest_articles(http_client).await
    }
}

impl RssScraper {
    pub async fn scrape_latest_articles(&self, http_client: &Client) -> Result<Vec<Article>> {
        let response = http_client
            .get(self.base_url.clone())
            .send()
            .await?
            .error_for_status()?;

        let body = response.text().await?;
        let feed = parser::parse(body.as_bytes())?;

        let articles: Vec<Article> = feed
            .entries
            .into_iter()
            .filter_map(|entry| {
                let url = match Url::parse(entry.id.as_str()) {
                    Ok(url) => url,
                    Err(_) => {
                        tracing::error!("Failed to parse article url: {}", entry.id);
                        return None;
                    }
                };

                let result = Article::new(
                    entry.title.unwrap().content,
                    entry.published.expect("Invalid published date"),
                    url,
                    self.news_source,
                    Tags(vec![]),
                );

                result.ok()
            })
            .collect();

        Ok(articles)
    }
}
