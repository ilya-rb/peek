use anyhow::{Result, bail};
use serde::Serialize;

pub const KEY_IRISH_TIMES: &str = "irishtimes";
pub const KEY_DOU: &str = "dou";
pub const KEY_HACKER_NEWS: &str = "hackernews";

pub const IRISH_TIMES: NewsSource = NewsSource {
    key: KEY_IRISH_TIMES,
    kind: NewsSourceKind::IrishTimes,
};

pub const DOU: NewsSource = NewsSource {
    key: KEY_DOU,
    kind: NewsSourceKind::Dou,
};

pub const HACKER_NEWS: NewsSource = NewsSource {
    key: KEY_HACKER_NEWS,
    kind: NewsSourceKind::HackerNews,
};

#[derive(Clone, Copy, Debug, PartialEq, Serialize)]
pub struct NewsSource {
    pub key: &'static str,
    pub kind: NewsSourceKind,
}

#[derive(Clone, Copy, Debug, PartialEq, Serialize)]
pub enum NewsSourceKind {
    IrishTimes,
    Dou,
    HackerNews,
}

impl TryFrom<&str> for NewsSource {
    type Error = anyhow::Error;

    fn try_from(source_key: &str) -> Result<Self, Self::Error> {
        match source_key {
            KEY_IRISH_TIMES => Ok(IRISH_TIMES),
            KEY_DOU => Ok(DOU),
            KEY_HACKER_NEWS => Ok(HACKER_NEWS),
            _ => bail!("Unsupported news source: {}", source_key),
        }
    }
}
