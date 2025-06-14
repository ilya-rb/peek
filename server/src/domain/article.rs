use anyhow::{Result, bail};
use chrono::{DateTime, Utc};
use serde::Serialize;
use url::Url;

use crate::domain::news_source::NewsSource;
use crate::domain::tag::Tags;

#[derive(Debug, Serialize, PartialEq, Clone)]
pub struct Article {
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
        if title.trim().is_empty() {
            bail!("Title is empty");
        }

        Ok(Article {
            title,
            link,
            source,
            tags,
            date,
        })
    }
}
