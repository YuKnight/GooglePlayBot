<?php
function getAuthorizationHeaders() {
    $headers = null;
    if (isset($_SERVER["Authorization"])) {
        $headers = trim($_SERVER["Authorization"]);
    } else if (isset($_SERVER["HTTP_AUTHORIZATION"])) {
        $headers = trim($_SERVER["HTTP_AUTHORIZATION"]);
    } else if (function_exists("apache_request_headers")) {
        $requestHeaders = apache_request_headers();
        $requestHeaders = array_combine(array_map("ucwords",
            array_keys($requestHeaders)), array_values($requestHeaders));
        if (isset($requestHeaders["Authorization"])) {
            $headers = trim($requestHeaders["Authorization"]);
        }
    }
    return $headers;
}
function getBasicToken() {
    $headers = getAuthorizationHeaders();
    if (!empty($headers)) {
        if (preg_match("/Basic\s(\S+)/", $headers, $matches)) {
            return $matches[1];
        }
    }
    return null;
}