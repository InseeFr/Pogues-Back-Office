from connector import Connector
import os
import requests
import time
'''
Import local collections into eXist db
'''
def main(): 
    c = Connector('http://exist:8080', 'admin', '')
    c.create('orbeon/fr')
    for root, dirs, files in os.walk('/db'):
        for f in files:
            collection = root.split('db')[1][1:]
            document = os.path.join(root, f)
            print c.upload(document, collection)

if __name__ == '__main__':
    while True:
        try:
            requests.get('http://exist:8080')
            break
        except:
            time.sleep(5)
            continue
    main()
