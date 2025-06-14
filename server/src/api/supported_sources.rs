use crate::configuration::Settings;
use crate::domain::news_source::{DOU, HACKER_NEWS, IRISH_TIMES, NewsSource, NewsSourceKind};
use actix_web::{HttpResponse, web};
use serde::Serialize;
use url::Url;

#[derive(Serialize)]
struct Response {
    sources: Vec<SupportedSource>,
}

#[derive(Serialize)]
struct SupportedSource {
    id: String,
    name: String,
    icon: Url,
}

#[tracing::instrument(name = "Querying supported sources", skip(settings))]
pub async fn supported_sources(settings: web::Data<Settings>) -> HttpResponse {
    let sources = vec![IRISH_TIMES, HACKER_NEWS, DOU]
        .into_iter()
        .map(|source| {
            let base_url = settings.app.base_url.as_str();
            let icon = format!("{}{}", base_url, source_icon(&source));

            SupportedSource {
                id: source.key.to_string(),
                name: source_name(&source).to_string(),
                icon: Url::parse(&icon).expect("Failed to parse icon URL"),
            }
        })
        .collect();

    HttpResponse::Ok().json(web::Json(Response { sources }))
}

fn source_name(source: &NewsSource) -> &'static str {
    match source.kind {
        NewsSourceKind::IrishTimes => "Irish Times",
        NewsSourceKind::HackerNews => "Hacker News",
        NewsSourceKind::Dou => "DOU",
    }
}

fn source_icon(source: &NewsSource) -> &'static str {
    match source.kind {
        NewsSourceKind::IrishTimes => "assets/icons/irishtimes.png",
        NewsSourceKind::HackerNews => "assets/icons/hackernews.png",
        NewsSourceKind::Dou => "assets/icons/dou.png",
    }
}
