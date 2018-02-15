<?php
require __DIR__ . "/../vendor/autoload.php";
require __DIR__ . "/utils.php";

use Medoo\Medoo;

$input = json_decode(file_get_contents("php://input"), true);

// Authorization
if (!array_key_exists("device", $input) || !array_key_exists("name", $input["device"]) ||
	sha1("device_" . $input["device"]["name"]) !== getBasicToken()) {
	echo "Authorization not passed";
	exit;
}

$setup = json_decode(file_get_contents(__DIR__ . "/../setup.json"), true);

$db = new Medoo($setup);

$deviceName = $input["device"]["name"];

if (!$db->has("devices", [
		"name" => $deviceName
	])) {
	$db->insert("devices", [
		"name" => $deviceName
	]);
}

// Device online
$db->update("devices", [
	"online" => date("Y-m-d H:i:s", time())
], [
	"name" => $deviceName
]);
	
// Device monitoring
if (array_key_exists("info", $input["device"])) {
	$db->update("devices", $input["device"]["info"], [
		"name" => $deviceName
	]);
}

$output = array();

// SQL requests
if (array_key_exists("requests", $input) && is_array($input["requests"])) {
	foreach($input["requests"] as $request) {
		array_push($output, $db->query($request["query"], $request["params"])->fetchAll(PDO::FETCH_ASSOC));
	}
}

echo json_encode($output, JSON_UNESCAPED_UNICODE | JSON_NUMERIC_CHECK | JSON_UNESCAPED_SLASHES);
