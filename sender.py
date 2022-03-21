import requests
import time
import random

url = "https://captiveportal-login.brocku.ca/auth/index.html/u"

names = ["sometimes", " ", "it", "just", " ", "do", "be", " ", "like", "that", "it ", "really ", "is ", "lol", " sometimes ", "you ", "just ", "gotta ", "be ", "like ", "that ", "eh ", " maybe ", "answer ", "a ", "few ", "more", " u w u "]
passw = ["you", "mom", "is", " ",  "a", "whore", " ", "cant possible be", " unfortunately ", "this ", "isnt my", "first", "uwu "]

for t in range(0, 10000):
	try:
		username = ""
		password = ""

		for i in range(0, len(names)):
			username = username + names[random.randint(0, len(names)-1)]

		for i in range(0, len(passw)):
			password = password + passw[random.randint(0, len(passw)-1)]

		reqt = requests.post(url, data = {'user': username, 'password': password})

		print(reqt.text)
		print(reqt.status_code)
		print(time.time())
		print(username)
		print(password)
		reqt.raise_for_status()
	except Exception as err:
		print(err)
