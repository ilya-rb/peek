use crate::test_app::TestApp;
use sqlx::PgPool;

#[sqlx::test]
pub async fn get_news_returns_400_for_invalid_source(db_pool: PgPool) {
    let app = TestApp::new(db_pool).await;
    let client = reqwest::Client::new();

    let response = client
        .get(format!("{}/news?source=invalid_source", &app.app_url))
        .send()
        .await
        .expect("Failed to send request");

    assert_eq!(response.status(), 400);
}
