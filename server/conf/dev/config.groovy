appdir {
	root = 'dev'
	covers = 'dev/covers'
	log = 'dev/log'
	webapp = 'web/app'
	resources = 'src/main/resources' // this is only needed for the nocover.svg file
}

log {
	configfile = 'conf/dev/logback.groovy'
}

db {
	url = 'jdbc:postgresql://localhost:5432/music-dev'
	username = 'sa'
	password = ''
	//schemas = ['PUBLIC', 'FT']
	showSql = false
}

server {
	port = 8080
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
	admin {
		username = 'admin'
		password = 'changeme'
	}
	secretkey = 'changeme'
	disabled = false
}

aws {
	bucket = 'com-perrier-music-dev'
	// TODO: When server needs creds, be sure to add these to run conig for intellij, don't check in!
	accesskeyid = ''
	secretaccesskey = ''
}