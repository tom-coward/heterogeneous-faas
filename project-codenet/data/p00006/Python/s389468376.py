<?php



fscanf(STDIN, "%s", $data);


for($i = strlen($data); 0 <= $i; $i--){
$data2 .= $data[$i];
}

echo $data2."\n";