use crate::configuration::Settings;
use crate::db::article_repository::get_articles_for_today;
use crate::domain::article::Article;
use crate::domain::news_source::NewsSource;
use crate::services::cache_fetcher::NoCacheFetcher;
use crate::services::rss::RssScraper;

pub async fn get_news(source: NewsSource, settings: &Settings) -> anyhow::Result<Vec<Article>> {
    let scraper = RssScraper::new(settings.services.dou.url.clone(), source);
    let cache_fetcher = NoCacheFetcher;
    get_articles_for_today(source, &scraper, &cache_fetcher).await
}
