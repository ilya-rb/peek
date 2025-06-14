use serde_json::Value;

pub fn load_test_assets(filename: &str) -> Value {
    let assets_path = std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("tests")
        .join("assets")
        .join(filename);

    let content = std::fs::read_to_string(assets_path)
        .unwrap_or_else(|_| panic!("Failed to read test asset: {}", filename));

    serde_json::from_str(&content)
        .unwrap_or_else(|_| panic!("Failed to parse JSON from test asset: {}", filename))
}
