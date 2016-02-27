from xml.dom import minidom
import time
import datetime
xmldoc = minidom.parse('dblp.xml')
article = xmldoc.getElementsByTagName('article')

#print(len(article))

out_file = open("DBLP.csv","w")

data=unicode(None)
index=0
for i in article:
	author = i.getElementsByTagName('author')	
	title= i.getElementsByTagName('title')
	year= i.getElementsByTagName('year')
	journal= i.getElementsByTagName('journal')
	index2=0
	for j in author:
		data=data+unicode(author[index2].firstChild.nodeValue+"|")+unicode(article[index].attributes['key'].value+"|")+(unicode(article[index].attributes['mdate'].value+"|"))+(unicode(year[0].firstChild.nodeValue+"|"))+(unicode(title[0].firstChild.nodeValue+"|"))+(unicode(journal[0].firstChild.nodeValue+"|"))+('\n')
		index2+=1	
	index+=1
out_file.write(data)
out_file.close()

