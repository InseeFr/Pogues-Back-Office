import requests
from requests.auth import HTTPBasicAuth
import sys
import os
from string import rfind
import base64

class XdbException(Exception):
    '''Exist db connector exception'''

class Connector:

    def __init__(self, url, user, password):
        self.url = url
        self.auth = HTTPBasicAuth(user, password)

    '''
    Create collection 
    '''
    def create(self, root, collection):
        print "creating collection %s in %s ..." % (collection, root)
        params = {
            '_query': 'xmldb:create-collection("%s","%s")'% (root, collection)
        }
        response = requests.get('%s/exist/rest/db'% (self.url), auth=self.auth, params=params)
        if 200 != response.status_code:
            raise XdbException
        return '%s/%s'%(root, collection)

    '''
    chmod resource
    Apply given permission on eXist-db resource,
    '''
    def chmod(self, resource, permissions):
        print "setting permissions %s on %s "% (permissions, resource)
        params = {
            '_query': 'sm:chmod(xs:anyURI("%s"), "%s")'% (resource, permissions)
        }
        response = requests.get('%s/exist/rest/db'% (self.url), auth=self.auth, params=params)
        if 200 != response.status_code:
            raise XdbException

    '''
    Put document to collection 
    Collection will be created if it does not exist
    '''
    def upload(self, fsPath, collection):
        print "storing from fs path %s to collection /%s ..." % (fsPath, collection)
        _, doc = os.path.split(fsPath)
        __, extension = os.path.splitext(doc)
        print 'extension, doc', extension, doc
        f = open(fsPath, 'r')
        xqm= f.read()
        f.close()
        content_types = {
            '.xqm': 'application/xquery',
			'.xq': 'application/xquery',
			'.xpl': 'application/xml',
			'.xquery': 'application/xquery',
            '.xml': 'application/xml',
			'.xconf': 'application/xml',
            '.xhtml': 'application/xml',
            '.xsl': 'application/xml'
        }
        headers = {
            'Content-Type': content_types[extension]
        }
        response = requests.put('%s/exist/rest/% s/%s'% (self.url, collection, doc), auth=self.auth, headers=headers, data=xqm)
        if 201 != response.status_code:
            print str(response)
            raise XdbException
        return '%s/%s' % (collection, doc)

    '''
    Execute a stored Xquery remotely 
    '''
    def execute(self, document):
        headers = {
            'Content-Type': 'application/xquery'
        }
        response = requests.get('%s/exist/rest/%s'% (self.url, document), auth=self.auth, headers=headers)
        if 200 != response.status_code:
            raise XdbException
        return response