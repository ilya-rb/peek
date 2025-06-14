use crate::domain::article::Article;
use crate::domain::news_source::NewsSource;
use async_trait::async_trait;
use chrono::{DateTime, Utc};

#[async_trait]
pub trait CacheFetcher {
    async fn get_articles(
        &self,
        source: NewsSource,
        date: DateTime<Utc>,
    ) -> anyhow::Result<Vec<Article>>;

    async fn save_articles(&self, articles: &[Article]) -> anyhow::Result<()>;
}

pub struct NoCacheFetcher;

#[async_trait]
impl CacheFetcher for NoCacheFetcher {
    async fn get_articles(
        &self,
        _source: NewsSource,
        _date: DateTime<Utc>,
    ) -> anyhow::Result<Vec<Article>> {
        Ok(vec![])
    }

    async fn save_articles(&self, _articles: &[Article]) -> anyhow::Result<()> {
        Ok(())
    }
}
