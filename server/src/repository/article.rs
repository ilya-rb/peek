use chrono::Utc;
use sqlx::PgPool;
use url::Url;

use crate::domain::{Article, NewsSource, Tag, Tags};

use anyhow::Result;

#[tracing::instrument(name = "Read articles from DB", skip(db, news_source))]
pub async fn get_by_source(db: &PgPool, news_source: NewsSource) -> Result<Vec<Article>> {
    let source: String = news_source.key.clone();
    let records = sqlx::query!(
        r#"
        SELECT id, link, title, date, tags
        FROM articles
        WHERE source = $1"#,
        source,
    )
    .fetch_all(db)
    .await
    .map_err(|e| {
        tracing::error!("Failed to read articles from DB: {:?}", e);
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
            source: news_source.clone(),
        })
        .collect();

    Ok(articles)
}

#[tracing::instrument(name = "Write scraped articles", skip(db, articles))]
pub async fn save(db: &PgPool, articles: Vec<Article>) -> Result<()> {
    let mut transaction = db.begin().await?;

    for article in articles {
        let tags: Vec<String> = article.tags.0.into_iter().map(|t| t.0).collect();
        let tags: &Vec<String> = tags.as_ref();

        sqlx::query!(
            r#"
            INSERT INTO articles (id, source, title, date, link, tags, created_at)
            VALUES ($1, $2, $3, $4, $5, $6, $7)
            "#,
            article.id,
            Into::<String>::into(article.source.key),
            article.title,
            article.date,
            Into::<String>::into(article.link),
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
