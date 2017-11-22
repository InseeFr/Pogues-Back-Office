from connector import Connector
import os
import requests
import time
import getopt
import sys

'''
In a docker-compose context we need to ensure remote service is up
before we go. So far this is what we've got ...
'''
def poll(host):
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
/!\ DEV ONLY /!\ 
We need to set the umask of admin to 000 
to allow recursive import of documents
'''
def configure(host, user, password):
    try:
        c = Connector(host, user, password)
        print c.upload('/opt/exist/system/security/exist/accounts/admin.xml', 'system/security/exist/accounts/admin.xml')
    except:
        print 'could not configure default permissions'

'''
Push local collections to eXist db
'''  
def push(host, user, password, root): 
    c = Connector(host, user, password)
    c.create('orbeon/fr/') # is it necessary (Non existing parent being created on push) ?
    for _root, dirs, files in os.walk(root):
        for f in files:
            collection = _root.split(root)[1][1:]
            document = os.path.join(_root, f)
            print c.upload(document, collection)
            print c.chmod(document, collection)

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
    poll(host)
    print 'Application starting'
    configure(host, user, password)
    push(host, user, password, root)
