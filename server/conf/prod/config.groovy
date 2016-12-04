appdir {
	root = '.'
	covers = 'covers'
	log = 'log'
	webapp = 'web'
	resources = 'resources' // this is only needed for the nocover.svg file
}

log {
	configfile = appdir.root + '/conf/logback.groovy'
}

// Heroku sets env var 'DATABASE_URL' which the app will use to derive the fields for the db connection
db {
//	url = ''
//	username = ''
//	password = ''
	showSql = false
}

server {
	port = 8000
	resource.base = appdir.webapp
	mimetypes = [
			"image/svg+xml svg",
			"image/png png"
	]
}

coverart {
	nocover = 'no-cover.svg'
}

auth {
//	admin {
//		username = 'admin'
//		password = 'changeme'
//	}
//	secretkey = 'changeme'
	disabled = false
}

aws {
	bucket = 'com-perrier-music'
//	accesskeyid = ''
//	secretaccesskey = ''
}