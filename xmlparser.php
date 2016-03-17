$size = filesize("dblp.xml");
$psize=0;
$handle = fopen("dblp.xml","r");
header('Content-Type: text/html; charset=utf-8');
error_reporting(E_ERROR  | E_PARSE);
$count = 0;
$time = time();
$buffer = '';
$buffering = false;
$fails = 0;

$file = fopen("result.csv","w+");
fwrite("sep=;\n");
fwrite("key;mdate;author;title;year;journal");
echo "Progress :      ";
$lastwr = -1;
while($line = fgets($handle)){
    $proci = strlen($line);
    $psize+=$proci;
    $perc = ($psize/$size)*100;
    $perc = round($perc);
    if ($perc > $lastwr){
    echo "\033[5D";      // Move 5 characters backward
    echo str_pad($perc, 3, ' ', STR_PAD_LEFT) . " %";
    $lastwr = $perc;
    }
    if (!stristr($line,"http://"))
        $line = html_entity_decode($line);
    
        if (!$buffering){
            if (stristr($line,"
author)){
                        foreach($element->author as $o){
                            $at = $element->attributes();
                            $key = $at->key;
                            $mdate= $at->mdate;
                            $author=$o;
                            $title=$element->title;
                            $year=$element->year;
                            $journal=$element->journal;
                            writeline($file,array($key,$mdate,$author,$title,$year,$journal));
                        }
                        
                    }
                    else{
                    $at = $element->attributes();
                            $key = $at->key;
                            $mdate= $at->mdate;
                            $author=$element->author;
                            $title=$element->title;
                            $year=$element->year;
                            $journal=$element->journal;
                    writeline($file,array($key,$mdate,$author,$title,$year,$journal));
                    }
                }
                catch(Exception $e){
                    $fails++;
                }
                $buffer = $cont;
                if (strlen($cont)>3)
                    $buffering = true;
            }
            else
            {
                
                if (stristr($line,"http://")){
                    $line = str_replace('"',"",$line);
                    $line = str_replace('&','&',$line);
                    
                }
                $buffer .= $line;
            }
        }
    
}
fclose($file);

echo time()-$time.PHP_EOL.$fails.PHP_EOL;
function writeline($file,$values){
    $str = implode(';',$values);
    $str = trim($str);
    fwrite($file,$str."\n");
    
}