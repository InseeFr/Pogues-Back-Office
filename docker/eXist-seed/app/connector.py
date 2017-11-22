import requests
from requests.auth import HTTPBasicAuth
import sys
from string import rfind
import base64

class Connector:

    def __init__(self, url, user, password):
        self.url = url
        self.auth = HTTPBasicAuth(user, password)

    '''
    Create collection 
    Apparently we need to put empty content to create an empty collection,
    which is not beautiful but just work
    '''
    def create(self, collection):
        headers = {
            'Content-Type': 'application/xquery'
        }
        response = requests.put('%s/exist/rest/%s'% (self.url, collection), auth=self.auth, headers=headers, data='')
        return response.status_code
   
    '''
    Put document to collection 
    Collection will be created if it does not exist
    '''
    def upload(self, document, collection):
        print "storing document %s to collection %s ..." % (document,collection)
        f = open(document, 'r')
        xqm= f.read()
        f.close()
        p = rfind(document, '/')
        if p > -1:
            doc = document[p+1:]
        else:
            doc = document
        headers = {
            'Content-Type': 'application/xquery'
        }
        response = requests.put('%s/exist/rest/%s/%s'% (self.url, collection, doc), auth=self.auth, headers=headers, data=xqm)
        return response.status_code
