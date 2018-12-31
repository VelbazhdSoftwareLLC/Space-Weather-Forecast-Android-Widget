<?php
include("database.php");

/* Check input parmeter. */
if(isset($_GET['limit']) != 1) {
    $limit = 3;
} else {
    $limit = (int)$_GET['limit'];
}

/* At least one dete should be addressed. */
if($limit < 1) {
    $limit = 1;
}

/* Database connection object. */
$link = new mysqli(HOSTNAME, USERNAME, PASSWORD, DBNAME);

/* Select data. */
$result = $link->query("SELECT * FROM `forecast`, `state` WHERE forecast.state_id=state.id ORDER BY date DESC LIMIT " . $limit . ";");

/* Fetch data to array. */
for($j = 0; $j<$result->num_rows; $j++) {
    $result->data_seek($j);
    $row = $result->fetch_assoc();

    foreach ($row as $key => $value) {
		$values[ $j ][ $key ] = $value;
	}
}

/* Generate JSON result. */
echo( json_encode( array_values($values) ) );

$link->close();
?>