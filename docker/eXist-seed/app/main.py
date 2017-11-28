from connector import Connector
import os
import requests
import time
import getopt
import sys


class Contractor:
    def __init__(self, connector):
        self.connector = connector
    '''
    In a docker-compose context we need to ensure remote service is up
    before we go. So far this is what we've got ...
    '''
    def poll(self, host):
        attempts = 5
        while True:
            try:
                attempts -= 1
                requests.get(host)
                break
            except:
                if(attempts >= 0):
                    print 'Service not found, wait 5 sec. and try again'
                    time.sleep(5)
                    continue
                raise Exception('Max attempts exceded')

    '''
    Push local collections to eXist db
    '''  
    def push(self, fsRoot, xDbRoot): 
        for root, dirs, files in os.walk(fsRoot):
            print root, dirs, files
            print root.split(fsRoot)[1][1:]
            xDbCollection = '%s/%s'% (xDbRoot, root.split(fsRoot)[1][1:])
            print 'xdbcollection', xDbCollection
            for d in dirs:
                self.connector.chmod(self.connector.create(xDbCollection, d), 'rwxrwxrwx')
            for f in files:
                fsPath = os.path.join(root, f)
                self.connector.chmod(self.connector.upload(fsPath, xDbCollection), 'rwxrwxrwx')

'''
USAGE:
    All call to the eXist-db API has to be
    /db/system/security/exist/accounts
'''
if __name__ == '__main__':
    '''
    Default values may be overriden using command line arguments
    '''
    root='/opt/exist/db'
    collection = '/db'
    host='http://exist:8080'
    user='admin'
    password=''
    opts, args = getopt.getopt(sys.argv[1:],"r: h: u: p:")    
    for  opt, arg in opts:
        if opt in ('-r'):
            root=arg
        if opt in ('-h'):
            host=arg
        if opt in ('-u'):
            user=arg
        if opt in ('-p'):
            password=arg
    connector = Connector(host, user, password)
    contractor = Contractor(connector)
    contractor.poll(host)
    print 'Application starting'
    contractor.push(root, collection)
