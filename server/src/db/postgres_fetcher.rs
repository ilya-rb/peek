use async_trait::async_trait;
use chrono::{DateTime, Utc};
use sqlx::PgPool;
use url::Url;

use crate::domain::article::Article;
use crate::domain::news_source::NewsSource;
use crate::domain::tag::{Tag, Tags};
use crate::services::cache_fetcher::CacheFetcher;

pub struct PostgresFetcher {
    db_pool: PgPool,
}

impl PostgresFetcher {
    pub fn new(db_pool: PgPool) -> Self {
        Self { db_pool }
    }
}

#[async_trait]
impl CacheFetcher for PostgresFetcher {
    async fn get_articles(
        &self,
        source: NewsSource,
        date: DateTime<Utc>,
    ) -> anyhow::Result<Vec<Article>> {
        let key = source.key;
        let date_only = date.date_naive();

        let records = sqlx::query!(
            r#"
            SELECT id, link, title, date, tags
            FROM articles
            WHERE source = $1 AND date::date = $2"#,
            key,
            date_only,
        )
        .fetch_all(&self.db_pool)
        .await
        .map_err(|e| {
            tracing::error!(
                "Failed to read articles from DB by source and date: {:?}",
                e
            );
            e
        })?;

        let articles = records
            .into_iter()
            .map(|row| Article {
                id: row.id,
                link: Url::parse(row.link.as_str()).unwrap(),
                date: row.date,
                title: row.title,
                tags: Tags(
                    row.tags
                        .iter()
                        .map(|t| Tag::new(t.clone()).unwrap())
                        .collect(),
                ),
                source,
            })
            .collect();

        Ok(articles)
    }

    async fn save_articles(&self, articles: &[Article]) -> anyhow::Result<()> {
        let mut transaction = self.db_pool.begin().await?;

        for article in articles {
            let tags: Vec<String> = article.tags.0.iter().map(|t| t.0.clone()).collect();
            let tags: &Vec<String> = tags.as_ref();

            sqlx::query!(
                r#"
                INSERT INTO articles (id, source, title, date, link, tags, created_at)
                VALUES ($1, $2, $3, $4, $5, $6, $7)
                "#,
                article.id,
                article.source.key,
                article.title,
                article.date,
                Into::<String>::into(article.link.clone()),
                tags,
                Utc::now(),
            )
            .execute(&mut *transaction)
            .await?;
        }

        transaction.commit().await.map_err(|e| {
            tracing::error!("Failed to write articles {:?}", e);
            e
        })?;

        Ok(())
    }
}
