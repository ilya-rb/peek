use crate::assets_loader::load_test_assets;
use crate::test_app::TestApp;
use serde_json::Value;
use sqlx::PgPool;

#[sqlx::test]
pub async fn supported_sources_returns_200(db_pool: PgPool) {
    let app = TestApp::new(db_pool).await;
    let client = reqwest::Client::new();

    let response = client
        .get(format!("{}/supported_sources", &app.app_url))
        .send()
        .await
        .expect("Failed to send request");

    assert_eq!(response.status(), 200);

    let body: Value = response.json().await.expect("Failed to parse JSON");
    let expected_data = load_test_assets("expected_sources.json");

    assert_eq!(body["sources"], expected_data["sources"]);
}
