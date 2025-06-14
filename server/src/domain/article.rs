use anyhow::{Result, bail};
use chrono::{DateTime, Utc};
use serde::Serialize;
use url::Url;
use uuid::Uuid;

use crate::domain::news_source::NewsSource;
use crate::domain::tag::Tags;

#[derive(Debug, Serialize, PartialEq, Clone)]
pub struct Article {
    pub id: Uuid,
    pub title: String,
    pub link: Url,
    pub source: NewsSource,
    pub tags: Tags,
    pub date: DateTime<Utc>,
}

impl Article {
    pub fn new(
        title: String,
        date: DateTime<Utc>,
        link: Url,
        source: NewsSource,
        tags: Tags,
    ) -> Result<Article> {
        #[cfg(test)]
        let id = Uuid::nil();
        #[cfg(not(test))]
        let id = Uuid::new_v4();

        if title.trim().is_empty() {
            bail!("Title is empty");
        }

        Ok(Article {
            id,
            title,
            link,
            source,
            tags,
            date,
        })
    }
}
