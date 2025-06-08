use crate::configuration::Settings;
use actix_web::{HttpResponse, web};
use serde::Serialize;
use url::Url;

#[derive(Serialize)]
pub struct Response {
    sources: Vec<SupportedSource>,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct SupportedSource {
    pub id: String,
    pub name: String,
    pub image_url: String,
}

#[tracing::instrument(name = "Querying supported sources", skip(settings))]
pub async fn supported_sources(settings: web::Data<Settings>) -> HttpResponse {
    let base_url = &settings.app.base_url;
    let port = settings.app.port;
    let sources = vec![
        as_supported_source(
            base_url,
            settings.services.irish_times.key.as_str(),
            port,
            String::from("Irish Times"),
        ),
        as_supported_source(
            base_url,
            settings.services.hacker_news.key.as_str(),
            port,
            String::from("Hacker News"),
        ),
        as_supported_source(
            base_url,
            settings.services.dou.key.as_str(),
            port,
            String::from("Dou"),
        ),
    ];

    let response = web::Json(Response { sources });
    HttpResponse::Ok().json(response)
}

fn as_supported_source(base_url: &Url, key: &str, port: u16, name: String) -> SupportedSource {
    let mut url = base_url.clone();
    url.set_port(Some(port)).unwrap();
    url.set_path(format!("assets/icons/{}.png", key).as_str());

    SupportedSource {
        id: String::from(key),
        image_url: url.to_string(),
        name,
    }
}
