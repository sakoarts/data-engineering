<?php
$size = filesize("dblp.xml");
$psize = 0;
$handle = fopen("dblp.xml", "r");
header('Content-Type: text/html; charset=utf-8');
error_reporting(E_ERROR | E_PARSE);
$count = 0;
$time = time();
$buffer = '';
$buffering = false;
$fails = 0;
$aid = 0;
$countD = 0;

$file = fopen("result.csv", "w+");
fwrite($file, "sep=;\n");
fwrite($file, "key;mdate;author;title;year;journal");
$file2 = fopen("edges.csv", "w+");
fwrite($file2, "sep=;\n");
$fauthors = fopen("authors.csv", "w+");
fwrite($fauthors, "sep=;\n");
echo "Progress :      ";
$lastwr = -1;
$trops = array();
while ($line = fgets($handle)) {
    $proci = strlen($line);
    $psize += $proci;
    $perc = ($psize / $size) * 100;
    $perc = round($perc);
    if ($perc > $lastwr) {
        echo "\033[5D";      // Move 5 characters backward
        echo str_pad($perc, 3, ' ', STR_PAD_LEFT) . " %";
        $lastwr = $perc;
    }
    if (!stristr($line, "http://"))
        $line = html_entity_decode($line);

    if (!$buffering) {
        if (stristr($line, "<article ")) {
            $buffering = true;
            $buffer .= $line;
        }
    } else {

        if (stristr($line, "</article")) {
            $buffering = false;
            $split = "";
            $cont = "";
            if (stristr($line, "<article")) {
                $split = substr($line, 0, strpos($line, "<article"));
                $cont = substr($line, strpos($line, "<article"));
            } else {
                $split = $line;
            }
            $buffer .= $split;
            try {
                $element = new SimpleXMLElement($buffer);
                foreach ($element->author as $o) {
                    $at = $element->attributes();
                    $key = $at->key;
                    $mdate = $at->mdate;
                    $author = $o;
                    $title = $element->title;
                    $year = $element->year;
                    $journal = $element->journal;
                    writeline($file, array($key, $mdate, $author, $title, $year, $journal));


                    foreach ($element->author as $p) {


                        if ($o != $p) {
                            writeline($file2, array($o, $p, $year, $mdate));
                        }

                        if (false) {
                            $similar = "";
                            $oid = 0;
                            $pid = 0;
                            $active = false;
                            fseek($fauthors, 0);
                            while (($row = fgetcsv($fauthors, 0, ';')) !== FALSE) {

                                //echo $row +".....";
                                if ($row[0] == $p) {
                                    $pid = $row[1];
                                    $active = true;
                                    break;
                                }
                                if ($row[0] == $o) {
                                    $oid = $row[1];
                                }
                                similar_text($p, $row[0], $perc);
                                if ($perc >= 75) {
                                    $countD++;
                                    $similar .= ($row[0] . " " . $perc . "%...");
                                }
                            }


                            if (!$active) {
                                //fseek($fh, 0, SEEK_END);
                                writeline($fauthors, array($p, $aid, $similar));
                                fflush($fauthors);

                                $pid = $aid;
                                $aid++;

                            }

                        }

                    }
                }

            } catch (Exception $e) {
                $fails++;

            }
            $buffer = $cont;
            if (strlen($cont) > 3)
                $buffering = true;
        } else {

            if (stristr($line, "http://")) {
                $line = str_replace('"', "", $line);
                $line = str_replace('&', '&amp;', $line);

            }
            $buffer .= $line;
        }
    }

}
fclose($file);
fclose($file2);
fclose($fauthors);
exec("gzip -c  result.csv > res.gz");
exec("gzip -c  edges.csv > res2.gz");
echo time() - $time . PHP_EOL . $fails . PHP_EOL;
echo "\n";
//echo aid;
echo "\n";
//echo countD;

function writeline($file, $values)
{
    $str = implode(';', $values);
    $str = trim($str);
    fwrite($file, $str . "\n");

}


?>