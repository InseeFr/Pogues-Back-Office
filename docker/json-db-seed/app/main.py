import requests
import time

class JSONException(Exception):
    '''Exist JSON connector exception'''

class Contractor:
    def __init__(self, host):
        self.host = host
    '''
    In a docker-compose context we need to ensure remote service is up
    before we go. So far this is what we've got ...
    '''
    def poll(self, host):
        attempts = 90
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
    Push local files to Pogues WS POST
    '''  
    def push(self, fsRoot): 
        for files in os.walk(fsRoot):
            for f in files:
                data_json = simplejson.dumps(f)
				payload = {'json_payload': data_json}
				 headers = {
            		'Content-Type': 'application/json'
        		}
				r = requests.post(self.host, headers=headers, data=payload)
        		if 201 != r.status_code:
            		print str(response)
           		 	raise JSONException

if __name__ == '__main__':
    root='/opt/json-seed/json'
    host='http://pogues-bo:8080/rmspogfo/pogues/persistence/questionnaires'
    contractor = Contractor(host)
    contractor.poll(host)
    print 'Application starting'
    contractor.push(root)
